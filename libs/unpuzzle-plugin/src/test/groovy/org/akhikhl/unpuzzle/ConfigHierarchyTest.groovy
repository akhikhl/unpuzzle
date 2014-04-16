/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle

import org.akhikhl.unpuzzle.eclipse2maven.EclipseSource

import spock.lang.Specification

/**
 *
 * @author akhikhl
 */
class ConfigHierarchyTest extends Specification {

  def 'should support defaultEclipseVersion inheritance'() {
  when:
    def c1 = new Config(defaultEclipseVersion: 'a')
    def c2 = new Config(parentConfig: c1)
    def c3 = new Config(parentConfig: c2, defaultEclipseVersion: 'b')
    def c4 = new Config(parentConfig: c3)
    def c5 = new Config(parentConfig: c4)
  then:
    c1.effectiveConfig.defaultEclipseVersion == 'a'
    c2.effectiveConfig.defaultEclipseVersion == 'a'
    c3.effectiveConfig.defaultEclipseVersion == 'b'
    c4.effectiveConfig.defaultEclipseVersion == 'b'
    c5.effectiveConfig.defaultEclipseVersion == 'b'
  }

  def 'should support eclipseMavenGroup inheritance'() {
  when:
    def c1 = new Config(defaultEclipseVersion: 'a')
    c1.eclipseVersion 'a', {
      eclipseMavenGroup = 'x'
    }
    c1.eclipseVersion 'b', {
    }
    c1.eclipseVersion 'c', {
      eclipseMavenGroup = 'x1'
    }
    def c2 = new Config(parentConfig: c1)
    c2.eclipseVersion 'a', {
      eclipseMavenGroup = 'y'
    }
    c2.eclipseVersion 'b', {
      eclipseMavenGroup = 'z'
    }
  then:
    c1.effectiveConfig.versionConfigs.a.eclipseMavenGroup == 'x'
    c1.effectiveConfig.versionConfigs.b.eclipseMavenGroup == null
    c1.effectiveConfig.versionConfigs.c.eclipseMavenGroup == 'x1'
    c2.effectiveConfig.versionConfigs.a.eclipseMavenGroup == 'y'
    c2.effectiveConfig.versionConfigs.b.eclipseMavenGroup == 'z'
    c2.effectiveConfig.versionConfigs.c.eclipseMavenGroup == 'x1'
  }

  def 'should support source inheritance'() {
  when:
    def c1 = new Config(defaultEclipseVersion: 'a')
    c1.eclipseVersion 'a', {
      source 'source-1'
      source 'source-2'
    }
    c1.eclipseVersion 'b', {
    }
    c1.eclipseVersion 'c', {
      source 'source-3'
      source 'source-4'
    }
    def c2 = new Config(parentConfig: c1)
    c2.eclipseVersion 'a', {
      source 'source-5'
    }
    c2.eclipseVersion 'b', {
      source 'source-6'
    }
  then:
    c1.effectiveConfig.versionConfigs.a.sources.collect { it.url } == ['source-1', 'source-2']
    c1.effectiveConfig.versionConfigs.b.sources.collect { it.url } == []
    c1.effectiveConfig.versionConfigs.c.sources.collect { it.url } == ['source-3', 'source-4']
    c2.effectiveConfig.versionConfigs.a.sources.collect { it.url } == ['source-1', 'source-2', 'source-5']
    c2.effectiveConfig.versionConfigs.b.sources.collect { it.url } == ['source-6']
    c2.effectiveConfig.versionConfigs.c.sources.collect { it.url } == ['source-3', 'source-4']
  }
}

