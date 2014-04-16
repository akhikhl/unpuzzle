/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle.utils

/**
 * Console interface used by ArchivePacker and Downloader.
 * @author akhikhl
 */
interface IConsole {

  void endProgress()

  void info(String message)

  void progress(String message)

  void progressError(String message)

  void startProgress(String message)
}
