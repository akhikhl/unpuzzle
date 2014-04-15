/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle

/**
 * Plugin extension for {@link org.akhikhl.unpuzzle.UnpuzzlePlugin}
 * @author akhikhl
 */
class Config {

  String defaultEclipseVersion = null

  Map<String, EclipseVersionConfig> versionConfigs = [:]

  Map uploadEclipse = [:]

  void eclipseVersion(String versionString, Closure closure) {
    if(versionConfigs[versionString] == null)
      versionConfigs[versionString] = new EclipseVersionConfig()
    closure.resolveStrategy = Closure.DELEGATE_FIRST
    closure.delegate = versionConfigs[versionString]
    closure()
  }
}
