/*
 * mavenize
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package org.akhikhl.mavenize.gradle.eclipse2mvn

import org.akhikhl.mavenize.eclipse2mvn.EclipseDownloader
import org.akhikhl.mavenize.eclipse2mvn.EclipseDeployer
import org.akhikhl.mavenize.eclipse2mvn.EclipseSource
import org.akhikhl.mavenize.osgi2mvn.Deployer
import org.gradle.api.*
import org.gradle.api.plugins.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.*

/**
 * Gradle plugin for mavenizing eclipse
 */
class Eclipse2MvnPlugin implements Plugin<Project> {

  void apply(Project project) {

    project.extensions.create('eclipse2mvn', Eclipse2MvnPluginExtension)

    project.afterEvaluate {

      project.task('downloadEclipse') {
        File markerFile = new File(project.buildDir, 'eclipseDownloaded')
        outputs.file markerFile
        doLast {
          if(!project.eclipse2mvn.sources)
            applyDefaultConfig(project)
          project.buildDir.mkdirs()
          new EclipseDownloader().downloadAndUnpack(project.eclipse2mvn.sources, project.buildDir)
          markerFile.text = new java.util.Date()
        }
      }

      project.task('installEclipse') {
        dependsOn project.tasks.downloadEclipse
        File outputMarkerFile = new File(project.buildDir, 'eclipseArtifactsInstalled')
        outputs.file outputMarkerFile
        doLast {
          Deployer mavenDeployer = new Deployer(new File(System.getProperty('user.home'), '.m2/repository').toURI().toURL().toString())
          new EclipseDeployer(project.eclipse2mvn.group).deploy(project.eclipse2mvn.sources, project.buildDir, mavenDeployer)
          outputMarkerFile.text = new java.util.Date()
        }
      }

      project.task('uploadEclipse') {
        dependsOn project.tasks.downloadEclipse
        doLast {
          def eclipseUpload
          if(project.eclipse2mvn.eclipseUpload)
            eclipseUpload = project.eclipse2mvn.eclipseUpload
          else if(project.ext.has('eclipseUpload'))
            eclipseUpload = project.ext.eclipseUpload
          else if(project.rootProject.ext.has('eclipseUpload'))
            eclipseUpload = project.rootProject.ext.eclipseUpload
          if(!eclipseUpload || !eclipseUpload.url || !eclipseUpload.user || !eclipseUpload.password) {
            System.err.println eclipseUpload
            System.err.println '''Could not upload eclipse: eclipseUpload properties not defined.
See Mavenize online documentation for more details:
https://github.com/akhikhl/mavenize/blob/master/README.md'''
            return
          }
          Deployer mavenDeployer = new Deployer(eclipseUpload.url, user: eclipseUpload.user, password: eclipseUpload.password)
          new EclipseDeployer(project.eclipse2mvn.group).deploy(project.eclipse2mvn.sources, project.buildDir, mavenDeployer)
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
    binding.eclipse2mvn = { Closure closure ->
      project.eclipse2mvn closure
    }
    GroovyShell shell = new GroovyShell(binding)
    this.getClass().getClassLoader().getResourceAsStream('eclipse-kepler.groovy').withReader('UTF-8') {
      shell.evaluate(it)
    }
  }
}
