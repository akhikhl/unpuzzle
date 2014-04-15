/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle

import org.apache.commons.io.FileUtils
import org.akhikhl.unpuzzle.eclipse2maven.EclipseDownloader
import org.akhikhl.unpuzzle.eclipse2maven.EclipseDeployer
import org.akhikhl.unpuzzle.eclipse2maven.EclipseSource
import org.akhikhl.unpuzzle.osgi2maven.Deployer
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author akhikhl
 */
class Configurer {

  protected static final Logger log = LoggerFactory.getLogger(Configurer)

  private final Project project
  private final Config defaultConfig

  Configurer(Project project) {
    this.project = project
    this.defaultConfig = new ConfigReader().readFromResource('defaultConfig.groovy')
  }

  void apply() {

    project.extensions.create('unpuzzle', Config)

    project.afterEvaluate {

      String eclipseVersion
      if(project.hasProperty('eclipseVersion'))
        eclipseVersion = project.eclipseVersion
      else {
        eclipseVersion = ProjectUtils.findResultUpAncestorChain(project, { it.extensions.findByName('unpuzzle')?.defaultEclipseVersion })
        if(eclipseVersion == null)
          eclipseVersion = defaultConfig.defaultEclipseVersion
      }

      project.unpuzzle.defaultEclipseVersion = eclipseVersion

      List versions = []
      applyToConfigs { Config config ->
        versions.addAll(config.versionConfigs.keySet())
      }
      log.warn 'DBG versions={}', versions

      Map version2EclipseMavenGroup = [:]
      Map version2EclipseMirror = [:]

      versions.each { version ->
        version2EclipseMavenGroup[version] = findInConfigs { it.versionConfigs.get(version)?.eclipseMavenGroup }
        version2EclipseMirror[version] = findInConfigs { it.versionConfigs.get(version)?.eclipseMirror }
      }
      log.warn 'DBG version2EclipseMavenGroup={}', version2EclipseMavenGroup
      log.warn 'DBG version2EclipseMirror={}', version2EclipseMirror

      applyToConfigs { Config config ->
        log.warn 'DBG checking config, versionConfigs={}', config.versionConfigs
        config.versionConfigs.each { version, versionConfig ->
          log.warn 'DBG checking versionConfig.eclipseMavenGroup={}', versionConfig.eclipseMavenGroup
          if(!versionConfig.eclipseMavenGroup) {
            log.warn 'DBG assigning versionConfig.eclipseMavenGroup={} <- {}', versionConfig.eclipseMavenGroup, version2EclipseMavenGroup[version]
            versionConfig.eclipseMavenGroup = version2EclipseMavenGroup[version]
          }
          if(!versionConfig.eclipseMirror)
            versionConfig.eclipseMirror = version2EclipseMirror[version]
        }
      }

      if(!findInConfigs { it.versionConfigs[eclipseVersion] }) {
        log.error 'Eclipse version {} is not configured', eclipseVersion
        return
      }

      project.task('downloadEclipse') {
        File markerFile = new File(project.buildDir, 'eclipseDownloaded')
        outputs.file markerFile
        doLast {
          project.buildDir.mkdirs()
          applyToConfigs { Config config ->
            EclipseVersionConfig versionConfig = config.versionConfigs[eclipseVersion]
            if(versionConfig) {
              log.warn 'DBG versionConfig.eclipseMavenGroup={}, versionConfig.eclipseMirror={}', versionConfig.eclipseMavenGroup, versionConfig.eclipseMirror
              new EclipseDownloader().downloadAndUnpack(versionConfig.sources, project.buildDir)
            }
          }
          markerFile.text = new java.util.Date()
        }
      }

      project.task('installEclipse') {
        dependsOn project.tasks.downloadEclipse
        File outputMarkerFile = new File(project.buildDir, 'eclipseArtifactsInstalled')
        outputs.file outputMarkerFile
        doLast {
          def mavenDeployer = new Deployer(new File(System.getProperty('user.home'), '.m2/repository').toURI().toURL().toString())
          applyToConfigs { Config config ->
            EclipseVersionConfig versionConfig = config.versionConfigs[eclipseVersion]
            if(versionConfig)
              new EclipseDeployer(versionConfig.eclipseMavenGroup).deploy(versionConfig.sources, project.buildDir, mavenDeployer)
          }
          outputMarkerFile.text = new java.util.Date()
        }
      }

      project.task('uploadEclipse') {
        dependsOn project.tasks.downloadEclipse
        doLast {
          def uploadEclipse
          if(project.unpuzzle.uploadEclipse)
            uploadEclipse = project.unpuzzle.uploadEclipse
          else if(project.hasProperty('uploadEclipse'))
            uploadEclipse = project.uploadEclipse
          if(!uploadEclipse || !uploadEclipse.url || !uploadEclipse.user || !uploadEclipse.password) {
            System.err.println uploadEclipse
            System.err.println 'Could not upload eclipse: uploadEclipse properties not defined.'
            System.err.println 'See Unpuzzle online documentation for more details:'
            System.err.println 'https://github.com/akhikhl/unpuzzle/blob/master/README.md'
            return
          }
          Deployer mavenDeployer = new Deployer(uploadEclipse.url, user: uploadEclipse.user, password: uploadEclipse.password)
          applyToConfigs { Config config ->
            EclipseVersionConfig versionConfig = config.versionConfigs[eclipseVersion]
            if(versionConfig)
              new EclipseDeployer(versionConfig.eclipseMavenGroup).deploy(versionConfig.sources, project.buildDir, mavenDeployer)
          }
        }
      }

      if(!project.tasks.findByName('clean'))
        project.task('clean') {
          doLast {
            if(project.buildDir.exists())
              FileUtils.deleteDirectory(project.buildDir)
          }
        }
    } // project.afterEvaluate
  }

  protected final void applyToConfigs(Closure closure) {

    closure(defaultConfig)

    ProjectUtils.collectWithAllAncestors(project).each { Project p ->
      Config config = p.extensions.findByName('unpuzzle')
      if(config)
        closure(config)
    }
  }

  protected final findInConfigs(Closure closure) {

    def result = ProjectUtils.findResultUpAncestorChain(project, { Project p ->
      Config config = p.extensions.findByName('unpuzzle')
      config ? closure(config) : null
    })

    if(result == null)
      result = closure(defaultConfig)

    return result
  }
}

