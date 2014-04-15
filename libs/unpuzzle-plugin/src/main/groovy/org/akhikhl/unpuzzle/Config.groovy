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

  private static void merge(Config target, Config source) {
    if(source.parentConfig)
      merge(target, source.parentConfig)
    if(source.defaultEclipseVersion != null)
      target.defaultEclipseVersion = source.defaultEclipseVersion
    source.versionConfigs.each { String version, EclipseVersionConfig svc ->
      EclipseVersionConfig tvc = target.versionConfigs[version]
      if(tvc == null)
        tvc = target.versionConfigs[version] = new EclipseVersionConfig()
      if(svc.eclipseMavenGroup)
        tvc.eclipseMavenGroup = svc.eclipseMavenGroup
      if(svc.eclipseMirror)
        tvc.eclipseMirror = svc.eclipseMirror
      tvc.sources.addAll svc.sources
    }
    target.uploadEclipse << source.uploadEclipse
  }

  Config parentConfig

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

  Config getEffectiveConfig() {
    Config result = new Config()
    merge(result, this)
    return result
  }
}
