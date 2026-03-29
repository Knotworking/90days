package com.knotworking.schengen.feature.schengen.domain

import kotlinx.datetime.LocalDate

data class Trip(
    val id: String,
    val entryDate: LocalDate,
    val exitDate: LocalDate,
    val label: String = ""
)
