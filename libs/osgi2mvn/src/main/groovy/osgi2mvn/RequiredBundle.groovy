/*
 * osgi2mvn
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "license.txt" for copying and usage permission.
 */
package osgi2mvn

/**
 * POJO class holding data on required bundle. Used by {@link osgi2mvn.Pom} class.
 * @author ahi
 * @see osgi2mvn.Pom
 */
final class RequiredBundle {

	String name
	String version
	String visibility
	String resolution

	String toString() {
		return "name: $name; version: $version; visibility: $visibility; resolution: $resolution"
	}
}

