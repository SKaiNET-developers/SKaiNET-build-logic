package sk.ainet.buildlogic.kmp

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/**
 * The `skainet { }` block contributed by `sk.ainet.multiplatform`.
 *
 * Holds the settings that can be applied *after* the build script has been evaluated.
 * Which platforms a module targets is deliberately **not** here — see [SkainetTargets]
 * for why, and for the `skainet.targets` property that controls it.
 */
abstract class SkainetMultiplatformExtension {

    /**
     * Android namespace. Required when `com.android.kotlin.multiplatform.library` is
     * applied, ignored otherwise.
     *
     * Deliberately explicit rather than derived from the project path: several modules
     * publish namespaces that do not match their path, and changing a published
     * artifact's namespace is a separate decision from adopting this plugin.
     */
    var namespace: String? = null

    /**
     * `jvmTarget` for the Android compilation. Most modules use [JvmTarget.JVM_11]; the
     * `skainet-io` family currently ships [JvmTarget.JVM_1_8] and passes it explicitly.
     */
    var androidJvmTarget: JvmTarget = JvmTarget.JVM_11

    /**
     * `jvmTarget` for the plain `jvm()` target's compilation (not Android — see
     * [androidJvmTarget] for that).
     *
     * Explicit and centralized rather than left unset: without this, the compiled bytecode
     * level silently follows whatever JDK happens to run the build (the host machine locally,
     * whatever a CI workflow's `setup-java` step pins) instead of being a deliberate,
     * version-controlled decision — the exact kind of implicit, per-repo-drifting default this
     * plugin exists to eliminate. [JvmTarget.JVM_17] is a widely-compatible modern LTS baseline;
     * override per module if a specific one needs something else.
     */
    var jvmTarget: JvmTarget = JvmTarget.JVM_17

    /** Calls `explicitApi()`. */
    var explicitApi: Boolean = true

    /** Adds `-Xexpect-actual-classes` to every compilation. */
    var expectActualClasses: Boolean = false

    /** Adds `kotlin-test` to `commonTest`. */
    var kotlinTestInCommonTest: Boolean = true
}
