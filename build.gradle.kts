// GROUP / VERSION_NAME come from gradle.properties -- same convention every other repo in the
// org uses, so the vanniktech maven-publish plugin's auto-configuration picks them up directly.
allprojects {
    group = providers.gradleProperty("GROUP").getOrElse("sk.ainet.buildlogic")
    version = providers.gradleProperty("VERSION_NAME").getOrElse("unspecified")
}
