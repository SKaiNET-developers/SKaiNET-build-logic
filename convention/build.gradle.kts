plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.vanniktech.mavenPublish)
}

repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
}

// group/version come from the root build.gradle.kts's allprojects{} block (gradle.properties-
// driven) -- not set here, single source of truth.

dependencies {
    // compileOnly, never implementation: these convention plugins call
    // `pluginManager.apply("org.jetbrains.kotlin.multiplatform")` / configure the AGP KMP
    // extension by *type*, but never bundle a specific KGP/AGP jar of their own — the real
    // runtime plugin (and its version) is whatever the consuming build's own root
    // `plugins { alias(...) apply false }` resolves. compileOnly gives these plugins the types
    // to compile against while deferring to the classes the consuming build actually loads;
    // `implementation` here would risk a second copy of KGP/AGP on the classpath in a different
    // classloader, turning every `getByType(SomeKgpType::class)` into a ClassCastException.
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.android.gradlePlugin)
    compileOnly("org.jetbrains.dokka:dokka-gradle-plugin:${libs.versions.dokka.get()}")
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

gradlePlugin {
    // sk.ainet.dokka (the precompiled sk.ainet.dokka.gradle.kts script plugin under
    // src/main/kotlin) is auto-registered by the kotlin-dsl plugin itself -- no entry needed here.
    plugins {
        register("SkainetMultiplatform") {
            id = "sk.ainet.multiplatform"
            implementationClass = "sk.ainet.buildlogic.kmp.SkainetMultiplatformPlugin"
        }
        register("SkainetMavenPins") {
            id = "sk.ainet.maven-pins"
            implementationClass = "sk.ainet.buildlogic.maven.MavenPinsPlugin"
        }
        register("SkainetNpmPins") {
            id = "sk.ainet.npm-pins"
            implementationClass = "sk.ainet.buildlogic.npm.NpmPinsPlugin"
        }
        register("SkainetBomCoverage") {
            id = "sk.ainet.transformers.bom-coverage"
            implementationClass = "sk.ainet.buildlogic.bom.BomCoveragePlugin"
        }
    }
}

// Real Maven Central publish (unlike the engine repo's build-logic, which is includeBuild-only
// and never published) -- java-gradle-plugin + vanniktech together produce both the
// implementation artifact and the Gradle plugin-marker POMs automatically, resolvable from a
// plain `pluginManagement { repositories { mavenCentral() } }` with no Plugin Portal credential.
// Configured entirely via root gradle.properties (mavenCentralPublishing/signAllPublications/...)
// -- same convention every other repo in this org uses, no mavenPublishing{} DSL block needed.
