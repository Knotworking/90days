package com.knotworking.schengen.feature.schengen.presentation.calendar

import androidx.compose.runtime.Stable
import com.knotworking.schengen.feature.schengen.presentation.model.TripUi

@Stable
data class CalendarState(
    val trips: List<TripUi> = emptyList(),
    val currentYearMonth: YearMonth = YearMonth.now(),
    val isLoading: Boolean = true
)
