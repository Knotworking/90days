plugins {
    id("schengen.android.library")
    id("schengen.compose")
}

android {
    namespace = "com.knotworking.schengen.core.presentation"
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.androidx.lifecycle.runtime.compose)
}
