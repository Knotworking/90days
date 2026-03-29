package com.knotworking.schengen.feature.schengen.domain

import kotlinx.datetime.LocalDate

data class SchengenStatus(
    val daysUsed: Int,
    val daysRemaining: Int,        // 90 - daysUsed, clamped to [0, 90]
    val currentEntryDate: LocalDate?,  // non-null when inside Schengen today
    val latestExitDate: LocalDate?     // null when not inside; day-by-day simulation
)
