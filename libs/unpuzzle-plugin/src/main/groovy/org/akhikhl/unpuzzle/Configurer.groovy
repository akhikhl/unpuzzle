/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle

import org.apache.commons.io.FileUtils
import org.gradle.api.GradleException
import org.akhikhl.unpuzzle.eclipse2maven.EclipseDownloader
import java.nio.file.Path
import java.nio.file.Paths
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
  private final File localMavenRepositoryDir
  private final File unpuzzleDir

  Configurer(Project project) {
    this.project = project
    localMavenRepositoryDir = new File(System.getProperty('user.home'), '.m2/repository')
    unpuzzleDir = new File(System.getProperty('user.home'), '.unpuzzle')
  }

  void apply() {
    if(!project.extensions.findByName('unpuzzle'))
      project.extensions.create('unpuzzle', Config)

    setupConfigChain(project)

    project.afterEvaluate {

      project.task('downloadEclipse') {
        group = 'unpuzzle'
        description = 'Downloads eclipse distribution into $HOME/.unpuzzle directory'
        doLast {
          downloadEclipse()
        }
      }

      project.task('installEclipse') {
        group = 'unpuzzle'
        description = 'Mavenizes and installs bundles of the eclipse distribution into local maven repository'
        outputs.upToDateWhen {
          installEclipseUpToDate()
        }
        doLast {
          installEclipse()
        }
      }

      project.task('uninstallEclipse') {
        group = 'unpuzzle'
        description = 'Uninstalls mavenized artifacts of the eclipse distribution from local maven repository'
        outputs.upToDateWhen {
          uninstallEclipseUpToDate()
        }
        doLast {
          uninstallEclipse()
        }
      }

      project.task('uploadEclipse') {
        group = 'unpuzzle'
        description = 'Uploads mavenized artifacts of the eclipse distribution to remote maven repository'
        dependsOn project.tasks.downloadEclipse
        doLast {
          uploadEclipse()
        }
      }

      project.task('cleanUnpuzzle') {
        group = 'unpuzzle'
        description = 'Cleans $HOME/.unpuzzle directory, uninstalls mavenized artifacts'
        dependsOn project.tasks.uninstallEclipse
        outputs.upToDateWhen {
          !unpuzzleDir.exists()
        }
        doLast {
          if(unpuzzleDir.exists())
            unpuzzleDir.deleteDir()
        }
      }
    } // project.afterEvaluate
  }

  void downloadEclipse() {
    def vconf = getSelectedVersionConfig()
    new EclipseDownloader().downloadAndUnpack(vconf.sources, unpuzzleDir)
  }

  private EclipseVersionConfig getSelectedVersionConfig() {
    Config econf = project.unpuzzle.effectiveConfig
    EclipseVersionConfig vconf = econf.selectedVersionConfig
    if(!vconf)
      throw new GradleException("Eclipse version ${econf.selectedEclipseVersion} is not configured")
    return vconf
  }

  void installEclipse() {
    def vconf = getSelectedVersionConfig()
    def mavenDeployer = new Deployer(localMavenRepositoryDir)
    def eclipseDeployer = new EclipseDeployer(unpuzzleDir, vconf.eclipseMavenGroup, mavenDeployer)
    if(!eclipseDeployer.allDownloadedPackagesAreInstalled(vconf.sources)) {
      downloadEclipse()
      eclipseDeployer.deploy(vconf.sources)
    }
  }

  boolean installEclipseUpToDate() {
    def vconf = getSelectedVersionConfig()
    def mavenDeployer = new Deployer(localMavenRepositoryDir)
    new EclipseDeployer(unpuzzleDir, vconf.eclipseMavenGroup, mavenDeployer).allDownloadedPackagesAreInstalled(vconf.sources)
  }

  private static void setupConfigChain(Project project) {
    if(project.unpuzzle.parentConfig == null) {
      Project p = project.parent
      while(p != null && !p.extensions.findByName('unpuzzle'))
        p = p.parent
      if(p == null) {
        log.debug '{}.unpuzzle.parentConfig <- defaultConfig', project.name
        project.unpuzzle.parentConfig = new ConfigReader().readFromResource('defaultConfig.groovy')
      }
      else {
        log.debug '{}.unpuzzle.parentConfig <- {}.unpuzzle', project.name, p.name
        project.unpuzzle.parentConfig = p.unpuzzle
        setupConfigChain(p)
      }
    } else
      log.debug '{}.unpuzzle already has parentConfig, setupConfigChain skipped', project.name
  }

  void uninstallEclipse() {
    def vconf = getSelectedVersionConfig()
    def mavenDeployer = new Deployer(localMavenRepositoryDir)
    def eclipseDeployer = new EclipseDeployer(unpuzzleDir, vconf.eclipseMavenGroup, mavenDeployer)
    if(!eclipseDeployer.allDownloadedPackagesAreUninstalled(vconf.sources))
      eclipseDeployer.uninstall(vconf.sources)
  }

  boolean uninstallEclipseUpToDate() {
    def vconf = getSelectedVersionConfig()
    def mavenDeployer = new Deployer(localMavenRepositoryDir)
    def eclipseDeployer = new EclipseDeployer(unpuzzleDir, vconf.eclipseMavenGroup, mavenDeployer)
    eclipseDeployer.allDownloadedPackagesAreUninstalled(vconf.sources)
  }

  void uploadEclipse() {
    def uploadEclipse = [:]
    if(project.hasProperty('uploadEclipse'))
      uploadEclipse << project.uploadEclipse
    uploadEclipse << project.unpuzzle.effectiveConfig.uploadEclipse
    if(!uploadEclipse || !uploadEclipse.url || !uploadEclipse.user || !uploadEclipse.password) {
      System.err.println uploadEclipse
      System.err.println 'Could not upload eclipse: uploadEclipse properties not defined.'
      System.err.println 'See Unpuzzle online documentation for more details:'
      System.err.println 'https://github.com/akhikhl/unpuzzle/blob/master/README.md'
      return
    }
    def vconf = getSelectedVersionConfig()
    Deployer mavenDeployer = new Deployer(uploadEclipse.url, user: uploadEclipse.user, password: uploadEclipse.password)
    def eclipseDeployer = new EclipseDeployer(unpuzzleDir, vconf.eclipseMavenGroup, mavenDeployer)
    eclipseDeployer.deploy(vconf.sources)
  }
}
