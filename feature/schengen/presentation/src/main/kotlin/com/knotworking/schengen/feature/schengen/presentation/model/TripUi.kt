package com.knotworking.schengen.feature.schengen.presentation.model

import com.knotworking.schengen.feature.schengen.domain.Trip
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

data class TripUi(
    val id: String,
    val entryDate: String,
    val exitDate: String,
    val entryLocalDate: LocalDate,
    val exitLocalDate: LocalDate,
    val label: String,
    val durationDays: Int
)

fun Trip.toTripUi(): TripUi = TripUi(
    id = id,
    entryDate = entryDate.formatDisplay(),
    exitDate = exitDate.formatDisplay(),
    entryLocalDate = entryDate,
    exitLocalDate = exitDate,
    label = label,
    durationDays = entryDate.daysUntil(exitDate) + 1
)

private fun LocalDate.formatDisplay(): String {
    val monthName = month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    return "$dayOfMonth $monthName $year"
}
