/*
 * underwork
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package org.akhikhl.underwork.gradle

import org.akhikhl.underwork.eclipse2maven.EclipseSource
import org.gradle.api.Project

/**
 * Plugin extension for {@link org.akhikhl.underwork.gradle.UnderworkPlugin}
 */
class UnderworkPluginExtension {

  String group = 'eclipse-kepler'
  String current_os
  String current_arch
  List<EclipseSource> sources = []
  def uploadEclipse = [:]

  UnderworkPluginExtension() {
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

  private void applyConfig(String configName) {
    Binding binding = new Binding()
    def pluginExtension = this
    binding.underwork = { Closure closure ->
      closure.resolveStrategy = Closure.DELEGATE_FIRST
      closure.delegate = pluginExtension
      closure()
    }
    GroovyShell shell = new GroovyShell(binding)
    this.getClass().getClassLoader().getResourceAsStream("${configName}.groovy").withReader('UTF-8') {
      shell.evaluate(it)
    }
  }

  void config(String configName) {
    if (configName in ['eclipse-kepler'])
      applyConfig(configName)
    else
      System.err.println("Unrecognized underwork configuration: $configName")
  }

  void source(Map options = [:], String url) {
    def src = new EclipseSource(url: url)
    if(options.sourcesOnly)
      src.sourcesOnly = options.sourcesOnly
    if(options.languagePacksOnly)
      src.languagePacksOnly = options.languagePacksOnly
    sources.add(src)
  }
}
