/*
 * untangle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package org.akhikhl.untangle.eclipse2maven

/**
 * Various utility functions.
 * @author Andrey Hihlovskiy
 */
final class Utils {

  static String getArchiveNameNoExt(String fileName) {
    if(fileName)
      for(String ext in [ '.tar.gz', '.gz', '.tar', '.zip' ])
        if(fileName.endsWith(ext))
          return fileName.substring(0, fileName.length() - ext.length());
    return fileName
  }
}

