/*
 * mavenize
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package org.akhikhl.mavenize.gradle.eclipse2mvn

import org.akhikhl.mavenize.eclipse2mvn.EclipseSource

class Eclipse2MvnPluginExtension {

  String eclipseGroup = 'eclipse'
  String current_os
  String current_arch

  List<EclipseSource> eclipseSources = []

  Eclipse2MvnPluginExtension() {
    current_os = System.getProperty('os.name')
    if(current_os.substring(0, 5).equalsIgnoreCase('linux'))
      current_os = 'linux'
    else if(current_os.substring(0, 7).equalsIgnoreCase('windows'))
      current_os = 'windows'

    current_arch = System.getProperty('os.arch')
    if(current_arch == 'x86' || current_arch == 'i386')
      current_arch = 'x86_32'
    else if(current_arch == 'amd64')
      current_arch = 'x86_64'
  }

  def eclipseSource(Map options = [:], String url) {
    def source = new EclipseSource(url: url)
    if(options.sourcesOnly)
      source.sourcesOnly = options.sourcesOnly
    if(options.languagePacksOnly)
      source.languagePacksOnly = options.languagePacksOnly
    eclipseSources.add(source)
  }
}
