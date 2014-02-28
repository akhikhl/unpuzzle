/*
 * underwork
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package org.akhikhl.underwork.utils

import org.apache.commons.codec.digest.DigestUtils
import spock.lang.Specification
import java.util.UUID

/**
 * Unit-test for {@link org.akhikhl.underwork.utils.Downloader} class.
 * @author Andrey Hihlovskiy
 */
class DownloaderTest extends Specification {

  static File testFolder
  static console

  def setupSpec() {
    testFolder = new File(System.getProperty('java.io.tmpdir'), UUID.randomUUID().toString())
    testFolder.deleteOnExit()
    console = new SysConsole()
  }

  Downloader downloader

  def setup() {
    downloader = new Downloader(console)
  }

  def 'should download files from arbitrary URL'() {
  when:
    downloader.downloadFile('sample.txt.tar.gz', new File('src/test/resources').absoluteFile.toURI().toURL().toString(), testFolder)
    def downloadedFile = new File(testFolder, 'sample.txt.tar.gz')
  then:
    downloadedFile.exists()
    downloadedFile.length() == 143
    String md5
    downloadedFile.withInputStream { ins ->
      md5 = DigestUtils.md5Hex(ins)
    }
    md5 == '05cdb738fc589f12ae6bae9221a5f023'
  }
}
