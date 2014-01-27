/*
 * osgi2mvn
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package osgi2mvn

import java.util.jar.JarFile
import java.util.jar.Manifest

import org.osgi.framework.Bundle
import org.osgi.framework.Constants

/**
 * Converts bundle manifest to pom.
 * @author ahi
 */
class Bundle2Pom {

  String group
  String dependencyGroup

  Bundle2Pom(String group = null, String dependencyGroup = null) {
    this.group = group
    this.dependencyGroup = dependencyGroup
  }

  public Pom convert(File bundleFileOrDirectory) {
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

    parseRequiredBundles(pom.requiredBundles, manifest.attr.getValue(Constants.REQUIRE_BUNDLE))

    return pom
  }

  private RequiredBundle parseRequiredBundle(String string) {
    List elements = string.split(';')
    String name = elements[0]
    elements.remove(0)
    RequiredBundle bundle = new RequiredBundle(name: name, resolution: Constants.RESOLUTION_MANDATORY, visibility: Constants.VISIBILITY_PRIVATE, version: "[1.0,)")
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

  private void parseRequiredBundles(List<RequiredBundle> requiredBundles, String requiredBundlesString) {
    int startPos = 0
    boolean quoted = false
    for(int i = 0; i < requiredBundlesString.length(); i++) {
      char c = requiredBundlesString.charAt(i)
      if(c == ',' && !quoted) {
        requiredBundles.add(parseRequiredBundle(requiredBundlesString.substring(startPos, i)))
        startPos = i + 1
      } else if(c == '"')
        quoted = !quoted
    }
    if(startPos < requiredBundlesString.length())
      requiredBundles.add(parseRequiredBundle(requiredBundlesString.substring(startPos, requiredBundlesString.length())))
  }
}

