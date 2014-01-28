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
          project.buildDir.mkdirs()
          new EclipseDownloader().downloadAndUnpack(project.eclipse2mvn.eclipseSources, project.buildDir)
          markerFile.text = new java.util.Date()
        }
      }

      project.task('installEclipse') {
        dependsOn project.tasks.downloadEclipse
        File outputMarkerFile = new File(project.buildDir, 'eclipseArtifactsInstalled')
        outputs.file outputMarkerFile
        doLast {
          Deployer mavenDeployer = new Deployer(new File(System.getProperty('user.home'), '.m2/repository').toURI().toURL().toString())
          new EclipseDeployer(project.eclipse2mvn.eclipseGroup).deploy(project.eclipse2mvn.eclipseSources, project.buildDir, mavenDeployer)
          outputMarkerFile.text = new java.util.Date()
        }
      }

      project.task('uploadEclipse') {
        dependsOn project.tasks.downloadEclipse
        doLast {
          def corporateDeployment = rootProject.ext.corporateDeployment
          Deployer mavenDeployer = new Deployer(corporateDeployment.url, user: corporateDeployment.user, password: corporateDeployment.password)
          new EclipseDeployer(project.ext.eclipseGroup).deploy(project.ext.eclipseSources, project.buildDir, mavenDeployer)
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
}
