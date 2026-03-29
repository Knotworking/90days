plugins {
    id("org.jetbrains.kotlin.plugin.compose")
}

dependencies {
    val bom = platform(libs.compose.bom)
    add("implementation", bom)
    add("debugImplementation", bom)
    add("implementation", libs.compose.ui)
    add("implementation", libs.compose.ui.tooling.preview)
    add("implementation", libs.compose.material3)
    add("debugImplementation", libs.compose.ui.tooling)
}
