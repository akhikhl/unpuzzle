/*
 * osgi2mvn
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package osgi2mvn

/**
 * Deploys OSGI bundle (jar or directory) to maven repository
 * @author Andrey Hihlovskiy
 */
class Deployer {
	
  private Map deployerOptions
  private String repositoryUrl
  private AntBuilder ant = new AntBuilder()
  private File workFolder

  /**
   * Constructs Deployer with the specified parameters.
   * @param deployerOptions - may contain properties "user" and "password"
   * @param repositoryUrl - URL of the target maven repository
   */
  Deployer(Map deployerOptions = [:], String repositoryUrl) {
    this.deployerOptions = deployerOptions
    this.repositoryUrl = repositoryUrl
    ant.taskdef resource: 'org/apache/maven/artifact/ant/antlib.xml'
    workFolder = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString())
    workFolder.deleteOnExit()
  }

  /**
   * deploys the specified bundle with the specified pom to target maven repository.
   * @param options - may contain sourceFile (of type java.io.File), pointing to sources jar.
   * @param pom - contains POM that will be used for deployment
   * @param file - jar-file or directory, containing OSGI bundle
   */
  void deployBundle(Map options = [:], Pom pomStruct, File file) {
    workFolder.mkdirs()
    def pomFile = new File(workFolder, 'myPom.xml')
    if (file.isDirectory()) {
      pomStruct.packaging = 'jar'
      pomFile.text = pomStruct.toString()
      File zipFile = new File(workFolder, "${pomStruct.artifact}-${pomStruct.version}.jar")
      ant.zip(basedir: file, destfile: zipFile)
      file = zipFile
    }
    else
      pomFile.text = pomStruct.toString()
    File sourceFile = options.sourceFile
    if(sourceFile?.isDirectory()) {
      File zipFile = new File(workFolder, sourceFile.name + '.jar')
      ant.zip(basedir: sourceFile, destfile: zipFile)
      sourceFile = zipFile
    }
    ant.with {
      pom id: 'mypom', file: pomFile
      deploy file: file, {
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

