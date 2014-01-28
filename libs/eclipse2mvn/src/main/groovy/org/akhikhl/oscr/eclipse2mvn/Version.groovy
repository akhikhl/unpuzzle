/*
 * oscr
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package org.akhikhl.oscr.eclipse2mvn

import org.akhikhl.oscr.osgi2mvn.Pom

/**
 * OSGi-specific version.
 * @author Andrey Hihlovskiy
 */
final class Version {

  long major = 0, minor = 0, release = 0
  String suffix = ''

  Version(Pom pom) {
    init(pom.version)
  }

  Version(String versionStr) {
    init(versionStr)
  }

  int compare(Version other) {
    int result = major - other.major
    if(result != 0)
      return result
    result = minor - other.minor
    if(result != 0)
      return result
    result = release - other.release
    if(result != 0)
      return result
    return suffix.compareTo(other.suffix)
  }

  private void init(String versionStr) {
    def m = versionStr =~ /(\d+)(\.(\d+))?(\.(\d+))?(\.(.+))?/
    if(m) {
      major = Long.valueOf(m[0][1] ?: '0')
      minor = Long.valueOf(m[0][3] ?: '0')
      release = Long.valueOf(m[0][5] ?: '0')
      suffix = m[0][7] ?: ''
    }
  }
}

