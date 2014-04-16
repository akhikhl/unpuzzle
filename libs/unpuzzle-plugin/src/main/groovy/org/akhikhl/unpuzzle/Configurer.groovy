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

      setupConfigChain(project)

      Config econf = project.unpuzzle.effectiveConfig
      EclipseVersionConfig vconf = econf.versionConfigs[econf.defaultEclipseVersion]
      if(!vconf) {
        log.error 'Eclipse version {} is not configured', econf.defaultEclipseVersion
        return
      }

      project.task('downloadEclipse') {
        group = 'unpuzzle'
        description = 'Downloads eclipse distribution into project\'s buildDir'
        doLast {
          downloadEclipse()
        }
      }

      project.task('installEclipse') {
        group = 'unpuzzle'
        description = 'Installs mavenized artifacts of the eclipse distribution into local maven repository'
        dependsOn project.tasks.downloadEclipse
        doLast {
          installEclipse()
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

      if(!project.tasks.findByName('clean'))
        project.task('clean') {
          group = 'unpuzzle'
          description = 'Cleans buildDir'
          doLast {
            if(project.buildDir.exists())
              FileUtils.deleteDirectory(project.buildDir)
          }
        }
    } // project.afterEvaluate
  }

  void downloadEclipse() {
    File markerFile = new File(project.buildDir, 'eclipseDownloaded')
    if(!markerFile.exists()) {
      project.buildDir.mkdirs()
      new EclipseDownloader().downloadAndUnpack(vconf.sources, project.buildDir)
      markerFile.mkdirs()
      markerFile.text = new java.util.Date()
    }
  }

  void installEclipse() {
    File markerFile = new File(project.buildDir, 'eclipseArtifactsInstalled')
    if(!markerFile.exists()) {
      project.buildDir.mkdirs()
      def mavenDeployer = new Deployer(new File(System.getProperty('user.home'), '.m2/repository').toURI().toURL().toString())
      new EclipseDeployer(vconf.eclipseMavenGroup).deploy(vconf.sources, project.buildDir, mavenDeployer)
      markerFile.text = new java.util.Date()
    }
  }

  private void setupConfigChain(Project project) {
    if(project.unpuzzle.parentConfig == null) {
      Project p = project.parent
      while(p != null && !p.extensions.findByName('unpuzzle'))
        p = p.parent
      if(p == null)
        project.unpuzzle.parentConfig = defaultConfig
      else {
        project.unpuzzle.parentConfig = p.unpuzzle
        setupConfigChain(p)
      }
    }
  }

  void uploadEclipse() {
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
    new EclipseDeployer(vconf.eclipseMavenGroup).deploy(vconf.sources, project.buildDir, mavenDeployer)
  }
}
