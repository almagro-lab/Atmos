package extensions

import org.gradle.api.GradleException
import org.gradle.api.artifacts.VersionCatalog

internal fun VersionCatalog.get(libraryAlias: String) =
    findLibrary(libraryAlias).orElseThrow {
        GradleException("Library alias '$libraryAlias' not found in version catalog 'libs'")
    }