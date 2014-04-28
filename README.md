# Unpuzzle 
[![Maintainer Status](http://stillmaintained.com/akhikhl/unpuzzle.png)](http://stillmaintained.com/akhikhl/unpuzzle) 
[![Build Status](https://travis-ci.org/akhikhl/unpuzzle.png?branch=master)](https://travis-ci.org/akhikhl/unpuzzle) 
[![Latest Version](http://img.shields.io/badge/latest_version-0.0.4-blue.svg)](https://github.com/akhikhl/unpuzzle/tree/v0.0.4) 
[![License](http://img.shields.io/badge/license-MIT-ff69b4.svg)](#copyright-and-license)

**Unpuzzle** is a set of tools for mavenizing OSGi-bundles.

You can consume Unpuzzle in two forms: as a [gradle plugin](#gradle-plugin) and as an [ordinary jar-library](#jar-library-api).

All versions of Unpuzzle are available in maven central under the group 'org.akhikhl.unpuzzle'.

**Content of this document**

1. [What "mavenizing" means?](#what-mavenizing-means)
2. [Gradle plugin](#gradle-plugin)
3. [Gradle tasks](#gradle-tasks)
  - [downloadEclipse](#downloadeclipse)
  - [installEclipse](#installeclipse)  
  - [uninstallEclipse](#uninstalleclipse)  
  - [uploadEclipse](#uploadeclipse)
  - [cleanUnpuzzle](#cleanunpuzzle)
4. [Gradle plugin extension](#gradle-plugin-extension)
5. [uploadEclipse configuration](#uploadeclipse-configuration)
6. [Jar-library API](#jar-library-api)
7. [Copyright and License](#copyright-and-license)

## What "mavenizing" means?

When OSGi-bundle is being "mavenized", the following happens:

- The program generates pom.xml for every OSGi-bundle (of eclipse distribution, for example).
  The generated pom.xml contains maven coordinates "group:artifact:version", 
  where "group" is constant (could be "eclipse", for example), "artifact" corresponds
  to OSGi-bundle name, "version" corresponds to OSGi-bundle version.

- The program compares every required-bundle of the given OSGi-bundle against 
  other mavenized OSGi-bundles and, when match found, converts it to maven dependency.
  
- The program automatically finds language-fragments of the given OSGi-bundle 
  and adds them as optional maven dependencies.
  
- When the program has access to source OSGi-bundles (from eclipse-SDK, for example),
  it automatically adds them as source-jars to their master mavenized OSGi-bundles.
  
- The program publishes mavenized OSGi-bundles to maven repository, 
  either local ($HOME/.m2/repository) or remote. Of course, you are in control 
  of which repository is used for publishing.
  
As the result, you get complete and consistent representation of OSGi-bundles
as a set of maven artifacts with dependencies. Combined with maven or gradle, 
it can greatly simplify building OSGi/eclipse applications.

## Gradle plugin

Add the following to "build.gradle":

```groovy
apply from: 'https://raw.github.com/akhikhl/unpuzzle/master/pluginScripts/unpuzzle.plugin'
```

then do "gradle installEclipse" from command-line. This will download eclipse
from it's distribution site and install eclipse plugins to the local maven repository:
$HOME/.m2/repository, into maven group "eclipse-kepler".

Alternatively, you can download the script from https://raw.github.com/akhikhl/unpuzzle/master/pluginScripts/unpuzzle.plugin 
to the project folder and include it like this:

```groovy
apply from: 'unpuzzle.plugin'
```

or feel free copying (and modifying) the declarations from this script to your "build.gradle".

## Gradle tasks

### downloadEclipse

**downloadEclipse** task downloads eclipse distribution from the official site,
then unpacks it to the $HOME/.unpuzzle folder. 

By default Unpuzzle downloads eclipse kepler SR1, delta-pack and eclipse-SDK. You can fine-tune, which version of eclipse is downloaded and with which add-ons by providing your own [configuration](#gradle-plugin-extension).

Before downloading a distribution package this task compares file size to the one returned by HTTP HEAD request. If file size did not change, no download is performed.

**Hint**: you can force re-download of eclipse distribution simply by deleting *.zip and *.tar.gz files in the folder $HOME/.unpuzzle/downloaded.

### installEclipse

**installEclipse** task mavenizes all OSGi-bundles of the downloaded eclipse distribution 
and installs the generated maven artifacts to local maven repository ($HOME/.m2/repository).

By default all OSGi-bundles are installed into "eclipse-kepler" maven group.
You can define other maven group by providing your own [configuration](#gradle-plugin-extension).

installEclipse task depends on [downloadEclipse](#downloadeclipse] task.

### uninstallEclipse

**uninstallEclipse** task uninstalls installed OSGi-bundles of the downloaded eclipse distribution from the local maven repository ($HOME/.m2/repository).

### uploadEclipse

**uploadEclipse** task mavenizes all OSGi-bundles of the downloaded eclipse distribution and installs the generated maven artifacts to remote maven repository.

You should specify [uploadEclipse configuration](#uploadEclipse-configuration] in order to make uploadEclipse work.

By default all OSGi-bundles are installed into "eclipse-kepler" maven group.
You can define other maven group by providing your own [configuration](#gradle-plugin-extension).

uploadEclipse task depends on [downloadEclipse](#downloadeclipse] task.

### cleanUnpuzzle

**cleanUnpuzzle** task cleans everything specific to Unpuzzle plugin. Particularly, it uninstalls installed maven artifacts and deletes directory $HOME/.unpuzzle.

## Gradle plugin extension

Unpuzzle works without configuration out of the box. You just apply gradle plugin,
run [installEclipse](#installeclipse) task and Unpuzzle does it's job with reasonable defaults.

However, there are cases when you need to fine-tune Unpuzzle. For example you might
want to download/install another version of eclipse distribution.

Unpuzzle supports the following gradle plugin extension:

```groovy
unpuzzle {

  defaultEclipseVersion = '4.3'

  eclipseVersion('4.3') {

    eclipseMavenGroup = 'eclipse-kepler'

    eclipseMirror = 'http://mirror.netcologne.de'
    
    source "$eclipseMirror/eclipse//technology/epp/downloads/release/kepler/SR1/eclipse-jee-kepler-SR1-linux-gtk-x86_64.tar.gz"
    source "$eclipseMirror/eclipse//eclipse/downloads/drops4/R-4.3.1-201309111000/eclipse-SDK-4.3.1-linux-gtk-x86_64.tar.gz", sourcesOnly: true
    source "$eclipseMirror/eclipse//technology/babel/babel_language_packs/R0.11.1/kepler/BabelLanguagePack-eclipse-de_4.3.0.v20131123020001.zip", languagePacksOnly: true
  }

  uploadEclipse = [
    url: 'http://example.com/repository',
    user: 'someUser',
    password: 'somePassword'
  ]  
}
```
Here is the detailed description of configuration options:

- **defaultEclipseVersion** - string, optional, default value is '4.3'. When specified, defines which version of eclipse is to be downloaded and installed
  by Unpuzzle tasks.
  
- **eclipseVersion** - function(String, Closure), multiplicity 0..n. When called, defines version-specific configuration. Unpuzzle configuration may contain multiple
  version-specific configurations. Only one version-specific configuration is "active" - this is defined by defaultEclipseVersion.

- **eclipseMavenGroup** - string, optional, default value (for version '4.3') is 'eclipse-kepler'.

- **eclipseMirror** - string, optional, default is null. Can be used for specifying common base URL for multiple sources.
  
- **source** - function(Map, String), multiplicity 0..n. Essentially "source" specifies URL
  from which Unpuzzle should download eclipse distribution (or add-on distributions,
  like eclipse-SDK, delta-pack or language-packs). Additionally it acccepts the following properties:
  - **sourcesOnly** - optional, boolean. When specified, signifies whether the given
    distribution package contains only sources. Default value is false.
    Typical use-case: sourcesOnly=true for eclipse-SDK.
  - **languagePacksOnly** - optional, boolean. When specified, signifies whether the given
    distribution package contains only language fragments. Default value is false.
    Typical use-case: languagePacksOnly=true for eclipse language packs.
    
- **uploadEclipse** - optional, hashmap. See more information at [uploadEclipse configuration](#uploadeclipse-configuration).     
    
Additionally the following properties are injected into version-specific configuration
and can be used for calculating correct version of eclipse to download:

- **current_os** - string, assigned to 'linux' or 'windows', depending on the current operating system.

- **current_arch** - string assigned to 'x86_32' or 'x86_64', depending on the current processor architecture.
    
You can see the complete and working configuration at https://github.com/akhikhl/unpuzzle/blob/master/libs/unpuzzle-plugin/src/main/resources/org/akhikhl/unpuzzle/defaultConfig.groovy

## uploadEclipse configuration

In order to upload mavenized OSGi bundles to remote repository you need to specify
three parameters: remote repository URL, user name and password.
All three can be specified in one of three places, in the following priority order:

- in "build.gradle" of the current project (project where unpuzzle gradle-plugin is being applied):
```groovy
unpuzzle {
  // ...
  uploadEclipse = [
    url: 'http://example.com/repository',
    user: 'someUser',
    password: 'somePassword'
  ]
}
```
- in "build.gradle" of the root project (in case of multiproject build):
```groovy
ext {
  // ...
  uploadEclipse = [
    url: 'http://example.com/repository',
    user: 'someUser',
    password: 'somePassword'
  ]
}
```
- in "init.gradle" script:
```groovy
projectsEvaluated {
  rootProject.ext {
    uploadEclipse = [
      url: 'file:///home/ahi/repository',
      user: 'someUser',
      password: 'somePassword'
    ]
  }
}
```

It is probably not good idea to store sensitive information (like user names and passwords)
within the source code of your project. Consider: if you store the source code 
in the version control system, everybody authorized to see the sources effectively 
gets he credentials to upload to your maven repository.

A healthy decision would be to use the last option - store user name
and password in "init.gradle" script outside of the project. See more information
about init scripts in [official gradle documentation](http://www.gradle.org/docs/current/userguide/init_scripts.html).

## Jar library API

Gradle plugin might be sufficient for the most use-cases requiring mavenizing OSGi-bundles.
However, you can mavenize OSGi-bundles even without gradle plugin, just by using Unpuzzle API functions.

Good example of Unpuzzle API usage is given in the file https://github.com/akhikhl/unpuzzle/blob/master/examples/deployEclipseKepler/build.gradle

Essentially, Unpuzzle API consists of four classes:

- [EclipseDownloader](http://akhikhl.github.io/unpuzzle/groovydoc/unpuzzle-eclipse2maven/org/akhikhl/unpuzzle/eclipse2maven/EclipseDownloader.html), 
  implements downloading and unpacking the specified set of sources.

- [Deployer](http://akhikhl.github.io/unpuzzle/groovydoc/unpuzzle-osgi2maven/org/akhikhl/unpuzzle/osgi2maven/Deployer.html), 
  implements deployment of single jar or directory with the specified POM to the specified repository.

- [EclipseDeployer](http://akhikhl.github.io/unpuzzle/groovydoc/unpuzzle-eclipse2maven/org/akhikhl/unpuzzle/eclipse2maven/EclipseDeployer.html), 
  implements dependency resolution and deployment of multiple OSGi bundles
  to the specified maven group and specified Deployer.
  
- [EclipseSource](http://akhikhl.github.io/unpuzzle/groovydoc/unpuzzle-eclipse2maven/org/akhikhl/unpuzzle/eclipse2maven/EclipseSource.html), 
  simple POJO class, storing information on download source.

## Copyright and License

Copyright 2014 (c) Andrey Hihlovskiy

All versions, present and past, of Unpuzzle are licensed under [MIT license](https://github.com/akhikhl/unpuzzle/blob/master/license.txt).

