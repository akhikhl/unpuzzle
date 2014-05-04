/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle

import org.akhikhl.unpuzzle.eclipse2maven.EclipseSource

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification

/**
 *
 * @author akhikhl
 */
class ConfigHierarchyTest extends Specification {

  private UnpuzzlePlugin plugin

  def setup() {
    plugin = new UnpuzzlePlugin()
  }

  def 'should support default localMavenRepositoryDir'() {
  when:
    Project p = ProjectBuilder.builder().withName('p').build()
    plugin.apply(p)
  then:
    p.effectiveUnpuzzle.localMavenRepositoryDir == new File(System.getProperty('user.home'), '.m2/repository')
  }

  def 'should support localMavenRepositoryDir inheritance'() {
  when:
    Project p1 = ProjectBuilder.builder().withName('p1').build()
    plugin.apply(p1)
    def file1 = new File(System.getProperty('user.home'), 'someDirectory')
    p1.unpuzzle.localMavenRepositoryDir = file1
    Project p2 = ProjectBuilder.builder().withName('p2').withParent(p1).build()
    plugin.apply(p2)
    Project p3 = ProjectBuilder.builder().withName('p3').withParent(p2).build()
    plugin.apply(p3)
    def file2 = new File(System.getProperty('user.home'), 'someDirectory2')
    p3.unpuzzle.localMavenRepositoryDir = file2
    Project p4 = ProjectBuilder.builder().withName('p4').withParent(p3).build()
    plugin.apply(p4)
  then:
    p1.effectiveUnpuzzle.localMavenRepositoryDir == file1
    p2.effectiveUnpuzzle.localMavenRepositoryDir == file1
    p3.effectiveUnpuzzle.localMavenRepositoryDir == file2
    p4.effectiveUnpuzzle.localMavenRepositoryDir == file2
  }

  def 'should support default unpuzzleDir'() {
  when:
    Project p = ProjectBuilder.builder().withName('p').build()
    plugin.apply(p)
  then:
    p.effectiveUnpuzzle.unpuzzleDir == new File(System.getProperty('user.home'), '.unpuzzle')
  }

  def 'should support unpuzzleDir inheritance'() {
  when:
    Project p1 = ProjectBuilder.builder().withName('p1').build()
    plugin.apply(p1)
    def file1 = new File(System.getProperty('user.home'), 'someDirectory')
    p1.unpuzzle.unpuzzleDir = file1
    Project p2 = ProjectBuilder.builder().withName('p2').withParent(p1).build()
    plugin.apply(p2)
    Project p3 = ProjectBuilder.builder().withName('p3').withParent(p2).build()
    plugin.apply(p3)
    def file2 = new File(System.getProperty('user.home'), 'someDirectory2')
    p3.unpuzzle.unpuzzleDir = file2
    Project p4 = ProjectBuilder.builder().withName('p4').withParent(p3).build()
    plugin.apply(p4)
  then:
    p1.effectiveUnpuzzle.unpuzzleDir == file1
    p2.effectiveUnpuzzle.unpuzzleDir == file1
    p3.effectiveUnpuzzle.unpuzzleDir == file2
    p4.effectiveUnpuzzle.unpuzzleDir == file2
  }

  def 'should support selectedEclipseVersion inheritance and override'() {
  when:
    Project p1 = ProjectBuilder.builder().withName('p1').build()
    plugin.apply(p1)
    p1.unpuzzle.selectedEclipseVersion = 'a'
    Project p2 = ProjectBuilder.builder().withName('p2').withParent(p1).build()
    plugin.apply(p2)
    Project p3 = ProjectBuilder.builder().withName('p3').withParent(p2).build()
    plugin.apply(p3)
    p3.unpuzzle.selectedEclipseVersion = 'b'
    Project p4 = ProjectBuilder.builder().withName('p4').withParent(p3).build()
    plugin.apply(p4)
    Project p5 = ProjectBuilder.builder().withName('p5').withParent(p4).build()
    plugin.apply(p5)
  then:
    p1.effectiveUnpuzzle.selectedEclipseVersion == 'a'
    p2.effectiveUnpuzzle.selectedEclipseVersion == 'a'
    p3.effectiveUnpuzzle.selectedEclipseVersion == 'b'
    p4.effectiveUnpuzzle.selectedEclipseVersion == 'b'
    p5.effectiveUnpuzzle.selectedEclipseVersion == 'b'
  }

  def 'should support eclipseMavenGroup inheritance'() {
  when:
    Project p1 = ProjectBuilder.builder().withName('p1').build()
    plugin.apply(p1)
    p1.unpuzzle.with {
      selectedEclipseVersion = 'a'
      eclipseVersion 'a', {
        eclipseMavenGroup = 'x'
      }
      eclipseVersion 'b', {
      }
      eclipseVersion 'c', {
        eclipseMavenGroup = 'x1'
      }
    }
    Project p2 = ProjectBuilder.builder().withName('p2').withParent(p1).build()
    plugin.apply(p2)
    p2.unpuzzle.with {
      eclipseVersion 'a', {
        eclipseMavenGroup = 'y'
      }
      eclipseVersion 'b', {
        eclipseMavenGroup = 'z'
      }
    }
  then:
    p1.effectiveUnpuzzle.versionConfigs.a.eclipseMavenGroup == 'x'
    p1.effectiveUnpuzzle.versionConfigs.b.eclipseMavenGroup == null
    p1.effectiveUnpuzzle.versionConfigs.c.eclipseMavenGroup == 'x1'
    p2.effectiveUnpuzzle.versionConfigs.a.eclipseMavenGroup == 'y'
    p2.effectiveUnpuzzle.versionConfigs.b.eclipseMavenGroup == 'z'
    p2.effectiveUnpuzzle.versionConfigs.c.eclipseMavenGroup == 'x1'
  }

  def 'should support source inheritance'() {
  when:
    Project p1 = ProjectBuilder.builder().withName('p1').build()
    plugin.apply(p1)
    p1.unpuzzle.with {
      selectedEclipseVersion = 'a'
      eclipseVersion 'a', {
        sources {
          source 'source-1'
          source 'source-2'
        }
      }
      eclipseVersion 'b', {
      }
      eclipseVersion 'c', {
        sources {
          source 'source-3'
          source 'source-4'
        }
      }
    }
    Project p2 = ProjectBuilder.builder().withName('p2').withParent(p1).build()
    plugin.apply(p2)
    p2.unpuzzle.with {
      eclipseVersion 'a', {
        sources {
          source 'source-5'
        }
      }
      eclipseVersion 'b', {
        sources {
          source 'source-6'
        }
      }
    }
  then:
    p1.effectiveUnpuzzle.versionConfigs.a.sources.collect { it.url } == ['source-1', 'source-2']
    p1.effectiveUnpuzzle.versionConfigs.b.sources.collect { it.url } == []
    p1.effectiveUnpuzzle.versionConfigs.c.sources.collect { it.url } == ['source-3', 'source-4']
    p2.effectiveUnpuzzle.versionConfigs.a.sources.collect { it.url } == ['source-1', 'source-2', 'source-5']
    p2.effectiveUnpuzzle.versionConfigs.b.sources.collect { it.url } == ['source-6']
    p2.effectiveUnpuzzle.versionConfigs.c.sources.collect { it.url } == ['source-3', 'source-4']
  }

  def 'should support mirror override'() {
  when:
    Project p1 = ProjectBuilder.builder().withName('p1').build()
    plugin.apply(p1)
    p1.unpuzzle.with {
      selectedEclipseVersion = 'a'
      eclipseVersion 'a', {
        eclipseMirror = 'aaa'
        sources {
          source "${eclipseMirror}/source-1"
          source "${eclipseMirror}/source-2"
        }
      }
    }
    Project p2 = ProjectBuilder.builder().withName('p2').withParent(p1).build()
    plugin.apply(p2)
    p2.unpuzzle.with {
      eclipseVersion 'a', {
        eclipseMirror = 'bbb'
        sources {
          source "${eclipseMirror}/source-3"
        }
      }
    }
  then:
    p1.effectiveUnpuzzle.versionConfigs.a.sources.collect { it.url } == ['aaa/source-1', 'aaa/source-2']
    p2.effectiveUnpuzzle.versionConfigs.a.sources.collect { it.url } == ['bbb/source-1', 'bbb/source-2', 'bbb/source-3']
  }

  def 'should support language pack templates'() {
  when:
    Project p1 = ProjectBuilder.builder().withName('p1').build()
    plugin.apply(p1)
    p1.unpuzzle.with {
      selectedEclipseVersion = 'a'
      eclipseVersion 'a', {
        eclipseMirror = 'aaa'
        sources {
          // intentional: string, not GString (resolved when language pack is applied)
          languagePackTemplate '${eclipseMirror}/someFolder/somePackage_${language}.tar.gz'
          languagePack 'de'
        }
      }
    }
    Project p2 = ProjectBuilder.builder().withName('p2').withParent(p1).build()
    plugin.apply(p2)
    p2.unpuzzle.with {
      eclipseVersion 'a', {
        eclipseMirror = 'bbb'
        sources {
          languagePack 'fr'
          languagePack 'es'
        }
      }
    }
  then:
    p1.effectiveUnpuzzle.versionConfigs.a.sources.collect { it.url } == ['aaa/someFolder/somePackage_de.tar.gz']
    p2.effectiveUnpuzzle.versionConfigs.a.sources.collect { it.url } == ['bbb/someFolder/somePackage_de.tar.gz', 'bbb/someFolder/somePackage_fr.tar.gz', 'bbb/someFolder/somePackage_es.tar.gz']
  }
}
