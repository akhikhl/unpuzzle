/*
 * mavenize
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package org.akhikhl.mavenize.eclipse2mvn

/**
 * POJO class holding information about eclipse distribution.
 * @author Andrey Hihlovskiy
 */
final class EclipseSource {
  String url
  boolean sourcesOnly = false
  boolean languagePacksOnly = false
}

