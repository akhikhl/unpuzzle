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

  private static File getLocalMavenRepositoryDir() {
    Paths.get(System.getProperty('user.home'), '.m2', 'repository').toFile()
  }

  private final Project project
  private final Config defaultConfig

  Configurer(Project project) {
    this.project = project
    this.defaultConfig = new ConfigReader().readFromResource('defaultConfig.groovy')
  }

  void apply() {

    if(!project.rootProject.extensions.findByName('unpuzzle')) {
      project.extensions.create('unpuzzle', Config)
      project.unpuzzle.parentConfig = defaultConfig
    }
    
    project.afterEvaluate {

      setupConfigChain(project)

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
    project.buildDir.mkdirs()
    def vconf = getEclipseVersionConfig()
    new EclipseDownloader().downloadAndUnpack(vconf.sources, project.buildDir)
  }

  EclipseVersionConfig getEclipseVersionConfig() {
    Config econf = project.unpuzzle.effectiveConfig
    EclipseVersionConfig vconf = econf.versionConfigs[econf.defaultEclipseVersion]
    if(!vconf)
      throw new GradleException("Eclipse version ${econf.defaultEclipseVersion} is not configured")
    return vconf
  }

  void installEclipse() {
    File repoDir = getLocalMavenRepositoryDir()
    def vconf = getEclipseVersionConfig()
    File groupDir = new File(repoDir, vconf.eclipseMavenGroup)
    if(!groupDir.exists()) {
      downloadEclipse()
      new EclipseDeployer(vconf.eclipseMavenGroup).deploy(vconf.sources, project.buildDir, new Deployer(repoDir))
    }
  }

  boolean installEclipseUpToDate() {
    File repoDir = getLocalMavenRepositoryDir()
    def vconf = getEclipseVersionConfig()
    File groupDir = new File(repoDir, vconf.eclipseMavenGroup)
    groupDir.exists()
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

  void uninstallEclipse() {
    File repoDir = getLocalMavenRepositoryDir()
    def vconf = getEclipseVersionConfig()
    File groupDir = new File(repoDir, vconf.eclipseMavenGroup)
    if(groupDir.exists())
      groupDir.deleteDir()
  }

  boolean uninstallEclipseUpToDate() {
    File repoDir = getLocalMavenRepositoryDir()
    def vconf = getEclipseVersionConfig()
    File groupDir = new File(repoDir, vconf.eclipseMavenGroup)
    !groupDir.exists()
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
    Deployer mavenDeployer = new Deployer(uploadEclipse.url, user: uploadEclipse.user, password: uploadEclipse.password)
    def vconf = getEclipseVersionConfig()
    new EclipseDeployer(vconf.eclipseMavenGroup).deploy(vconf.sources, project.buildDir, mavenDeployer)
  }
}
