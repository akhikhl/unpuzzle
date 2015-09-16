/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle.utils

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

import org.apache.commons.io.IOUtils

/**
 * Downloads the specified file from the specified URL to the specified target folder.
 * As a convenience feature prints the number of downloaded bytes to console.
 * @author akhikhl
 */
final class Downloader {

  private class DownloadCountingOutputStream extends CountingOutputStream {

    private int afterWriteCalls = 0

    public DownloadCountingOutputStream(final OutputStream out) {
      super(out)
    }

    @Override
    protected void afterWrite(int n) throws IOException {
      ++afterWriteCalls
      if (afterWriteCalls % 300 == 0)
        console.progress("Downloaded bytes: ${this.getCount()}")
    }
  }

  static boolean checkConnection(URL url) {
    try {
      HttpURLConnection urlConn = (HttpURLConnection) url.openConnection()
      urlConn.connect()
      return HttpURLConnection.HTTP_OK == urlConn.getResponseCode()
    } catch (IOException e) {
      return false
    }
  }

  private final IConsole console

  Downloader(IConsole console) {
    this.console = console
  }

  void downloadFile(String fileName, String urlBase, File targetDir) throws IOException {
    String url = urlBase
    if (!url.endsWith('/'))
      url += '/'
    url += fileName
    downloadFile(new URL(url), new File(targetDir, fileName))
  }

  void downloadFile(URL url, File file) throws IOException {
    Long remoteContentLength
    def connection = openConnection(url)
    try {
      remoteContentLength = Long.parseLong(connection.getHeaderField('Content-Length'))
    } catch(NumberFormatException e) {
      // no header, download anyway
    }
    if (file.exists() && remoteContentLength == file.length()) {
      console.info("File ${file.getName()} already downloaded, skipping download")
      return
    }
    console.startProgress("Downloading file: ${url.toExternalForm()}")
    InputStream inStream = connection.getInputStream();
    try {
      file.parentFile.mkdirs()
      inStream.withStream { is ->
        file.withOutputStream { os ->
          IOUtils.copy(is, new DownloadCountingOutputStream(os))
        }
      }
    } finally {
      inStream.close()
      console.endProgress()
    }
  }
    
  URLConnection openConnection(URL url){
    String protocol = url.getProtocol()
    String user = System.getProperty("${protocol}.proxyUser")
    String pw = System.getProperty("${protocol}.proxyPassword")
    def connection = url.openConnection();
    if(user != null && pw != null ){
      connection.setRequestProperty(
          "Proxy-Authorization",
          "Basic " + Base64.getEncoder().encodeToString("${user}:${pw}".getBytes()));
    }
    return connection
  }
}
