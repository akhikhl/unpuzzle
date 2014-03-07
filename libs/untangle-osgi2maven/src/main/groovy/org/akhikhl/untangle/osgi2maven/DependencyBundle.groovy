/*
 * untangle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package org.akhikhl.untangle.osgi2maven

import groovy.transform.ToString

/**
 * POJO class holding data on dependency bundle. Used by {@link org.akhikhl.untangle.osgi2maven.Pom} class.
 * @author Andrey Hihlovskiy
 */
@ToString
final class DependencyBundle {
  String group
	String name
	String version
	String visibility
	String resolution
}

