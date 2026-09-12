[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/sk.ainet.buildlogic/sk.ainet.multiplatform.gradle.plugin.svg)](https://central.sonatype.com/artifact/sk.ainet.buildlogic/sk.ainet.multiplatform.gradle.plugin)

# SKaiNET-build-logic

Shared Gradle convention plugins for `SKaiNET-developers` org repos — extracted from the
[SKaiNET engine repo's](https://github.com/SKaiNET-developers/SKaiNET) own in-tree `build-logic`
module so they're consumable as real, version-pinned dependencies instead of requiring a sibling
checkout of the entire engine repo via `includeBuild`. That distribution gap is exactly why it
was never actually shared before this repo existed: the engine's own
`sk.ainet.transformers.bom-coverage` plugin is literally named for `SKaiNET-transformers` as its
intended consumer, but that repo ended up independently reimplementing the same ~70 lines in its
own `buildSrc` instead, because `includeBuild`-only consumption was impractical.

## Plugins

| Plugin ID | What it does |
|---|---|
| `sk.ainet.multiplatform` | Standard KMP module setup: `skainet { targets = "jvm,apple,linux,..." }` expands friendly group names to real Kotlin targets (`apple`→`iosArm64,iosSimulatorArm64,macosArm64`, `linux`→`linuxX64,linuxArm64`, `androidNative`→`androidNativeArm32,androidNativeArm64`), plus `android {}` block wiring, `explicitApi()`, `kotlin-test` in `commonTest`, and Karma test hardening for JS/Wasm. |
| `sk.ainet.maven-pins` | Root-only. `skainet { mavenPins { pin("group:artifact", version) } }` forces a Maven/JVM coordinate to an audited version across every subproject's dependency graph — the mechanism for silencing a transitive-dependency CVE that has no direct upgrade path. `verifyMavenPins` (wired into `check`) fails if a pin stops resolving to its declared version. |
| `sk.ainet.npm-pins` | Same idea for npm/Yarn transitive dependencies, across both the Kotlin/JS and Kotlin/Wasm `yarn.lock` graphs at once (one declaration, both lockfiles). Required at the root project whenever any module builds `js`/`wasmJs` targets — `sk.ainet.multiplatform` enforces this at configuration time. |
| `sk.ainet.transformers.bom-coverage` | Auto-discovers every subproject applying `com.vanniktech.maven.publish` and adds it as an `api` platform constraint on a `java-platform` BOM module — no manual module list to maintain as new publishable modules are added. Fails fast if a published module is missing `POM_ARTIFACT_ID`/`POM_NAME` (would otherwise fail silently or wrongly at Maven Central deploy time). |
| `sk.ainet.dokka` | Standard Dokka wiring (module name/version, native-cinterop source-set suppression, shared jvm/android source-set dedup) with source-linking to a configurable `skainet.dokka.sourceRepoUrl` gradle property. |

## Using

```kotlin
// module build.gradle.kts
plugins {
    id("sk.ainet.multiplatform") version "1.0.0"
    alias(libs.plugins.androidMultiplatformLibrary)   // opt into Android, applied by the consumer
}

skainet {
    namespace = "sk.ainet.yourlib.core"
    targets = "jvm,apple,linux,js,wasmJs"
}
```

No `includeBuild`, no sibling checkout — resolves from `pluginManagement { repositories {
mavenCentral() } }` like any other published Gradle plugin.

## Building

```sh
./gradlew build              # full build
./gradlew publishToMavenLocal
```

## Publishing

Same gitflow as every other repo in this org: branch `release/X.Y.Z` off `develop`, bump
`VERSION_NAME` in `gradle.properties`, merge to `develop`, tag the merge commit, push the tag —
`.github/workflows/publish.yml` builds and publishes to Maven Central on any pushed tag.
