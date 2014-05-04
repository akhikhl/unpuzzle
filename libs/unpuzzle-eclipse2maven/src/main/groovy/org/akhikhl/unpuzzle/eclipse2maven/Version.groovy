/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle.eclipse2maven

import org.akhikhl.unpuzzle.osgi2maven.Pom

/**
 * OSGi-specific version.
 * @author akhikhl
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

