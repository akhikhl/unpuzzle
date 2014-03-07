/*
 * untangle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package org.akhikhl.untangle.utils

/**
 * Console interface used by ArchivePacker and Downloader.
 * @author Andrey Hihlovskiy
 */
interface IConsole {

  void endProgress()

  void info(String message)

  void progress(String message)

  void progressError(String message)

  void startProgress(String message)
}
