/*
 * osgi2mvn
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package osgi2mvn

import groovy.xml.MarkupBuilder

/**
 * POJO class holding data extracted from bundle and needed for POM generation.
 * @author ahi
 */
final class Pom {

  private static final String encoding = 'UTF-8'

	String group
  String artifact
  String version
	String packaging = 'jar'
	List<RequiredBundle> requiredBundles = []
  String dependencyGroup

	boolean isZip() {
		return packaging == 'zip'
	}

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
      if(pom.requiredBundles)
        dependencies {
          for(def reqBundle in pom.requiredBundles)
            dependency {
              if(reqBundle.group)
                groupId reqBundle.group
              else if(dependencyGroup)
                groupId dependencyGroup
              else
                groupId reqBundle.name
              artifactId reqBundle.name
              if(reqBundle.version)
                version reqBundle.version
              scope 'compile'
            }
        }
    }
  }
}

