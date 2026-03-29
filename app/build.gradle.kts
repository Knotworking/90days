plugins {
    id("schengen.android.application")
    id("schengen.compose")
    id("schengen.koin")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.knotworking.schengen"
    defaultConfig {
        applicationId = "com.knotworking.schengen"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.presentation)
    implementation(projects.core.designSystem)
    implementation(projects.feature.schengen.domain)
    implementation(projects.feature.schengen.data)
    implementation(projects.feature.schengen.presentation)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.navigation)
    implementation(libs.room.runtime)
    implementation(libs.compose.material.icons.extended)
}
