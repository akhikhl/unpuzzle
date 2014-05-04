/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle

import org.gradle.api.*
import org.gradle.api.plugins.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.*

/**
 * Gradle plugin for mavenizing eclipse
 * @author akhikhl
 */
class UnpuzzlePlugin implements Plugin<Project> {

  void apply(Project project) {
    new Configurer(project).apply()
  } // apply
}
