/*
 * untangle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package org.akhikhl.untangle.osgi2maven

import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit

import spock.lang.Specification

/**
 * Unit-test for {@link org.akhikhl.untangle.osgi2maven.Bundle2Pom} class.
 * @author Andrey Hihlovskiy
 */
class Bundle2PomTest extends Specification {

  def setupSpec() {
    XMLUnit.setIgnoreWhitespace(true)
  }

  private static identicalXml(control, result) {
    new Diff(control, result).identical()
  }

  def 'should convert jar bundle to pom'() {
  when:
    def bundleFile = new File('src/test/resources/org.eclipse.equinox.preferences_3.5.100.v20130422-1538.jar')
  then:
    bundleFile.exists()
    def pom = new Bundle2Pom('eclipse-test', 'eclipe-dep').convert(bundleFile)
    identicalXml '''<?xml version='1.0' encoding='UTF-8'?>
<project xmlns='http://maven.apache.org/POM/4.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>
  <modelVersion>4.0.0</modelVersion>
  <groupId>eclipse-test</groupId>
  <artifactId>org.eclipse.equinox.preferences</artifactId>
  <version>3.5.100.v20130422-1538</version>
  <dependencies>
    <dependency>
      <groupId>eclipe-dep</groupId>
      <artifactId>org.eclipse.equinox.common</artifactId>
      <version>[3.2.0,4.0.0)</version>
      <scope>compile</scope>
    </dependency>
    <dependency>
      <groupId>eclipe-dep</groupId>
      <artifactId>org.eclipse.equinox.registry</artifactId>
      <version>[3.2.0,4.0.0)</version>
      <scope>compile</scope>
      <optional>true</optional>
    </dependency>
  </dependencies>
</project>''', pom.toString()
  }

  def 'should convert unpacked bundle to pom'() {
  when:
    def bundleFile = new File('src/test/resources/org.eclipse.epp.package.jee_2.0.1.20130919-0803')
  then:
    bundleFile.exists()
    def pom = new Bundle2Pom('eclipse-test', 'eclipe-dep').convert(bundleFile)
    identicalXml '''<?xml version='1.0' encoding='UTF-8'?>
<project xmlns='http://maven.apache.org/POM/4.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>
  <modelVersion>4.0.0</modelVersion>
  <groupId>eclipse-test</groupId>
  <artifactId>org.eclipse.epp.package.jee</artifactId>
  <version>2.0.1.20130919-0803</version>
  <packaging>dir</packaging>
  <dependencies>
    <dependency>
      <groupId>eclipe-dep</groupId>
      <artifactId>org.eclipse.platform</artifactId>
      <version>[4.3.0,5.0.0)</version>
      <scope>compile</scope>
    </dependency>
    <dependency>
      <groupId>eclipe-dep</groupId>
      <artifactId>org.eclipse.equinox.app</artifactId>
      <version>[1.3.0,2.0.0)</version>
      <scope>compile</scope>
    </dependency>
  </dependencies>
</project>''', pom.toString()
  }
}

