/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle.eclipse2maven

/**
 * POJO class holding information about eclipse distribution.
 * @author Andrey Hihlovskiy
 */
final class EclipseSource {
  String url
  boolean sourcesOnly = false
  boolean languagePacksOnly = false
}

