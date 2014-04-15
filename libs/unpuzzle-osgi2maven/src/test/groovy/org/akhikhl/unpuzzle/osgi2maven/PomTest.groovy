/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle.osgi2maven

import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit

import spock.lang.Specification

/**
 * Unit-test for {@link org.akhikhl.unpuzzle.osgi2maven.Pom} class.
 * @author Andrey Hihlovskiy
 */
class PomTest extends Specification {

  def setupSpec() {
    XMLUnit.setIgnoreWhitespace(true)
  }

  private static identicalXml(control, result) {
    new Diff(control, result).identical()
  }

  def 'should create empty pom'() {
  when:
    def pom = new Pom()
  then:
    identicalXml '''<?xml version="1.0" encoding="UTF-8"?>
<project xmlns='http://maven.apache.org/POM/4.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>
  <modelVersion>4.0.0</modelVersion>
</project>''', pom.toString()
  }

  def 'should create simple pom'() {
  when:
    def pom = new Pom(group: 'myGroup', artifact: 'myArtifact', version: '0.0.1')
  then:
    identicalXml '''<?xml version="1.0" encoding="UTF-8"?>
<project xmlns='http://maven.apache.org/POM/4.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>
  <modelVersion>4.0.0</modelVersion>
  <groupId>myGroup</groupId>
  <artifactId>myArtifact</artifactId>
  <version>0.0.1</version>
</project>''', pom.toString()
  }

  def 'should create pom with dependency'() {
  when:
    def pom = new Pom(group: 'myGroup', artifact: 'myArtifact', version: '0.0.1', dependencyGroup: 'group2')
    pom.dependencyBundles.add(new DependencyBundle(group: 'group1', name: 'dep1', version: '1.0'))
    pom.dependencyBundles.add(new DependencyBundle(name: 'dep2', version: '2.0'))
  then:
    identicalXml '''<?xml version="1.0" encoding="UTF-8"?>
<project xmlns='http://maven.apache.org/POM/4.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>
  <modelVersion>4.0.0</modelVersion>
  <groupId>myGroup</groupId>
  <artifactId>myArtifact</artifactId>
  <version>0.0.1</version>
  <dependencies>
    <dependency>
      <groupId>group1</groupId>
      <artifactId>dep1</artifactId>
      <version>1.0</version>
      <scope>compile</scope>
    </dependency>
    <dependency>
      <groupId>group2</groupId>
      <artifactId>dep2</artifactId>
      <version>2.0</version>
      <scope>compile</scope>
    </dependency>
  </dependencies>
</project>''', pom.toString()
  }
}

