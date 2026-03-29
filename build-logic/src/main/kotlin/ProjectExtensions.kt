import org.gradle.api.Project
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.getByType

// Expose the version catalog to precompiled script plugins.
// The LibrariesForLibs class is on the classpath via
// implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
// in build-logic/build.gradle.kts.
internal val Project.libs: LibrariesForLibs
    get() = extensions.getByType<LibrariesForLibs>()
