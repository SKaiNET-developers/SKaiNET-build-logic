import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier

plugins {
    id("org.jetbrains.dokka")
}

// Generic Dokka wiring, ported from the SKaiNET engine repo's own sk.ainet.dokka precompiled
// script plugin — the only engine-specific bit was a hardcoded source-link URL, now a required
// root-level gradle.property (`skainet.dokka.sourceRepoUrl`, e.g.
// "https://github.com/SKaiNET-developers/SKaiNET-audio/tree/main") instead.
extensions.configure<DokkaExtension> {
    moduleName.set(project.name)
    moduleVersion.set(providers.gradleProperty("VERSION_NAME"))

    dokkaPublications.configureEach {
        suppressInheritedMembers.set(true)
    }

    dokkaSourceSets.configureEach {
        documentedVisibilities(VisibilityModifier.Public)
        suppressGeneratedFiles.set(true)

        // Suppress native source sets that use cinterop — Dokka 2.x cannot translate
        // platform-specific interop symbols (e.g. CoreGraphics, posix).
        val nativeSuffixes = listOf(
            "iosArm64Main", "iosSimulatorArm64Main",
            "macosArm64Main",
            "linuxX64Main", "linuxArm64Main",
        )
        if (nativeSuffixes.any { name.endsWith(it) }) {
            suppress.set(true)
        }

        // Modules with a shared src/jvmAndroidMain source *directory* (#966 upstream) compile the
        // same files into BOTH the jvm and android compilations. Dokka refuses files that belong
        // to two source sets (dokka#3701), and the android pages would duplicate the jvm ones
        // anyway — document the shared API once, via jvm, by suppressing the android source set.
        if ((name == "androidMain" || name == "android") &&
            projectDir.resolve("src/jvmAndroidMain").exists()
        ) {
            suppress.set(true)
        }

        val sourceRepoUrl = providers.gradleProperty("skainet.dokka.sourceRepoUrl").orNull
        if (sourceRepoUrl != null) {
            sourceLink {
                localDirectory.set(projectDir.resolve("src"))
                remoteUrl("$sourceRepoUrl/${project.path.replace(":", "/").removePrefix("/")}/src")
                remoteLineSuffix.set("#L")
            }
        }
    }
}
