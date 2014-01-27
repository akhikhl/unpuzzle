/*
 * osgi2mvn
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package osgi2mvn

import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit

import spock.lang.Specification

/**
 * Unit-test for {@link osgi2mvn.Pom} class.
 * @author ahi
 */
class PomTest extends Specification {

  def setupSpec() {
    XMLUnit.setIgnoreWhitespace(true)
  }

  private static identicalXml(control, result) {
    new Diff(control, result).identical()
  }

  def 'should create create empty pom xml'() {
  when:
    def pom = new Pom()
  then:
    identicalXml '''<?xml version="1.0" encoding="UTF-8"?>
<project xmlns='http://maven.apache.org/POM/4.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>
  <modelVersion>4.0.0</modelVersion>
</project>''', pom.toString()
  }

  def 'should create create simple pom xml'() {
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
}

