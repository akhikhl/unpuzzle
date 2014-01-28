/*
 * mavenize
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package org.akhikhl.mavenize.eclipse2mvn

import org.akhikhl.mavenize.downloadhelpers.IConsole
import org.akhikhl.mavenize.downloadhelpers.SysConsole
import org.akhikhl.mavenize.downloadhelpers.Downloader
import org.akhikhl.mavenize.downloadhelpers.ArchiveUnpacker

/**
 * Downloads and unpacks more or more eclipse distributions from the specified URLs.
 * @author Andrey Hihlovskiy
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
      File unpackDir = new File(targetDir, Utils.getArchiveNameNoExt(fileName))
      if(!unpackDir.exists()) {
        File archiveFile = new File(targetDir, fileName)
        downloader.downloadFile(new URL(url), archiveFile)
        File unpackTempDir = new File(targetDir, UUID.randomUUID().toString())
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

