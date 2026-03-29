import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

// Apply com.android.library via apply() rather than plugins {} so that
// generatePrecompiledScriptPluginAccessors never sees the combination of
// com.android.library + kotlin-multiplatform in a synthetic project where the
// android.builtInKotlin=false flag (root gradle.properties) has no effect.
// At actual build time the root gradle.properties flags are in scope, so this works.
apply(plugin = "com.android.library")

extensions.configure<com.android.build.api.dsl.LibraryExtension> {
    configureAndroidCommon()
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(25)
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}
