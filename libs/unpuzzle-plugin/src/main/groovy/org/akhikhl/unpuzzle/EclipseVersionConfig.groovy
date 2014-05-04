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
 * Holds plugin configuration specific to particular eclipse version.
 * @author akhikhl
 */
class EclipseVersionConfig {

  String eclipseMavenGroup
  String eclipseMirror
  String eclipseArchiveMirror
  List<Closure> lazySources = []
  Set<String> languagePacks = new LinkedHashSet()

  Collection<EclipseSource> getSources() {
    getSourcesConfig().sources
  }

  EclipseSourcesConfig getSourcesConfig() {
    EclipseSourcesConfig sourcesConfig = new EclipseSourcesConfig(eclipseMirror: eclipseMirror, eclipseArchiveMirror: eclipseArchiveMirror)
    for(Closure closure in lazySources) {
      closure = closure.rehydrate(sourcesConfig, closure.owner, this)
      closure.resolveStrategy = Closure.DELEGATE_FIRST
      closure()
    }
    for(String language in languagePacks)
      sourcesConfig.languagePack(language)
    return sourcesConfig
  }

  void languagePack(String language) {
    languagePacks.add(language)
  }

  void sources(Closure closure) {
    lazySources.add(closure)
  }

  @Override
  String toString() {
    "EclipseVersionConfig(eclipseMavenGroup=${eclipseMavenGroup}, eclipseMirror=${eclipseMirror}, eclipseArchiveMirror=${eclipseArchiveMirror}, lazySources[${lazySources.size()}], languagePacks=${languagePacks})"
  }
}

