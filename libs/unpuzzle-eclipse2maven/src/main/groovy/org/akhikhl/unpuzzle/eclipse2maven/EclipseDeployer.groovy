/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle.eclipse2maven

import org.apache.commons.codec.digest.DigestUtils

import org.akhikhl.unpuzzle.utils.IConsole
import org.akhikhl.unpuzzle.utils.SysConsole
import org.akhikhl.unpuzzle.osgi2maven.Pom
import org.akhikhl.unpuzzle.osgi2maven.Bundle2Pom
import org.akhikhl.unpuzzle.osgi2maven.DependencyBundle
import org.akhikhl.unpuzzle.osgi2maven.Deployer

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Deploys eclipse plugins to maven.
 * @author akhikhl
 */
final class EclipseDeployer {

  protected static final Logger log = LoggerFactory.getLogger(EclipseDeployer)

  private final File targetDir
  private final String eclipseGroup
  private final Deployer mavenDeployer
  private final IConsole console
  private final String installGroupPath
  private final String installGroupChecksum
  private Map artifacts = [:]
  private Map artifactsNl = [:]
  private Map artifactFiles = [:]
  private Map sourceFiles = [:]

  EclipseDeployer(File targetDir, String eclipseGroup, Deployer mavenDeployer, IConsole console = null) {
    this.targetDir = targetDir
    this.eclipseGroup = eclipseGroup
    this.mavenDeployer = mavenDeployer
    this.console = console ?: new SysConsole()
    installGroupPath = mavenDeployer.repositoryUrl.toString() + '/' + (eclipseGroup ? eclipseGroup.replace('.', '/') : '')
    installGroupChecksum = DigestUtils.md5Hex(installGroupPath)
  }

  boolean allDownloadedPackagesAreInstalled(List<EclipseSource> sources) {
    if(mavenDeployer.repositoryUrl.protocol == 'file') {
      for(EclipseSource source in sources) {
        String url = source.url
        String fileName = url.substring(url.lastIndexOf('/') + 1)
        String downloadedChecksum
        File downloadedChecksumFile = new File(targetDir, "downloaded-checksums/${fileName}.md5")
        if(downloadedChecksumFile.exists())
          downloadedChecksum = downloadedChecksumFile.text
        if(downloadedChecksum == null) {
          log.info 'allDownloadedPackagesAreInstalled, url={}, downloadedChecksum={}, returning false', url, downloadedChecksum
          return false
        }
        String installedChecksum
        File installedChecksumFile = new File(targetDir, "installed-checksums/${installGroupChecksum}/${fileName}.md5")
        if(installedChecksumFile.exists())
          installedChecksum = installedChecksumFile.text
        if(downloadedChecksum != installedChecksum) {
          log.info 'allDownloadedPackagesAreInstalled, url={}, downloadedChecksum={}, installedChecksum={}, returning false', url, downloadedChecksum, installedChecksum
          return false
        }
      }
      log.info 'allDownloadedPackagesAreInstalled, repository={}, all checksums match, returning true', mavenDeployer.repositoryUrl
      return true
    }
    log.info 'allDownloadedPackagesAreInstalled, repository={}, non-file protocol, returning false', mavenDeployer.repositoryUrl
    return false
  }

  boolean allDownloadedPackagesAreUninstalled(List<EclipseSource> sources) {
    if(mavenDeployer.repositoryUrl.protocol == 'file') {
      for(EclipseSource source in sources) {
        String url = source.url
        String fileName = url.substring(url.lastIndexOf('/') + 1)
        String downloadedChecksum
        File downloadedChecksumFile = new File(targetDir, "downloaded-checksums/${fileName}.md5")
        if(downloadedChecksumFile.exists())
          downloadedChecksum = downloadedChecksumFile.text
        String installedChecksum
        File installedChecksumFile = new File(targetDir, "installed-checksums/${installGroupChecksum}/${fileName}.md5")
        if(installedChecksumFile.exists())
          installedChecksum = installedChecksumFile.text
        if(downloadedChecksum != null && downloadedChecksum == installedChecksum) {
          log.info 'allDownloadedPackagesAreUninstalled, url={}, checksum match, returning false', url
          return false
        }
      }
      log.info 'allDownloadedPackagesAreUninstalled, repository={}, all checksums removed, returning true', mavenDeployer.repositoryUrl
      return true
    }
    log.info 'allDownloadedPackagesAreUninstalled, repository={}, non-file protocol, returning false', mavenDeployer.repositoryUrl
    return false
  }

  private void collectArtifactsInFolder(EclipseSource source, artifactsSourceDir) {
    def processFile = { File file ->
      console.info("Collecting artifacts: ${file.name}")
      Bundle2Pom reader = new Bundle2Pom(group: eclipseGroup, dependencyGroup: eclipseGroup)
      Pom pom = reader.convert(file)
      def source_match = pom.artifact =~ /(.*)\.source/
      if(source_match) {
        def artifact = source_match[0][1]
        sourceFiles["${artifact}:${pom.version}"] = file
      } else if(!source.sourcesOnly) {
        def nl_match = pom.artifact =~ /(.*)\.nl_(.*)/
        if(nl_match) {
          def artifact = nl_match[0][1]
          def language = nl_match[0][2]
          if(!artifactsNl[language])
            artifactsNl[language] = [:]
          artifactsNl[language][artifact] = pom
        } else if(!source.languagePacksOnly) {
          if(!artifacts.containsKey(pom.artifact))
            artifacts[pom.artifact] = []
          artifacts[pom.artifact].add pom
        }
        artifactFiles["${pom.artifact}:${pom.version}"] = file
      }
    }
    console.startProgress("Reading bundles in $artifactsSourceDir")
    try {
      artifactsSourceDir.eachDir processFile
      artifactsSourceDir.eachFileMatch ~/.*\.jar/, processFile
    } finally {
      console.endProgress()
    }
  }

  void deploy(List<EclipseSource> sources) {
    
    File installedCheckumsInfoFile = new File(targetDir, "installed-checksums/${installGroupChecksum}/info.txt")
    installedCheckumsInfoFile.parentFile.mkdirs()
    installedCheckumsInfoFile.text = """eclipseGroup=$eclipseGroup
installGroupPath=$installGroupPath"""

    for(EclipseSource source in sources) {
      String url = source.url
      String fileName = url.substring(url.lastIndexOf('/') + 1)
      File unpackDir = new File(targetDir, "unpacked/${Utils.getArchiveNameNoExt(fileName)}")
      boolean packageInstalled = false
      if(mavenDeployer.repositoryUrl.protocol == 'file') {
        String downloadedChecksum
        File downloadedChecksumFile = new File(targetDir, "downloaded-checksums/${fileName}.md5")
        if(downloadedChecksumFile.exists())
          downloadedChecksum = downloadedChecksumFile.text
        String installedChecksum
        File installedChecksumFile = new File(targetDir, "installed-checksums/${installGroupChecksum}/${fileName}.md5")
        if(installedChecksumFile.exists())
          installedChecksum = installedChecksumFile.text
        packageInstalled = downloadedChecksum == installedChecksum
      }
      if(!packageInstalled)
        collectArtifactsInFolder(source, new File(unpackDir, 'plugins'))
    }

    fixDependencies()

    console.startProgress('Deploying artifacts')
    try {
      artifacts.each { name, artifactVersions ->
        artifactVersions.each { pom ->
          mavenDeployer.deployBundle pom, artifactFiles["${pom.artifact}:${pom.version}"], sourceFile: sourceFiles["${pom.artifact}:${pom.version}"]
        }
      }
      artifactsNl.each { language, map_nl ->
        map_nl.each { artifactName, pom ->
          mavenDeployer.deployBundle pom, artifactFiles["${pom.artifact}:${pom.version}"]
        }
      }
    } finally {
      console.endProgress()
    }

    if(mavenDeployer.repositoryUrl.protocol == 'file')
      for(EclipseSource source in sources) {
        String url = source.url
        String fileName = url.substring(url.lastIndexOf('/') + 1)
        File downloadedChecksumFile = new File(targetDir, "downloaded-checksums/${fileName}.md5")
        File installedChecksumFile = new File(targetDir, "installed-checksums/${installGroupChecksum}/${fileName}.md5")
        installedChecksumFile.parentFile.mkdirs()
        installedChecksumFile.text = downloadedChecksumFile.text
      }
  }

  private void fixDependencies() {
    console.startProgress('Fixing dependencies')
    try {
      artifacts.each { name, artifactVersions ->
        console.info("Fixing dependencies: $name")
        artifactVersions.each { pom ->
          pom.dependencyBundles.removeAll { reqBundle ->
            if(!artifacts[reqBundle.name.trim()]) {
              console.info("Warning: artifact dependency $pom.group:$pom.artifact:$pom.version -> $reqBundle.name could not be resolved.")
              return true
            }
            return false
          }
          pom.dependencyBundles.each { reqBundle ->
            def resolvedVersions = artifacts[reqBundle.name.trim()]
            if(resolvedVersions.size() == 1)
              reqBundle.version = resolvedVersions[0].version
            else if(!resolvedVersions.find { it -> it.version == reqBundle.version.trim() }) {
              def compare = { a, b -> new Version(a).compare(new Version(b)) }
              resolvedVersions = resolvedVersions.sort(compare)
              int i = Collections.binarySearch resolvedVersions, reqBundle.version.trim(), compare as java.util.Comparator
              if(i < 0)
                i = -i - 1
              if(i > resolvedVersions.size() - 1)
                i = resolvedVersions.size() - 1
              def c = resolvedVersions[i]
              def depsStr = resolvedVersions.collect({ p -> "$p.group:$p.artifact:$p.version" }).join(', ')
              console.info("Warning: resolved ambiguous dependency: $pom.group:$pom.artifact:$pom.version -> $reqBundle.name:$reqBundle.version, chosen $c.group:$c.artifact:$c.version from [$depsStr].")
              reqBundle.version = c.version
            }
          }
          artifactsNl.each { language, map_nl ->
            def pom_nl = map_nl[pom.artifact]
            if(pom_nl)
              pom.dependencyBundles.each { dep_bundle ->
                def dep_pom_nl = map_nl[dep_bundle.name]
                if(dep_pom_nl) {
                  pom_nl.dependencyBundles.add new DependencyBundle(name: dep_pom_nl.artifact, version: dep_pom_nl.version, visibility: dep_bundle.visibility, resolution: dep_bundle.resolution)
                }
              }
          }
        }
      }
    } finally {
      console.endProgress()
    }
  }

  void uninstall(List<EclipseSource> sources) {

    if(mavenDeployer.repositoryUrl.protocol != 'file') {
      console.progressError("Could not uninstall from non-file URL: ${mavenDeployer.repositoryUrl}")
      return
    }

    File repositoryDir = new File(mavenDeployer.repositoryUrl.toURI())

    for(EclipseSource source in sources) {
      String url = source.url
      String fileName = url.substring(url.lastIndexOf('/') + 1)
      File unpackDir = new File(targetDir, "unpacked/${Utils.getArchiveNameNoExt(fileName)}")
      boolean packageInstalled = false
      if(mavenDeployer.repositoryUrl.protocol == 'file') {
        String downloadedChecksum
        File downloadedChecksumFile = new File(targetDir, "downloaded-checksums/${fileName}.md5")
        if(downloadedChecksumFile.exists())
          downloadedChecksum = downloadedChecksumFile.text
        String installedChecksum
        File installedChecksumFile = new File(targetDir, "installed-checksums/${installGroupChecksum}/${fileName}.md5")
        if(installedChecksumFile.exists())
          installedChecksum = installedChecksumFile.text
        packageInstalled = downloadedChecksum == installedChecksum
      }
      if(packageInstalled)
        collectArtifactsInFolder(source, new File(unpackDir, 'plugins'))
    }

    fixDependencies()

    def deleteArtifactDir = { pom ->
      File artifactDir = new File(repositoryDir, "${eclipseGroup}/${pom.artifact}")
      if(artifactDir.exists())
        artifactDir.deleteDir()
    }

    console.startProgress('Uninstalling artifacts')
    try {
      artifacts.each { name, artifactVersions ->
        artifactVersions.each deleteArtifactDir
      }
      artifactsNl.each { language, map_nl ->
        map_nl.each { artifactName, pom ->
          deleteArtifactDir(pom)
        }
      }
    } finally {
      console.endProgress()
    }

    if(mavenDeployer.repositoryUrl.protocol == 'file')
      for(EclipseSource source in sources) {
        String url = source.url
        String fileName = url.substring(url.lastIndexOf('/') + 1)
        File installedChecksumFile = new File(targetDir, "installed-checksums/${installGroupChecksum}/${fileName}.md5")
        installedChecksumFile.delete()
      }
  }
}
