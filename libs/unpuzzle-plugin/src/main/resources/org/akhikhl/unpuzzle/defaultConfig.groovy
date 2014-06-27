unpuzzle {

  unpuzzleDir = new File(System.getProperty('user.home'), '.unpuzzle')

  localMavenRepositoryDir = new File(unpuzzleDir, 'm2_repository')

  selectedEclipseVersion = '4.3.2'

  def suffix_os = [ 'linux': 'linux-gtk', 'macosx': 'macosx-cocoa', 'windows': 'win32' ]
  def suffix_arch = [ 'x86_32': '', 'x86_64': '-x86_64' ]
  def fileExt_os = [ 'linux': 'tar.gz', 'macosx': 'tar.gz', 'windows': 'zip' ]

  eclipseVersion('3.7.1') {

    eclipseMavenGroup = 'eclipse-indigo-sr1'

    eclipseMirror = 'http://mirror.netcologne.de'

    eclipseArchiveMirror = 'http://archive.eclipse.org'

    sources {

      source "$eclipseArchiveMirror/technology/epp/downloads/release/indigo/SR1/eclipse-jee-indigo-SR1-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}"
      source "$eclipseArchiveMirror/eclipse/downloads/drops/R-3.7.1-201109091335/eclipse-SDK-3.7.1-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}", sourcesOnly: true
      source "$eclipseArchiveMirror/eclipse/downloads/drops/R-3.7.1-201109091335/eclipse-3.7.1-delta-pack.zip"

      languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/indigo/BabelLanguagePack-eclipse-${language}_3.7.0.v20131123061707.zip'
      languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/indigo/BabelLanguagePack-rt.equinox-${language}_3.7.0.v20131123061707.zip'
    }
  }

  eclipseVersion('3.7.2') {

    eclipseMavenGroup = 'eclipse-indigo-sr2'

    eclipseMirror = 'http://mirror.netcologne.de'

    eclipseArchiveMirror = 'http://archive.eclipse.org'

    sources {

      source "$eclipseMirror/eclipse//technology/epp/downloads/release/indigo/SR2/eclipse-jee-indigo-SR2-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}"
      source "$eclipseArchiveMirror/eclipse/downloads/drops/R-3.7.2-201202080800/eclipse-SDK-3.7.2-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}", sourcesOnly: true
      source "$eclipseArchiveMirror/eclipse/downloads/drops/R-3.7.2-201202080800/eclipse-3.7.2-delta-pack.zip"

      languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/indigo/BabelLanguagePack-eclipse-${language}_3.7.0.v20131123061707.zip'
      languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/indigo/BabelLanguagePack-rt.equinox-${language}_3.7.0.v20131123061707.zip'
    }
  }

  eclipseVersion('4.2.1') {

    eclipseMavenGroup = 'eclipse-juno-sr1'

    eclipseMirror = 'http://mirror.netcologne.de'

    eclipseArchiveMirror = 'http://archive.eclipse.org'

    sources {

      source "$eclipseArchiveMirror/technology/epp/downloads/release/juno/SR1/eclipse-jee-juno-SR1-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}"
      source "$eclipseArchiveMirror/eclipse/downloads/drops4/R-4.2.1-201209141800/eclipse-SDK-4.2.1-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}", sourcesOnly: true
      source "$eclipseArchiveMirror/eclipse/downloads/drops4/R-4.2.1-201209141800/eclipse-4.2.1-delta-pack.zip"

      languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/juno/BabelLanguagePack-eclipse-${language}_4.2.0.v20131123041006.zip'
      languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/juno/BabelLanguagePack-rt.equinox-${language}_4.2.0.v20131123041006.zip'
    }
  }

  eclipseVersion('4.2.2') {

    eclipseMavenGroup = 'eclipse-juno-sr2'

    eclipseMirror = 'http://mirror.netcologne.de'

    eclipseArchiveMirror = 'http://archive.eclipse.org'

    sources {

      source "$eclipseMirror/eclipse//technology/epp/downloads/release/juno/SR2/eclipse-jee-juno-SR2-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}"
      source "$eclipseMirror/eclipse//eclipse/downloads/drops4/R-4.2.2-201302041200/eclipse-SDK-4.2.2-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}", sourcesOnly: true
      source "$eclipseMirror/eclipse//eclipse/downloads/drops4/R-4.2.2-201302041200/eclipse-4.2.2-delta-pack.zip"

      languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/juno/BabelLanguagePack-eclipse-${language}_4.2.0.v20131123041006.zip'
      languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/juno/BabelLanguagePack-rt.equinox-${language}_4.2.0.v20131123041006.zip'
    }
  }

  eclipseVersion('4.3.1') {

    eclipseMavenGroup = 'eclipse-kepler-sr1'

    eclipseMirror = 'http://mirror.netcologne.de'

    eclipseArchiveMirror = 'http://archive.eclipse.org'

    sources {

      source "$eclipseMirror/eclipse//technology/epp/downloads/release/kepler/SR1/eclipse-jee-kepler-SR1-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}"
      source "$eclipseMirror/eclipse//eclipse/downloads/drops4/R-4.3.1-201309111000/eclipse-SDK-4.3.1-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}", sourcesOnly: true
      source "$eclipseMirror/eclipse//eclipse/downloads/drops4/R-4.3.1-201309111000/eclipse-4.3.1-delta-pack.zip"

      languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/kepler/BabelLanguagePack-eclipse-${language}_4.3.0.v20131123020001.zip'
    }
  }
    
  eclipseVersion('4.3.2') {

    eclipseMavenGroup = 'eclipse-kepler-sr2'

    eclipseMirror = 'http://mirror.netcologne.de'

    eclipseArchiveMirror = 'http://archive.eclipse.org'

    sources {

      source "$eclipseMirror/eclipse//technology/epp/downloads/release/kepler/SR2/eclipse-jee-kepler-SR2-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}"
      source "$eclipseMirror/eclipse//eclipse/downloads/drops4/R-4.3.2-201402211700/eclipse-SDK-4.3.2-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}", sourcesOnly: true
      source "$eclipseMirror/eclipse//eclipse/downloads/drops4/R-4.3.2-201402211700/eclipse-4.3.2-delta-pack.zip"

      languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/kepler/BabelLanguagePack-eclipse-${language}_4.3.0.v20131123020001.zip'
    }
  }

  eclipseVersion('4.4') {

    eclipseMavenGroup = 'eclipse-luna'

    eclipseMirror = 'http://mirror.netcologne.de'

    eclipseArchiveMirror = 'http://archive.eclipse.org'

    sources {

      source "$eclipseMirror/eclipse//technology/epp/downloads/release/luna/R/eclipse-jee-luna-R-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}"
      source "$eclipseMirror/eclipse//eclipse/downloads/drops4/R-4.4-201406061215/eclipse-SDK-4.4-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}", sourcesOnly: true
      source "$eclipseMirror/eclipse//eclipse/downloads/drops4/R-4.4-201406061215/eclipse-4.4-delta-pack.zip"

      languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.12.0/luna/BabelLanguagePack-eclipse-${language}_4.4.0.v20140623020002.zip'
    }
  }  
}
