import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

var kmpConfigured = false

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    kmpConfigured = true
    extensions.configure<KotlinMultiplatformExtension> {
        sourceSets.commonMain.dependencies {
            implementation(libs.koin.core)
        }
        sourceSets.androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
    }
}

pluginManager.withPlugin("com.android.application") {
    if (!kmpConfigured) {
        dependencies {
            add("implementation", libs.koin.android)
            add("implementation", libs.koin.compose)
            add("implementation", libs.koin.compose.viewmodel)
        }
    }
}

pluginManager.withPlugin("com.android.library") {
    if (!kmpConfigured) {
        dependencies {
            add("implementation", libs.koin.android)
            add("implementation", libs.koin.compose)
            add("implementation", libs.koin.compose.viewmodel)
        }
    }
}
