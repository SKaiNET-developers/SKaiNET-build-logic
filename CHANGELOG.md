# Changelog

All notable changes to **SKaiNET-build-logic** are documented here.

The format roughly follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.1.0] — 2026-09-12

### Added

- `sk.ainet.multiplatform` gains `skainet { jvmTarget = ... }`, centralizing the plain `jvm()`
  target's compiled bytecode level (default `JvmTarget.JVM_17`). Previously unset entirely, so
  the bytecode level silently followed whatever JDK happened to run the build (a consumer's
  local machine, or whatever a CI workflow's `setup-java` step pinned) instead of being a
  deliberate, version-controlled decision — the exact kind of implicit per-repo drift this
  plugin exists to eliminate. Verified end-to-end against `SKaiNET-audio`: compiled class files
  now carry major version 61 (Java 17) regardless of the JDK 25 host running the build.

## [1.0.0] — 2026-09-12

First release. Five Gradle convention plugins extracted wholesale from the SKaiNET engine
repo's own in-tree `build-logic` module, published for real to Maven Central instead of the
source-only `includeBuild` consumption that made them impractical to share across repos —
`SKaiNET-transformers` had already independently reimplemented one of them
(`sk.ainet.transformers.bom-coverage`, as its own `buildSrc` plugin) rather than adopt the
engine's version, because a full sibling checkout of the entire engine repo just to get one
small plugin was never a realistic ask.

### Added

- `sk.ainet.multiplatform` — standard KMP module setup: `skainet.targets` gradle property
  expands friendly group names (`apple`, `linux`, `androidNative`, ...) to real Kotlin targets,
  plus `android {}` block wiring, `explicitApi()`, `kotlin-test` in `commonTest`, and Karma test
  hardening for JS/Wasm.
- `sk.ainet.maven-pins` — root-only `skainet { mavenPins { pin(...) } }`, forces a Maven/JVM
  coordinate to an audited version across every subproject's dependency graph; `verifyMavenPins`
  (wired into `check`) fails if a pin stops resolving to its declared version.
- `sk.ainet.npm-pins` — the same mechanism for npm/Yarn transitive dependencies, across both
  the Kotlin/JS and Kotlin/Wasm `yarn.lock` graphs at once.
- `sk.ainet.transformers.bom-coverage` — auto-discovers every subproject applying
  `com.vanniktech.maven.publish` into a BOM platform; reconciles the engine's original
  implementation with `SKaiNET-transformers`' independently-added POM-completeness fail-fast
  check (both kept, not one picked over the other).
- `sk.ainet.dokka` — standard Dokka wiring, genericized from the engine's own version (the one
  engine-specific bit, a hardcoded source-link URL, is now a `skainet.dokka.sourceRepoUrl`
  gradle property).

Left behind in the engine repo as confirmed engine-specific: `GenerateKernelMatrixTask`,
`KernelSupportModels`, `DocumentationModels`, `SchemaValidationTask`, `JsonValidator`,
`sk.ainet.documentation` (operator/kernel documentation generation tied to SKaiNET's own
KSP-emitted compute-operator metadata).
