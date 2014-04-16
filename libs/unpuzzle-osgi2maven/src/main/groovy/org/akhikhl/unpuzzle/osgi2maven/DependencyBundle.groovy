/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle.osgi2maven

import groovy.transform.ToString

/**
 * POJO class holding data on dependency bundle. Used by {@link org.akhikhl.unpuzzle.osgi2maven.Pom} class.
 * @author akhikhl
 */
@ToString
final class DependencyBundle {
  String group
	String name
	String version
	String visibility
	String resolution
}

