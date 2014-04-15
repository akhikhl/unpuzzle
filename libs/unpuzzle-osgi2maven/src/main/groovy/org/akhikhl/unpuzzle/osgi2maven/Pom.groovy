/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle.osgi2maven

import groovy.xml.MarkupBuilder

import org.osgi.framework.Constants

/**
 * POJO class holding data extracted from bundle and needed for POM generation.
 * @author Andrey Hihlovskiy
 */
final class Pom {

  private static final String encoding = 'UTF-8'

	String group
  String artifact
  String version
	String packaging = 'jar'
	List<DependencyBundle> dependencyBundles = []
  String dependencyGroup

	String toString() {
    ByteArrayOutputStream stm = new ByteArrayOutputStream()
    writeTo(new OutputStreamWriter(stm, encoding))
    String result = stm.toString(encoding)
    if(result.charAt(0) == 0xfeff)
      result = result.substring(1) // remove BOM, if present
    return result
	}

  void writeTo(Writer writer) {
    def builder = new MarkupBuilder(writer)
    builder.mkp.xmlDeclaration(version: '1.0', encoding: encoding)
    def pom = this
    builder.project xmlns: 'http://maven.apache.org/POM/4.0.0', 'xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance', {
      modelVersion '4.0.0'
      if(pom.group)
        groupId pom.group
      if(pom.artifact)
        artifactId pom.artifact
      if(pom.version)
        version pom.version
      if(pom.packaging != 'jar')
        packaging pom.packaging
      if(pom.dependencyBundles)
        dependencies {
          for(def depBundle in pom.dependencyBundles)
            dependency {
              if(depBundle.group)
                groupId depBundle.group
              else if(dependencyGroup)
                groupId dependencyGroup
              else
                groupId depBundle.name
              artifactId depBundle.name
              if(depBundle.version)
                version depBundle.version
              scope 'compile'
              if(depBundle.resolution == Constants.RESOLUTION_OPTIONAL)
                optional true
            }
        }
    }
  }
}

