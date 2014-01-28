/*
 * mavenize
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package org.akhikhl.mavenize.downloadhelpers

import java.io.IOException

import org.apache.commons.lang3.StringUtils

/**
 * Console implementation, writes everything to System.out.
 * @author Andrey Hihlovskiy
 */
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
  void progressError(String message) {
    try {
      System.out.println(StringUtils.rightPad(message, 60, ' '))
    } catch (IOException e) {
      throw new RuntimeException(e)
    }
  }

  @Override
  void startProgress(String message) {
    System.out.println(message)
  }
}
