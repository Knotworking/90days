package com.knotworking.schengen.core.presentation

import android.content.Context
import com.knotworking.schengen.core.domain.UiText

private val stringResources = mapOf(
    "error_unknown" to R.string.error_unknown,
    "error_invalid_date_range" to R.string.error_invalid_date_range,
    "error_disk_full" to R.string.error_disk_full
)

fun UiText.asString(context: Context): String = when (this) {
    is UiText.DynamicString -> value
    is UiText.StringResource -> context.getString(
        stringResources[key] ?: R.string.error_unknown
    )
}
