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

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author akhikhl
 */
class ConfigHierarchyTest extends Specification {

  private static final Logger log = LoggerFactory.getLogger(ConfigHierarchyTest)

  def 'should support selectedEclipseVersion inheritance and override'() {
  when:
    def c1 = new Config(selectedEclipseVersion: 'a')
    def c2 = new Config(parentConfig: c1)
    def c3 = new Config(parentConfig: c2, selectedEclipseVersion: 'b')
    def c4 = new Config(parentConfig: c3)
    def c5 = new Config(parentConfig: c4)
  then:
    c1.effectiveConfig.selectedEclipseVersion == 'a'
    c2.effectiveConfig.selectedEclipseVersion == 'a'
    c3.effectiveConfig.selectedEclipseVersion == 'b'
    c4.effectiveConfig.selectedEclipseVersion == 'b'
    c5.effectiveConfig.selectedEclipseVersion == 'b'
  }

  def 'should support eclipseMavenGroup inheritance'() {
  when:
    def c1 = new Config(selectedEclipseVersion: 'a')
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
    def c1 = new Config(selectedEclipseVersion: 'a')
    c1.eclipseVersion 'a', {
      sources {
        source 'source-1'
        source 'source-2'
      }
    }
    c1.eclipseVersion 'b', {
    }
    c1.eclipseVersion 'c', {
      sources {
        source 'source-3'
        source 'source-4'
      }
    }
    def c2 = new Config(parentConfig: c1)
    c2.eclipseVersion 'a', {
      sources {
        source 'source-5'
      }
    }
    c2.eclipseVersion 'b', {
      sources {
        source 'source-6'
      }
    }
  then:
    c1.effectiveConfig.versionConfigs.a.sources.collect { it.url } == ['source-1', 'source-2']
    c1.effectiveConfig.versionConfigs.b.sources.collect { it.url } == []
    c1.effectiveConfig.versionConfigs.c.sources.collect { it.url } == ['source-3', 'source-4']
    c2.effectiveConfig.versionConfigs.a.sources.collect { it.url } == ['source-1', 'source-2', 'source-5']
    c2.effectiveConfig.versionConfigs.b.sources.collect { it.url } == ['source-6']
    c2.effectiveConfig.versionConfigs.c.sources.collect { it.url } == ['source-3', 'source-4']
  }

  def 'should support mirror override'() {
  when:
    def c1 = new Config(selectedEclipseVersion: 'a')
    c1.eclipseVersion 'a', {
      eclipseMirror = 'aaa'
      sources {
        source "${eclipseMirror}/source-1"
        source "${eclipseMirror}/source-2"
      }
    }
    def c2 = new Config(parentConfig: c1)
    c2.eclipseVersion 'a', {
      eclipseMirror = 'bbb'
      sources {
        source "${eclipseMirror}/source-3"
      }
    }
  then:
    c1.effectiveConfig.versionConfigs.a.sources.collect { it.url } == ['aaa/source-1', 'aaa/source-2']
    c2.effectiveConfig.versionConfigs.a.sources.collect { it.url } == ['bbb/source-1', 'bbb/source-2', 'bbb/source-3']
  }
}
