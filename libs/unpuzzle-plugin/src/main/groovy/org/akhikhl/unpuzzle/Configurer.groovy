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

  Configurer(Project project) {
    this.project = project
  }

  void apply() {
    if(!project.extensions.findByName('unpuzzle'))
      project.extensions.create('unpuzzle', Config)

    def self = this

    project.metaClass {

      getEffectiveUnpuzzle = {
        self.getEffectiveConfig()
      }
    }

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
          !effectiveConfig.unpuzzleDir.exists()
        }
        doLast {
          if(effectiveConfig.unpuzzleDir.exists())
            effectiveConfig.unpuzzleDir.deleteDir()
        }
      }
    } // project.afterEvaluate
  }

  void downloadEclipse() {
    def vconf = getSelectedVersionConfig()
    new EclipseDownloader().downloadAndUnpack(vconf.sources, effectiveConfig.unpuzzleDir)
  }

  final Config getEffectiveConfig() {
    if(!project.ext.has('_effectiveUnpuzzle')) {
      Config econfig = new Config()
      Config.merge(econfig, project.unpuzzle)
      project.ext._effectiveUnpuzzle = econfig
    }
    return project._effectiveUnpuzzle
  }

  private EclipseVersionConfig getSelectedVersionConfig() {
    EclipseVersionConfig vconf = effectiveConfig.selectedVersionConfig
    if(!vconf)
      throw new GradleException("Eclipse version ${effectiveConfig.selectedEclipseVersion} is not configured")
    return vconf
  }

  void installEclipse() {
    def vconf = getSelectedVersionConfig()
    if(effectiveConfig.dryRun) {
      log.warn 'installEclipse: unpuzzle.dryRun=true, no work done'
      return
    }
    def mavenDeployer = new Deployer(effectiveConfig.localMavenRepositoryDir)
    def eclipseDeployer = new EclipseDeployer(effectiveConfig.unpuzzleDir, vconf.eclipseMavenGroup, mavenDeployer)
    if(!eclipseDeployer.allDownloadedPackagesAreInstalled(vconf.sources)) {
      downloadEclipse()
      log.warn 'Installing eclipse version {} to maven-repo {}, maven-group {}', effectiveConfig.selectedEclipseVersion, effectiveConfig.localMavenRepositoryDir.toURI().toString(), vconf.eclipseMavenGroup
      eclipseDeployer.deploy(vconf.sources)
    }
  }

  boolean installEclipseUpToDate() {
    def vconf = getSelectedVersionConfig()
    if(effectiveConfig.dryRun)
      return false
    def mavenDeployer = new Deployer(effectiveConfig.localMavenRepositoryDir)
    new EclipseDeployer(effectiveConfig.unpuzzleDir, vconf.eclipseMavenGroup, mavenDeployer).allDownloadedPackagesAreInstalled(vconf.sources)
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
    if(effectiveConfig.dryRun) {
      log.warn 'uninstallEclipse: unpuzzle.dryRun=true, no work done'
      return
    }
    def mavenDeployer = new Deployer(effectiveConfig.localMavenRepositoryDir)
    def eclipseDeployer = new EclipseDeployer(effectiveConfig.unpuzzleDir, vconf.eclipseMavenGroup, mavenDeployer)
    if(!eclipseDeployer.allDownloadedPackagesAreUninstalled(vconf.sources)) {
      log.warn 'Uninstalling eclipse version {} from maven-repo {}, maven-group {}', effectiveConfig.selectedEclipseVersion, effectiveConfig.localMavenRepositoryDir.toURI().toString(), vconf.eclipseMavenGroup
      eclipseDeployer.uninstall(vconf.sources)
    }
  }

  boolean uninstallEclipseUpToDate() {
    def vconf = getSelectedVersionConfig()
    if(effectiveConfig.dryRun)
      return false
    def mavenDeployer = new Deployer(effectiveConfig.localMavenRepositoryDir)
    def eclipseDeployer = new EclipseDeployer(effectiveConfig.unpuzzleDir, vconf.eclipseMavenGroup, mavenDeployer)
    eclipseDeployer.allDownloadedPackagesAreUninstalled(vconf.sources)
  }

  void uploadEclipse() {
    def uploadEclipse = [:]
    if(project.hasProperty('uploadEclipse'))
      uploadEclipse << project.uploadEclipse
    uploadEclipse << effectiveConfig.uploadEclipse
    if(!uploadEclipse || !uploadEclipse.url || !uploadEclipse.user || !uploadEclipse.password) {
      log.error 'Could not upload eclipse: uploadEclipse properties not defined.'
      log.error 'See Unpuzzle online documentation for more details:'
      log.error 'https://github.com/akhikhl/unpuzzle/blob/master/README.md'
      return
    }
    def vconf = getSelectedVersionConfig()
    if(effectiveConfig.dryRun) {
      log.warn 'uploadEclipse: unpuzzle.dryRun=true, no work done'
      return
    }
    log.warn 'Deploying eclipse version {} to maven-repo {}, maven-group {}', effectiveConfig.selectedEclipseVersion, uploadEclipse.url, vconf.eclipseMavenGroup
    Deployer mavenDeployer = new Deployer(uploadEclipse.url, user: uploadEclipse.user, password: uploadEclipse.password)
    def eclipseDeployer = new EclipseDeployer(effectiveConfig.unpuzzleDir, vconf.eclipseMavenGroup, mavenDeployer)
    eclipseDeployer.deploy(vconf.sources)
  }
}
