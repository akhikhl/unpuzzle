/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle

import org.gradle.api.Project

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Project-centric utilities
 *
 * @author akhikhl
 */
final class ProjectUtils {

  private static final Logger log = LoggerFactory.getLogger(ProjectUtils)

  /**
   * Collects all ancestors + the given project.
   *
   * @param project project being analyzed, not modified.
   * @return list of projects, first element is root, last element is the given project.
   */
  static List<Project> collectWithAllAncestors(Project project) {
    List<Project> projects = []
    Project p = project
    while(p != null) {
      projects.add(0, p)
      p = p.parent
    }
    return projects
  }

  static findResultUpAncestorChain(Project project, Closure closure) {
    Project p = project
    while(p != null) {
      def result = closure(p)
      if(result)
        return result
      p = p.parent
    }
    return null
  }
}
