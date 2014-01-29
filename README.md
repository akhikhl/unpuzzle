# Mavenize [![Maintainer Status](http://stillmaintained.com/akhikhl/mavenize.png)](http://stillmaintained.com/akhikhl/mavenize) [![Build Status](https://travis-ci.org/akhikhl/mavenize.png?branch=master)](https://travis-ci.org/akhikhl/mavenize) [![endorse](https://api.coderwall.com/akhikhl/endorsecount.png)](https://coderwall.com/akhikhl)

**Mavenize** is a set of tools for mavenizing OSGi-bundles.

You can consume Mavenize in two forms: as a [gradle plugin](#gradle-plugin) and as an [ordinary jar-library](#jar-library-api).

All versions of Mavenize are available in maven central under the group 'org.akhikhl.mavenize'.

**Content of this document**

1. [What "mavenize" means?](#what-mavenize-means)
2. [Gradle plugin](#gradle-plugin)
3. [Gradle tasks](#gradle-tasks)
  - [downloadEclipse](#downloadeclipse)
  - [installEclipse](#installeclipse)  
  - [uploadEclipse](#uploadeclipse)  
4. [Gradle plugin extension](#gradle-plugin-extension)
5. [eclipseUpload configuration](#eclipseupload-configuration)
6. [Jar-library API](#jar-library-api)
7. [Copyright and License](#copyright-and-license)

## What "mavenize" means?

Under "mavenizing" OSGi-bundles we mean the following:

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
apply from: 'https://raw.github.com/akhikhl/mavenize/master/pluginScripts/mavenize.plugin'
```

then do "gradle installEclipse" from command-line. This will download eclipse
from it's distribution site and install eclipse plugins to the local maven repository:
$HOME/.m2/repository.

Alternatively, you can download the script from https://raw.github.com/akhikhl/mavenize/master/pluginScripts/mavenize.plugin 
to the project folder and include it like this:

```groovy
apply from: 'mavenize.plugin'
```

or feel free copying (and modifying) the declarations from this script to your "build.gradle".

## Gradle tasks

### downloadEclipse

**downloadEclipse** task downloads eclipse distribution from the official site,
then unpacks it to the buildDir folder. 

By default Mavenize downloads eclipse kepler SR1, with delta-pack and eclipse-SDK.
You can fine-tune, which version of eclipse is downloaded and with which add-ons
by providing your own [configuration](#gradle-plugin-extension).

If download finishes with success, Mavenize "remembers" it by creating marker file 
"$buildDir/eclipseDownloaded". If downloadEclipse task is invoked again later, 
it will first check whether marker file exists. If it does, the download is skipped.

**Hint**: you can force re-download of eclipse distribution by simply deleting marker file
(or the complete buildDir).

### installEclipse

**installEclipse** task mavenizes all OSGi-bundles of the downloaded eclipse distribution 
and installs the generated maven artifacts to local maven repository ($HOME/.m2/repository).

By default all OSGi-bundles are installed into "eclipse-kepler" maven group.
You can define other maven group by providing your own [configuration](#gradle-plugin-extension).

If installation finishes with success, Mavenize "remembers" it by creating marker file 
"$buildDir/eclipseArtifactsInstalled". If installEclipse task is invoked again later, 
it will first check whether marker file exists. If it does, the installation is skipped.

installEclipse task depends on [downloadEclipse](#downloadeclipse] task.

### uploadEclipse

**uploadEclipse** task mavenizes all OSGi-bundles of the downloaded eclipse distribution 
and installs the generated maven artifacts to remote maven repository.

You should specify [eclipseUpload configuration](#eclipseupload-configuration] in order to make uploadEclipse work.

By default all OSGi-bundles are installed into "eclipse-kepler" maven group.
You can define other maven group by providing your own [configuration](#gradle-plugin-extension).

uploadEclipse task depends on [downloadEclipse](#downloadeclipse] task.

## Gradle plugin extension

Mavenize works without configuration out of the box. You just apply gradle plugin,
invoke [installEclipse](#installeclipse) task and Mavenize does it's job with reasonable defaults.

However, there are cases when you need to fine-tune Mavenize. For example you might
want to change maven group or to download/mavenize/install other version of eclipse distribution.

Mavenize supports the following gradle plugin extension:

```groovy
eclipse2mvn {
  config 'eclipse-kepler'
  group = 'eclipse'
  source 'http://some.url/goes/here.zip', sourcesOnly: false, languagePacksOnly: false
  eclipseUpload = [
    url: 'http://example.com/repository',
    user: 'ahi',
    password: 'ahi123'
  ]  
}
```
Here is the detailed description of all properties:

- **config** - optional, function call. It currently accepts only 'eclipse-kepler' as an argument.
  "config" reconfigures Mavenizer so that it downloads/mavenizes/installs all OSGi bundles
  relevant to the specified configuration. You can slightly augment the configuration
  by providing additional sources. See concrete example at https://github.com/akhikhl/mavenize/tree/master/examples/deployEclipseKeplerViaPlugin
  
- **group** - optional, string. "group" specifies which maven group is assigned
  to all OSGi bundles upon mavenizing. The default value is 'eclipse-kepler'.
  
- **source** - optional, multiple, function call. Basically "source" specifies URL
  from which Mavenizer should download eclipse distribution (or add-on distributions,
  like eclipse-SDK, delta-pack or language-packs). Additionally it acccepts the following properties:
  - **sourcesOnly** - optional, boolean. When specified, signifies whether the given
    distribution package contains only sources or not. Default value is false.
    Typical use-case: sourcesOnly=true for eclipse-SDK.
  - **languagePacksOnly** - optional, boolean. When specified, signifies whether the given
    distribution package contains only language fragments. Default value is false.
    Typical use-case: languagePacksOnly=true for eclipse language packs.
    
- **eclipseUpload** - optional, hashmap. See more information at [eclipseUpload configuration](#eclipseupload-configuration).     
    
Additionally the following properties are injected into eclipse2mvn plugin extension
and can be used for deducting correct version of eclipse to download:

- **current_os** - string, assigned to 'linux' or 'windows', depending on the current operating system.

- **current_arch** - string assigned to 'x86_32' or 'x86_64', depending on the current processor architecture.
    
You can see the complete and working configuration at https://github.com/akhikhl/mavenize/blob/master/libs/gradle-eclipse2mvn/src/main/resources/eclipse-kepler.groovy

## eclipseUpload configuration

In order to upload mavenized OSGi bundles to remote repository you need to specify
three parameters: remote repository URL, user name and password.
All three can be specified in one of three places, in the following priority order:

- in "build.gradle" of the current project (project where gradle-eclipse2mvn plugin is being applied):
```groovy
eclipse2mvn {
  // ...
  eclipseUpload = [
    url: 'http://example.com/repository',
    user: 'ahi',
    password: 'ahi123'
  ]  
}
```
- in "build.gradle" of the current project (project where gradle-eclipse2mvn plugin is being applied):
```groovy
ext {
  // ...
  eclipseUpload = [
    url: 'http://example.com/repository',
    user: 'ahi',
    password: 'ahi123'
  ]  
}
```
- in "build.gradle" of the root project (in case of multiproject build):
```groovy
ext {
  // ...
  eclipseUpload = [
    url: 'http://example.com/repository',
    user: 'ahi',
    password: 'ahi123'
  ]  
}
```
- in "init.gradle" script:
```groovy
projectsEvaluated {
  rootProject.ext {
    eclipseUpload = [
      url: 'file:///home/ahi/repository',
      user: 'ahi',
      password: 'ahi123'
    ]
  }
}
```

It is probably not the best idea to store sensitive information (like user names and passwords)
within the source code of your project. Consider: if you store the source code 
in the version control system, everybody authorized to see the sources effectively 
gets he credentials to upload to your maven repository.

A healthy alternative would be to use the last option - to store user name
and password in "init.gradle" script outside of the project. See more information
about init scripts in [official gradle documentation](http://www.gradle.org/docs/current/userguide/init_scripts.html).

## Jar library API

Gradle plugin might be sufficient for the most use-cases requiring mavenizing OSGi-bundles.
However, you can mavenize OSGi-bundles even without gradle plugin, just by using Mavenize API functions.

Good example of Mavenize API usage is given in the file https://github.com/akhikhl/mavenize/blob/master/examples/deployEclipseKepler/build.gradle

Essentially, Mavenize API consists of four classes:

- [EclipseDownloader](http://akhikhl.github.io/mavenize/groovydoc/eclipse2mvn/org/akhikhl/mavenize/eclipse2mvn/EclipseDownloader.html), 
  implements downloading and unpacking the specified set of sources.

- [Deployer](http://akhikhl.github.io/mavenize/groovydoc/osgi2mvn/org/akhikhl/mavenize/osgi2mvn/Deployer.html), 
  implements deployment of single jar or directory with the specified POM to the specified repository.

- [EclipseDeployer](http://akhikhl.github.io/mavenize/groovydoc/eclipse2mvn/org/akhikhl/mavenize/eclipse2mvn/EclipseDeployer.html), 
  implements dependency resolution and deployment of multiple OSGi bundles
  to the specified maven group and specified Deployer.
  
- [EclipseSource](http://akhikhl.github.io/mavenize/groovydoc/eclipse2mvn/org/akhikhl/mavenize/eclipse2mvn/EclipseSource.html), 
  simple POJO class, storing information on download source.

## Copyright and License

Copyright 2014 (c) Andrey Hihlovskiy

All versions, present and past, of Mavenize are licensed under [MIT license](https://github.com/akhikhl/mavenize/blob/master/license.txt).

