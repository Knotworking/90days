package com.knotworking.schengen.core.domain

sealed interface UiText {
    data class DynamicString(val value: String) : UiText
    // key is a string (not an R.string Int) so it works on both Android and iOS.
    // Android resolves via a key→R.string map in :core:presentation.
    // iOS will resolve via NSLocalizedString(key, ...) in iosMain.
    data class StringResource(val key: String) : UiText
}
