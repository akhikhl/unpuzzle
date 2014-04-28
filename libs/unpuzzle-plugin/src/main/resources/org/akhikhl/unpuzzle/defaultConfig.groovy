unpuzzle {

  defaultEclipseVersion = '4.3'

  eclipseVersion('4.3') {

    eclipseMavenGroup = 'eclipse-kepler'

    eclipseMirror = 'http://mirror.netcologne.de'

    def eclipseArchives = [
      'linux_x86_32' : 'eclipse-jee-kepler-SR2-linux-gtk.tar.gz',
      'linux_x86_64' : 'eclipse-jee-kepler-SR2-linux-gtk-x86_64.tar.gz',
      'windows_x86_32' : 'eclipse-jee-kepler-SR2-win32.zip',
      'windows_x86_64' : 'eclipse-jee-kepler-SR2-win32-x86_64.zip'
    ]

    def eclipseSdkArchives = [
      'linux_x86_32' : 'eclipse-SDK-4.3.2-linux-gtk.tar.gz',
      'linux_x86_64' : 'eclipse-SDK-4.3.2-linux-gtk-x86_64.tar.gz',
      'windows_x86_32' : 'eclipse-SDK-4.3.2-win32.zip',
      'windows_x86_64' : 'eclipse-SDK-4.3.2-win32-x86_64.zip'
    ]

    def eclipseArchive = eclipseArchives[current_os + '_' + current_arch]
    def eclipseSdkArchive = eclipseSdkArchives[current_os + '_' + current_arch]

    source "$eclipseMirror/eclipse//technology/epp/downloads/release/kepler/SR2/$eclipseArchive"
    source "$eclipseMirror/eclipse//eclipse/downloads/drops4/R-4.3.2-201402211700/$eclipseSdkArchive", sourcesOnly: true
    source "$eclipseMirror/eclipse//eclipse/downloads/drops4/R-4.3.2-201402211700/eclipse-4.3.2-delta-pack.zip"

    languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/kepler/BabelLanguagePack-eclipse-${language}_4.3.0.v20131123020001.zip'
  }

  eclipseVersion('4.2') {

    eclipseMavenGroup = 'eclipse-juno'

    eclipseMirror = 'http://mirror.netcologne.de'

    def eclipseArchives = [
      'linux_x86_32' : 'eclipse-jee-juno-SR2-linux-gtk.tar.gz',
      'linux_x86_64' : 'eclipse-jee-juno-SR2-linux-gtk-x86_64.tar.gz',
      'windows_x86_32' : 'eclipse-jee-juno-SR2-win32.zip',
      'windows_x86_64' : 'eclipse-jee-juno-SR2-win32-x86_64.zip'
    ]

    def eclipseSdkArchives = [
      'linux_x86_32' : 'eclipse-SDK-4.2.2-linux-gtk.tar.gz',
      'linux_x86_64' : 'eclipse-SDK-4.2.2-linux-gtk-x86_64.tar.gz',
      'windows_x86_32' : 'eclipse-SDK-4.2.2-win32.zip',
      'windows_x86_64' : 'eclipse-SDK-4.2.2-win32-x86_64.zip'
    ]

    def eclipseArchive = eclipseArchives[current_os + '_' + current_arch]
    def eclipseSdkArchive = eclipseSdkArchives[current_os + '_' + current_arch]

    source "$eclipseMirror/eclipse//technology/epp/downloads/release/juno/SR2/$eclipseArchive"
    source "$eclipseMirror/eclipse//eclipse/downloads/drops4/R-4.2.2-201302041200/$eclipseSdkArchive", sourcesOnly: true
    source "$eclipseMirror/eclipse//eclipse/downloads/drops4/R-4.2.2-201302041200/eclipse-4.2.2-delta-pack.zip"

    languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/juno/BabelLanguagePack-eclipse-${language}_4.2.0.v20131123041006.zip'
    languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/juno/BabelLanguagePack-rt.equinox-${language}_4.2.0.v20131123041006.zip'
  }

  eclipseVersion('3.7') {

    eclipseMavenGroup = 'eclipse-indigo'

    eclipseMirror = 'http://mirror.netcologne.de'

    def eclipseArchives = [
      'linux_x86_32' : 'eclipse-jee-indigo-SR2-linux-gtk.tar.gz',
      'linux_x86_64' : 'eclipse-jee-indigo-SR2-linux-gtk-x86_64.tar.gz',
      'windows_x86_32' : 'eclipse-jee-indigo-SR2-win32.zip',
      'windows_x86_64' : 'eclipse-jee-indigo-SR2-win32-x86_64.zip'
    ]

    def eclipseSdkArchives = [
      'linux_x86_32' : 'eclipse-SDK-3.7.2-linux-gtk.tar.gz',
      'linux_x86_64' : 'eclipse-SDK-3.7.2-linux-gtk-x86_64.tar.gz',
      'windows_x86_32' : 'eclipse-SDK-3.7.2-win32.zip',
      'windows_x86_64' : 'eclipse-SDK-3.7.2-win32-x86_64.zip'
    ]

    def eclipseArchive = eclipseArchives[current_os + '_' + current_arch]
    def eclipseSdkArchive = eclipseSdkArchives[current_os + '_' + current_arch]

    source "$eclipseMirror/eclipse//technology/epp/downloads/release/indigo/SR2/$eclipseArchive"
    source "http://archive.eclipse.org/eclipse/downloads/drops/R-3.7.2-201202080800/$eclipseSdkArchive", sourcesOnly: true
    source "http://archive.eclipse.org/eclipse/downloads/drops/R-3.7.2-201202080800/eclipse-3.7.2-delta-pack.zip"

    languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/indigo/BabelLanguagePack-eclipse-${language}_3.7.0.v20131123061707.zip'
  	languagePackTemplate '${eclipseMirror}/eclipse//technology/babel/babel_language_packs/R0.11.1/indigo/BabelLanguagePack-rt.equinox-${language}_3.7.0.v20131123061707.zip'
  }
}
