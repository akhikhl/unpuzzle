/*
 * osgi2mvn
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package osgi2mvn

import groovy.transform.ToString

/**
 * POJO class holding data on required bundle. Used by {@link osgi2mvn.Pom} class.
 * @author ahi
 * @see osgi2mvn.Pom
 */
@ToString
final class RequiredBundle {
  String group
	String name
	String version
	String visibility
	String resolution
}

