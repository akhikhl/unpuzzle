/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle.utils

import java.io.IOException
import java.io.OutputStream

/**
 * Decorator stream, counting the bytes being written.
 * @author akhikhl
 */
class CountingOutputStream extends OutputStream {

  private final OutputStream proxy
  private long count = 0

  CountingOutputStream(final OutputStream out) {
    assert out != null
    this.proxy = out
  }

  protected void afterWrite(int n) throws IOException {
  }

  void reset() {
    this.count = 0
  }

  long getCount() {
    if (count >= 0 && count <= Long.MAX_VALUE)
      return count
    throw new IllegalStateException("out bytes exceeds Long.MAX_VALUE: $count")
  }

  @Override
  void write(int b) throws IOException {
    ++count
    proxy.write(b)
    afterWrite(1)
  }

  @Override
  void write(byte[] b, int off, int len) throws IOException {
    count += len
    proxy.write(b, off, len)
    afterWrite(len)
  }

  @Override
  void write(byte[] b) throws IOException {
    count += b.length
    proxy.write(b)
    afterWrite(b.length)
  }

  @Override
  void close() throws IOException {
    proxy.close()
  }

  @Override
  void flush() throws IOException {
    proxy.flush()
  }
}
