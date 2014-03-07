/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package org.akhikhl.unpuzzle.osgi2maven

import groovy.xml.NamespaceBuilder

/**
 * Deploys OSGI bundle (jar or directory) to maven repository
 * @author Andrey Hihlovskiy
 */
class Deployer {

  private Map deployerOptions
  private String repositoryUrl
  private AntBuilder ant
  private mvn
  private File workFolder

  /**
   * Constructs Deployer with the specified parameters.
   * @param deployerOptions - may contain properties "user" and "password"
   * @param repositoryUrl - URL of the target maven repository
   */
  Deployer(Map deployerOptions = [:], String repositoryUrl) {
    this.deployerOptions = deployerOptions
    if(deployerOptions.ant)
      this.ant = deployerOptions.ant
    else {
      URLClassLoader classLoader = (URLClassLoader)this.getClass().getClassLoader()
      URL mavenAntTasks = classLoader.URLs.find { it.toString().contains('maven-ant-tasks') }
      this.ant = new AntBuilder()
      this.ant.taskdef(resource: 'org/apache/maven/artifact/ant/antlib.xml', classpath: mavenAntTasks.toString())
    }
    this.repositoryUrl = repositoryUrl
    workFolder = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString())
    workFolder.deleteOnExit()
  }

  /**
   * Deploys the specified bundle with the specified POM to target maven repository.
   * @param options - may contain sourceFile (of type java.io.File), pointing to sources jar.
   * @param pomStruct - contains POM that will be used for deployment
   * @param bundleFileOrDirectory - jar-file or directory, containing OSGI bundle
   */
  void deployBundle(Map options = [:], Pom pomStruct, File bundleFileOrDirectory) {
    workFolder.mkdirs()
    def pomFile = new File(workFolder, 'myPom.xml')
    File bundleFile
    if (bundleFileOrDirectory.isDirectory()) {
      pomStruct.packaging = 'jar'
      pomFile.text = pomStruct.toString()
      File zipFile = new File(workFolder, "${pomStruct.artifact}-${pomStruct.version}.jar")
      ant.zip(basedir: bundleFileOrDirectory, destfile: zipFile)
      bundleFile = zipFile
    }
    else {
      pomFile.text = pomStruct.toString()
      bundleFile = bundleFileOrDirectory
    }
    File sourceFile = options.sourceFile
    if(sourceFile?.isDirectory()) {
      File zipFile = new File(workFolder, sourceFile.name + '.jar')
      ant.zip(basedir: sourceFile, destfile: zipFile)
      sourceFile = zipFile
    }
    ant.with {
      pom id: 'mypom', file: pomFile
      deploy file: bundleFile, {
        pom refid: 'mypom'
        if(sourceFile)
          attach file: sourceFile, type: 'jar', classifier: 'sources'
       remoteRepository url: repositoryUrl, {
        if(deployerOptions.user && deployerOptions.password)
          authentication username: deployerOptions.user, password: deployerOptions.password
       }
     }
    }
  }
}

