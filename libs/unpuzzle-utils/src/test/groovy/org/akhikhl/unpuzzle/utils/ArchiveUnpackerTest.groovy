/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package org.akhikhl.unpuzzle.utils

import spock.lang.Specification

/**
 * Unit-test for {@link org.akhikhl.unpuzzle.utils.ArchiveUnpacker} class.
 * @author Andrey Hihlovskiy
 */
class ArchiveUnpackerTest {

  static console

  def setupSpec() {
    console = new SysConsole()
  }

  File testFolder
  ArchiveUnpacker unpacker

  def setup() {
    testFolder = new File(System.getProperty('java.io.tmpdir'), UUID.randomUUID().toString())
    testFolder.deleteOnExit()
    unpacker = new ArchiveUnpacker(console)
  }

  def 'should unpack .tar.gz files'() {
  when:
    File archiveFile = new File('src/test/resources/sample.txt.tar.gz').absoluteFile
    unpacker.unpack(archiveFile, testFolder)
    File[] unpackedFiles = testFolder.listFiles()
  then:
    unpackedFiles != null
    unpackedFiles.length() == 1
    unpackedFiles[0].name == 'sample.txt'
    unpackedFiles[0].text == '0042bfe2-8812-11e3-85b6-6bf8a04179ee'
  }

  def 'should unpack .zip files'() {
  when:
    File archiveFile = new File('src/test/resources/sample.txt.zip').absoluteFile
    unpacker.unpack(archiveFile, testFolder)
    File[] unpackedFiles = testFolder.listFiles()
  then:
    unpackedFiles != null
    unpackedFiles.length() == 1
    unpackedFiles[0].name == 'sample.txt'
    unpackedFiles[0].text == '0042bfe2-8812-11e3-85b6-6bf8a04179ee'
  }
}

