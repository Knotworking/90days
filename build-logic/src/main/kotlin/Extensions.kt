import com.android.build.api.dsl.CommonExtension

// AGP 9.x: CommonExtension has no type parameters
internal fun CommonExtension.configureAndroidCommon() {
    compileSdk = 35
}
