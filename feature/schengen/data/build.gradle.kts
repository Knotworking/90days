plugins {
    id("schengen.kmp.library")
    id("schengen.room")
    id("schengen.koin")
}

android {
    namespace = "com.knotworking.schengen.feature.schengen.data"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.feature.schengen.domain)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
