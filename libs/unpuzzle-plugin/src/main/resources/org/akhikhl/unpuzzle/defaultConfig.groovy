unpuzzle {

  selectedEclipseVersion = '4.3'

  eclipseVersion('4.3') {

    eclipseMavenGroup = 'eclipse-kepler'

    eclipseMirror = 'http://mirror.netcologne.de'

    eclipseArchiveMirror = 'http://archive.eclipse.org'

    sources {

      def suffix_os = [ 'linux': 'linux-gtk', 'windows': 'win32' ]
      def suffix_arch = [ 'x86_32': '', 'x86_64': '-x86_64' ]
      def fileExt_os = [ 'linux': 'tar.gz', 'windows': 'zip' ]

      source "$eclipseMirror/eclipse//technology/epp/downloads/release/kepler/SR2/eclipse-jee-kepler-SR2-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}"
      source "$eclipseMirror/eclipse//eclipse/downloads/drops4/R-4.3.2-201402211700/eclipse-SDK-4.3.2-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}", sourcesOnly: true
      source "$eclipseMirror/eclipse//eclipse/downloads/drops4/R-4.3.2-201402211700/eclipse-4.3.2-delta-pack.zip"

      languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/kepler/BabelLanguagePack-eclipse-${language}_4.3.0.v20131123020001.zip'
    }
  }

  eclipseVersion('4.2') {

    eclipseMavenGroup = 'eclipse-juno'

    eclipseMirror = 'http://mirror.netcologne.de'

    eclipseArchiveMirror = 'http://archive.eclipse.org'

    sources {

      def suffix_os = [ 'linux': 'linux-gtk', 'windows': 'win32' ]
      def suffix_arch = [ 'x86_32': '', 'x86_64': '-x86_64' ]
      def fileExt_os = [ 'linux': 'tar.gz', 'windows': 'zip' ]

      source "$eclipseMirror/eclipse//technology/epp/downloads/release/juno/SR2/eclipse-jee-juno-SR2-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}"
      source "$eclipseMirror/eclipse//eclipse/downloads/drops4/R-4.2.2-201302041200/eclipse-SDK-4.2.2-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}", sourcesOnly: true
      source "$eclipseMirror/eclipse//eclipse/downloads/drops4/R-4.2.2-201302041200/eclipse-4.2.2-delta-pack.zip"

      languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/juno/BabelLanguagePack-eclipse-${language}_4.2.0.v20131123041006.zip'
      languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/juno/BabelLanguagePack-rt.equinox-${language}_4.2.0.v20131123041006.zip'
    }
  }

  eclipseVersion('3.7') {

    eclipseMavenGroup = 'eclipse-indigo'

    eclipseMirror = 'http://mirror.netcologne.de'

    eclipseArchiveMirror = 'http://archive.eclipse.org'

    sources {

      def suffix_os = [ 'linux': 'linux-gtk', 'windows': 'win32' ]
      def suffix_arch = [ 'x86_32': '', 'x86_64': '-x86_64' ]
      def fileExt_os = [ 'linux': 'tar.gz', 'windows': 'zip' ]

      source "$eclipseMirror/eclipse//technology/epp/downloads/release/indigo/SR2/eclipse-jee-indigo-SR2-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}"
      source "$eclipseArchiveMirror/eclipse/downloads/drops/R-3.7.2-201202080800/eclipse-SDK-3.7.2-${suffix_os[current_os]}${suffix_arch[current_arch]}.${fileExt_os[current_os]}", sourcesOnly: true
      source "$eclipseArchiveMirror/eclipse/downloads/drops/R-3.7.2-201202080800/eclipse-3.7.2-delta-pack.zip"

      languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/indigo/BabelLanguagePack-eclipse-${language}_3.7.0.v20131123061707.zip'
      languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/indigo/BabelLanguagePack-rt.equinox-${language}_3.7.0.v20131123061707.zip'
    }
  }
}
