/*
 * untangle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package org.akhikhl.untangle.gradle

import org.gradle.api.*
import org.gradle.api.plugins.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.*

import org.akhikhl.untangle.eclipse2maven.EclipseDownloader
import org.akhikhl.untangle.eclipse2maven.EclipseDeployer
import org.akhikhl.untangle.eclipse2maven.EclipseSource
import org.akhikhl.untangle.osgi2maven.Deployer

/**
 * Gradle plugin for mavenizing eclipse
 */
class UntanglePlugin implements Plugin<Project> {

  void apply(Project project) {

    project.extensions.create('untangle', UntanglePluginExtension)

    project.afterEvaluate {

      project.task('downloadEclipse') {
        File markerFile = new File(project.buildDir, 'eclipseDownloaded')
        outputs.file markerFile
        doLast {
          if(!project.untangle.sources)
            applyDefaultConfig(project)
          project.buildDir.mkdirs()
          new EclipseDownloader().downloadAndUnpack(project.untangle.sources, project.buildDir)
          markerFile.text = new java.util.Date()
        }
      }

      project.task('installEclipse') {
        dependsOn project.tasks.downloadEclipse
        File outputMarkerFile = new File(project.buildDir, 'eclipseArtifactsInstalled')
        outputs.file outputMarkerFile
        doLast {
          Deployer mavenDeployer = new Deployer(new File(System.getProperty('user.home'), '.m2/repository').toURI().toURL().toString())
          new EclipseDeployer(project.untangle.group).deploy(project.untangle.sources, project.buildDir, mavenDeployer)
          outputMarkerFile.text = new java.util.Date()
        }
      }

      project.task('uploadEclipse') {
        dependsOn project.tasks.downloadEclipse
        doLast {
          def uploadEclipse
          if(project.untangle.uploadEclipse)
            uploadEclipse = project.untangle.uploadEclipse
          else if(project.ext.has('uploadEclipse'))
            uploadEclipse = project.ext.uploadEclipse
          else if(project.rootProject.ext.has('uploadEclipse'))
            uploadEclipse = project.rootProject.ext.uploadEclipse
          if(!uploadEclipse || !uploadEclipse.url || !uploadEclipse.user || !uploadEclipse.password) {
            System.err.println uploadEclipse
            System.err.println 'Could not upload eclipse: uploadEclipse properties not defined.'
            System.err.println 'See Untangle online documentation for more details:'
            System.err.println 'https://github.com/akhikhl/untangle/blob/master/README.md'
            return
          }
          Deployer mavenDeployer = new Deployer(uploadEclipse.url, user: uploadEclipse.user, password: uploadEclipse.password)
          new EclipseDeployer(project.untangle.group).deploy(project.untangle.sources, project.buildDir, mavenDeployer)
        }
      }

      if(!project.tasks.findByName('clean'))
        project.task('clean') {
          doLast {
            if(project.buildDir.exists())
              FileUtils.deleteDirectory(project.buildDir);
          }
        }
    } // project.afterEvaluate
  } // apply

  private void applyDefaultConfig(Project project) {
    Binding binding = new Binding()
    binding.eclipse2maven = { Closure closure ->
      project.untangle closure
    }
    GroovyShell shell = new GroovyShell(binding)
    this.getClass().getClassLoader().getResourceAsStream('eclipse-kepler.groovy').withReader('UTF-8') {
      shell.evaluate(it)
    }
  }
}
