/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle.eclipse2maven

import org.apache.commons.codec.digest.DigestUtils

import org.akhikhl.unpuzzle.utils.IConsole
import org.akhikhl.unpuzzle.utils.SysConsole
import org.akhikhl.unpuzzle.utils.Downloader
import org.akhikhl.unpuzzle.utils.ArchiveUnpacker

/**
 * Downloads and unpacks more or more eclipse distributions from the specified URLs.
 * @author akhikhl
 */
final class EclipseDownloader {

  private IConsole console

  EclipseDownloader() {
    this.console = new SysConsole()
  }

  EclipseDownloader(IConsole console) {
    this.console = console
  }

  void downloadAndUnpack(List<EclipseSource> sources, File targetDir) {
    targetDir.mkdirs()
    Downloader downloader = new Downloader(console)
    ArchiveUnpacker archiveUnpacker = new ArchiveUnpacker(console)
    for(EclipseSource source in sources) {
      String url = source.url
      String fileName = url.substring(url.lastIndexOf('/') + 1)
      File unpackDir = new File(targetDir, "unpacked/${Utils.getArchiveNameNoExt(fileName)}")
      if(!unpackDir.exists()) {
        File archiveFile = new File(targetDir, "downloaded/${fileName}")
        downloader.downloadFile(new URL(url), archiveFile)
        String archiveFileMd5
        archiveFile.withInputStream {
          archiveFileMd5 = DigestUtils.md5Hex(it)
        }
        File checksumFile = new File(targetDir, "downloaded-checksums/${fileName}.md5")
        checksumFile.parentFile.mkdirs()
        checksumFile.text = archiveFileMd5
        File unpackTempDir = new File(targetDir, "unpacked/${UUID.randomUUID().toString()}")
        archiveUnpacker.unpack(archiveFile, unpackTempDir)
        File[] files = unpackTempDir.listFiles()
        if(files != null) {
          if(files.length == 1 && files[0].isDirectory())
            files[0].renameTo(unpackDir)
          else {
            unpackDir.mkdirs()
            for(File f in files)
              f.renameTo(new File(unpackDir, f.name))
          }
        }
        unpackTempDir.deleteDir()
      }
    }
  }
}
