# mavenize

**mavenize** is a set of tools for mavenizing OSGI bundles.

**Content of this document**

1. [What "mavenize" means?](#what-mavenize-means)
2. [Gradle tasks](#gradle-tasks)
3. [Gradle configuration](#gradle-configuration)
4. [API](#api)

## What "mavenize" means?

Under "mavenizing" OSGi bundles we mean the following:

- Every OSGi bundle (of eclipse distribution, for example) is published to maven repository 
  with coordinates "group:artifact:version", where "group" is fixed 
  (could be "eclipse", for example), "artifact" corresponds
  to OSGi bundle name, "version" corresponds to OSGi bundle version.

- Every required bundle of the given OSGi bundle is matched against other mavenized OSGi bundles
  and, when match found, it is converted to maven dependency.
  
- Every language-fragment OSGi-bundle is added as an optional maven dependency
  to it's master mavenized OSGi bundle.
  
- Every source OSGi-bundle (from eclipse-SDK, for example) is added as sources-jar
  to it's master mavenized OSGi bundle.
  
As the result, you get complete and consistent representation of OSGi bundles
as a set of maven artifacts with dependencies. Combined with maven or gradle, 
it can greatly simplify building OSGi/eclipse applications.

## Gradle tasks

## Gradle configuration

## API

