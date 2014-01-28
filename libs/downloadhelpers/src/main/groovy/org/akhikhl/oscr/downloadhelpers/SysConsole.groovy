/*
 * osgi2mvn
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package org.akhikhl.oscr.downloadhelpers

import java.io.IOException

import org.apache.commons.lang3.StringUtils

final class SysConsole implements IConsole {

  @Override
  void endProgress() {
    System.out.println()
  }

  @Override
  void info(String message) {
    System.out.println(message)
  }

  @Override
  void progress(String message) {
    try {
      System.out.write(('\r' + StringUtils.rightPad(message, 60, ' ')).getBytes())
    } catch (IOException e) {
      throw new RuntimeException(e)
    }
    System.out.flush()
  }

  @Override
  void startProgress(String message) {
    System.out.println(message)
  }
}
