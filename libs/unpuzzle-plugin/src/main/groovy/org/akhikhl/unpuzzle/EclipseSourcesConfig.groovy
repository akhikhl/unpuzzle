/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle

import org.akhikhl.unpuzzle.eclipse2maven.EclipseSource

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author ahi
 */
class EclipseSourcesConfig {

  private static final Logger log = LoggerFactory.getLogger(EclipseSourcesConfig)

  String eclipseMirror
  String eclipseArchiveMirror
  List<EclipseSource> sources = []
  List<String> languagePackTemplates = []

  void languagePack(String language) {
    def engine = new groovy.text.GStringTemplateEngine()
    for(String template in languagePackTemplates)
      source engine.createTemplate(template).make([eclipseMirror: eclipseMirror, eclipseArchiveMirror: eclipseArchiveMirror, language: language]).toString(), languagePacksOnly: true
  }

  void languagePackTemplate(String template) {
    languagePackTemplates.add(template)
  }

  void source(Map options = [:], String url) {
    log.debug 'EclipseSourcesConfig.source {}', url
    def src = new EclipseSource(url: url)
    if(options.sourcesOnly)
      src.sourcesOnly = options.sourcesOnly
    if(options.languagePacksOnly)
      src.languagePacksOnly = options.languagePacksOnly
    sources.add(src)
  }
}

