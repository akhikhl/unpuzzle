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

  Config parentConfig

  String selectedEclipseVersion
  Set<String> languagePacks = new LinkedHashSet()
  Map<String, List<Closure>> lazyVersions = [:]
  private Map<String, EclipseVersionConfig> versionConfigs = null
  Map uploadEclipse = [:]

  void eclipseVersion(String versionString, Closure closure) {
    List<Closure> closureList = lazyVersions[versionString]
    if(closureList == null)
      closureList = lazyVersions[versionString] = []
    closureList.add(closure)
    versionConfigs = null
  }

  void languagePack(String language) {
    languagePacks.add(language)
  }

  Config getEffectiveConfig() {
    Config result = new Config()
    merge(result, this)
    return result
  }

  EclipseVersionConfig getSelectedVersionConfig() {
    getVersionConfigs()[selectedEclipseVersion]
  }

  Map<String, EclipseVersionConfig> getVersionConfigs() {
    if(versionConfigs == null) {
      Map m = [:]
      lazyVersions.each { String versionString, List<Closure> closureList ->
        def versionConfig = m[versionString] = new EclipseVersionConfig()
        for(Closure closure in closureList) {
          closure = closure.rehydrate(versionConfig, closure.owner, closure.thisObject)
          closure.resolveStrategy = Closure.DELEGATE_FIRST
          closure()
        }
        for(String language in languagePacks)
          versionConfig.languagePack(language)
      }
      versionConfigs = m
    }
    return versionConfigs.asImmutable()
  }

  private static void merge(Config target, Config source) {
    if(source.parentConfig)
      merge(target, source.parentConfig)
    if(source.selectedEclipseVersion != null)
      target.selectedEclipseVersion = source.selectedEclipseVersion
    target.languagePacks.addAll(source.languagePacks)
    source.lazyVersions.each { String versionString, List<Closure> sourceClosureList ->
      List<Closure> targetClosureList = target.lazyVersions[versionString]
      if(targetClosureList == null)
        targetClosureList = target.lazyVersions[versionString] = []
      targetClosureList.addAll(sourceClosureList)
    }
    target.uploadEclipse << source.uploadEclipse
  }
}
