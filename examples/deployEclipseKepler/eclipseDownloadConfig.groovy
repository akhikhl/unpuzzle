group = 'eclipse-kepler'

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
source "$eclipseMirror/eclipse//technology/babel/babel_language_packs/R0.11.1/kepler/BabelLanguagePack-eclipse-de_4.3.0.v20131123020001.zip", languagePacksOnly: true
