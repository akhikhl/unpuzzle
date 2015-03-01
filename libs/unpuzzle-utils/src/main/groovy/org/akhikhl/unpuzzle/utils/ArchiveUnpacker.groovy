/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.zip.GZIPInputStream

import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveException
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils

/**
 * Unpacks the specified archive file - .zip, .tar.gz, .gz or .tar
 * As a convenience feature prints the number of unpacked bytes to console.
 * @author akhikhl
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
          if (isExecutable(entry, entry.getMode())) {
            outputFile.setExecutable(true);
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
    ZipArchiveInputStream zis = new ZipArchiveInputStream(new FileInputStream(inputFile))
    try {
      ZipArchiveEntry ze = zis.getNextEntry()
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
          if (isExecutable(ze, ze.getUnixMode())) {
            outputFile.setExecutable(true);
          }
        }
        ze = zis.getNextEntry()
      }
    } finally {
      zis.close()
      console.endProgress()
    }
  }

  /** Returns true if the given unix filemode contains any executable flags. */
  public static boolean isExecutable(ArchiveEntry entry, int mode) {
    try {
      return parseFilePermissions(mode).contains("x");
    } catch (Exception e) {
      System.err.println("Ignoring permissions for " + entry.getName() + " mode= " + mode + " because " + e.getMessage());
    }
  }

  /** Returns a modeStr which is compatible with PosixFilePermissions.fromString.
   *  
   *  TODO: When unpuzzle upgrades to Java >= 7
   *  This modeStr is compatible with PosixFilePermissions.fromString
   *  which is a Java7 API.  In the meantime, we will settle for
   *  File.setExecutable() which is available since 6.
   */
  public static String parseFilePermissions(int mode) {
    // add octal 1 000 (to ensure that the string has at least 4 characters)
    String modeNumStr = Integer.toOctalString(mode + 01000);
    if (modeNumStr.length() < 3) {
      throw new IllegalArgumentException("There should be at least 4 digits, but this only had " )
    }
    CharSequence permissions = modeNumStr.substring(modeNumStr.length()-3)

    StringBuffer b = new StringBuffer(9);
    for (int i = 0; i < 3; i++) {
      char c = permissions.charAt(i);
      switch (c) {
      case '0': b.append("---"); break;
      case '1': b.append("--x"); break;
      case '2': b.append("-w-"); break;
      case '3': b.append("-wx"); break;
      case '4': b.append("r--"); break;
      case '5': b.append("r-x"); break;
      case '6': b.append("rw-"); break;
      case '7': b.append("rwx"); break;
      default: throw new IllegalArgumentException("Each digit should be 0-7, was " + c);
      }
    }
    return b.toString();
  }
}
