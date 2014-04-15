/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle

import org.akhikhl.unpuzzle.eclipse2maven.EclipseSource

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
      svc.sources.each { EclipseSource s ->
        EclipseSource t = new EclipseSource(url: s.url, sourcesOnly: s.sourcesOnly, languagePacksOnly: s.languagePacksOnly)
        if(t.url instanceof Closure) {
          def c = t.url
          c = c.rehydrate(tvc, c.owner, c.thisObject)
          c.resolveStrategy = Closure.DELEGATE_FIRST
          t.url = c
        }
        tvc.sources.add(t)
      }
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
