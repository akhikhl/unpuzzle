/*
 * mavenize
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package org.akhikhl.mavenize.downloadhelpers

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

import org.apache.commons.compress.archivers.ArchiveException
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils

/**
 * Unpacks the specified archive file - .zip, .tar.gz, .gz or .tar
* As a convenience feature prints the number of unpacked bytes to console.
  * @author Andrey Hihlovskiy
 */
final class ArchiveUnpacker {

  private class UncompressCountingOutputStream extends CountingOutputStream {

    private int afterWriteCalls = 0

    public UncompressCountingOutputStream(final OutputStream out) {
      super(out)
    }

    @Override
    protected void afterWrite(int n) throws IOException {
      ++afterWriteCalls
      if (afterWriteCalls % 300 == 0)
        console.progress("Uncompressing bytes: ${this.getCount()}")
    }
  }

  private final IConsole console
  private File workFolder

  ArchiveUnpacker(IConsole console) {
    this.console = console
    workFolder = new File(System.getProperty('java.io.tmpdir'), UUID.randomUUID().toString())
    workFolder.deleteOnExit()
  }

  void unGzip(final File inputFile, final File outputFile) throws IOException {
    console.startProgress("Ungzipping file: ${inputFile.getName()}")
    try {
      inputFile.withInputStream { ins ->
        outputFile.withOutputStream { out ->
          IOUtils.copy(new GZIPInputStream(ins), new UncompressCountingOutputStream(out))
        }
      }
    } finally {
      console.endProgress()
    }
  }

  void unpack(File archiveFile, File outputDir) throws IOException, ArchiveException {
    String fileName = archiveFile.getName()
    if (fileName.endsWith('.tar.gz')) {
      workFolder.mkdirs()
      File tarFile = new File(workFolder, StringUtils.removeEnd(fileName, '.tar.gz') + '.tar')
      unGzip(archiveFile, tarFile)
      unTar(tarFile, outputDir)
    }
    else if (fileName.endsWith('.gz')) {
      File tarFile = new File(outputDir, StringUtils.removeEnd(fileName, '.gz'))
      unGzip(archiveFile, tarFile)
    }
    else if (fileName.endsWith('.tar'))
      unTar(archiveFile, outputDir)
    else if (fileName.endsWith('.zip'))
      unZip(archiveFile, outputDir)
  }

  void unTar(final File inputFile, final File outputDir) throws IOException, ArchiveException {
    console.startProgress("Untarring file: ${inputFile.getName()}")
    outputDir.mkdirs()
    final TarArchiveInputStream tarInputStream = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.TAR, new FileInputStream(inputFile))
    try {
      TarArchiveEntry entry = null
      while ((entry = (TarArchiveEntry) tarInputStream.getNextEntry()) != null) {
        final File outputFile = new File(outputDir, entry.getName())
        console.info(entry.getName())
        if (entry.isDirectory())
          outputFile.mkdirs()
        else {
          outputFile.getParentFile().mkdirs()
          outputFile.withOutputStream { outputFileStream ->
            IOUtils.copy(tarInputStream, outputFileStream)
          }
        }
      }
    } finally {
      tarInputStream.close()
      console.endProgress()
    }
  }

  void unZip(final File inputFile, final File outputDir) throws IOException, ArchiveException {
    console.startProgress("Unzipping file: ${inputFile.getName()}")
    ZipInputStream zis = new ZipInputStream(new FileInputStream(inputFile))
    try {
      ZipEntry ze = zis.getNextEntry()
      while (ze != null) {
        final File outputFile = new File(outputDir, ze.getName())
        console.info(ze.getName())
        if (ze.isDirectory())
          outputFile.mkdirs()
        else {
          outputFile.getParentFile().mkdirs()
          outputFile.withOutputStream { fos ->
            IOUtils.copy(zis, fos)
          }
        }
        ze = zis.getNextEntry()
      }
    } finally {
      zis.close()
      console.endProgress()
    }
  }
}
