/*
 * osgi2mvn
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package org.akhikhl.oscr.osgi2mvn

import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit

import spock.lang.Specification
import java.util.UUID

/**
 * Unit-test for {@link org.akhikhl.oscr.osgi2mvn.Deployer} class.
 * @author Andrey Hihlovskiy
 */
class DeployerTest extends Specification {

  static File testFolder
  static Deployer deployer

  def setupSpec() {
    XMLUnit.setIgnoreWhitespace(true)
    testFolder = new File(System.getProperty('java.io.tmpdir'), UUID.randomUUID().toString())
    testFolder.deleteOnExit()
    deployer = new Deployer(testFolder.toURI().toURL().toString())
  }

  private static identicalXml(control, result) {
    new Diff(control, result).identical()
  }

  def 'should deploy jar bundle to test maven repository'() {
  when:
    def bundleFile = new File('src/test/resources/org.eclipse.equinox.preferences_3.5.100.v20130422-1538.jar')
    def pom = new Bundle2Pom('eclipse-test', 'eclipe-dep').convert(bundleFile)
    deployer.deployBundle pom, bundleFile
    def deployedDir = new File(testFolder, 'eclipse-test/org.eclipse.equinox.preferences/3.5.100.v20130422-1538')
    def deployedJar = new File(deployedDir, 'org.eclipse.equinox.preferences-3.5.100.v20130422-1538.jar')
    def deployedPom = new File(deployedDir, 'org.eclipse.equinox.preferences-3.5.100.v20130422-1538.pom')
  then:
    deployedDir.exists()
    deployedJar.exists()
    deployedPom.exists()
  }

  def 'should deploy unpacked bundle to test maven repository'() {
  when:
    def bundleFile = new File('src/test/resources/org.eclipse.epp.package.jee_2.0.1.20130919-0803')
    def pom = new Bundle2Pom('eclipse-test', 'eclipe-dep').convert(bundleFile)
    deployer.deployBundle pom, bundleFile
    def deployedDir = new File(testFolder, 'eclipse-test/org.eclipse.epp.package.jee/2.0.1.20130919-0803')
    def deployedJar = new File(deployedDir, 'org.eclipse.epp.package.jee-2.0.1.20130919-0803.jar')
    def deployedPom = new File(deployedDir, 'org.eclipse.epp.package.jee-2.0.1.20130919-0803.pom')
  then:
    deployedDir.exists()
    deployedJar.exists()
    deployedPom.exists()
  }
}

