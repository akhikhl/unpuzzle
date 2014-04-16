/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle.osgi2maven

import java.util.jar.JarFile
import java.util.jar.Manifest

import org.osgi.framework.Constants

/**
 * Converts bundle manifest to POM.
 * @author akhikhl
 */
final class Bundle2Pom {

  String group
  String dependencyGroup

  /**
   * Constructs Bundle2Pom object with the specified parameters.
   * @param group - maven group to which the given artifact belongs.
   * @param dependencyGroup - maven group to which the dependencies of the given artifact belong.
   */
  Bundle2Pom(String group = null, String dependencyGroup = null) {
    this.group = group
    this.dependencyGroup = dependencyGroup
  }

  /**
   * Converts bundle to POM.
   * @param bundleFileOrDirectory - jar-file or directory containing OSGi bundle.
   * @return the converted POM.
   */
  Pom convert(File bundleFileOrDirectory) {
    def pom = new Pom()
    def manifest
    if (bundleFileOrDirectory.isDirectory()) {
      new File(bundleFileOrDirectory, 'META-INF/MANIFEST.MF').withInputStream {
        manifest = new Manifest(it)
      }
      pom.packaging = 'dir'
    } else
      manifest = new JarFile(bundleFileOrDirectory).manifest

    pom.artifact = manifest.attr.getValue(Constants.BUNDLE_SYMBOLICNAME)
    if (pom.artifact.contains(';'))
      pom.artifact = pom.artifact.split(';')[0]

    pom.group = group ?: pom.artifact
    pom.dependencyGroup = dependencyGroup
    pom.version = manifest.attr.getValue(Constants.BUNDLE_VERSION)

    parseDependencyBundles(pom.dependencyBundles, manifest.attr.getValue(Constants.REQUIRE_BUNDLE))

    return pom
  }

  private DependencyBundle parseDependencyBundle(String string) {
    List elements = string.split(';')
    String name = elements[0]
    elements.remove(0)
    DependencyBundle bundle = new DependencyBundle(name: name, resolution: Constants.RESOLUTION_MANDATORY, visibility: Constants.VISIBILITY_PRIVATE, version: "[1.0,)")
    for(String element in elements)
      if (element.startsWith(Constants.BUNDLE_VERSION_ATTRIBUTE)) {
        String s = element.substring(element.indexOf('=') + 1)
        if(s.startsWith('"'))
          s = s.substring(1)
        if(s.endsWith('"'))
          s = s.substring(0, s.length() - 1)
        bundle.version = s
      } else if (element.startsWith(Constants.RESOLUTION_DIRECTIVE))
        bundle.resolution = element.substring(element.indexOf('=') + 1)
      else if (element.startsWith(Constants.VISIBILITY_DIRECTIVE))
        bundle.visibility = element.substring(element.indexOf('=') + 1)
    return bundle
  }

  private void parseDependencyBundles(List<DependencyBundle> depBundles, String depBundlesString) {
    if(!depBundlesString)
      return
    int startPos = 0
    boolean quoted = false
    for(int i = 0; i < depBundlesString.length(); i++) {
      char c = depBundlesString.charAt(i)
      if(c == ',' && !quoted) {
        depBundles.add(parseDependencyBundle(depBundlesString.substring(startPos, i)))
        startPos = i + 1
      } else if(c == '"')
        quoted = !quoted
    }
    if(startPos < depBundlesString.length())
      depBundles.add(parseDependencyBundle(depBundlesString.substring(startPos, depBundlesString.length())))
  }
}

