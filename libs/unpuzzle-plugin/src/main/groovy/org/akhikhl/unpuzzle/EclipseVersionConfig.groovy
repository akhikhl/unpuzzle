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
  List<EclipseSource> sources = []
  List<String> languagePackTemplates = []

  void languagePack(String language) {
    def engine = new groovy.text.GStringTemplateEngine()
    for(String template in languagePackTemplates)
      source engine.createTemplate(template).make([eclipseMirror: eclipseMirror, language: language]).toString(), languagePacksOnly: true
  }

  void languagePackTemplate(String template) {
    languagePackTemplates.add(template)
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

