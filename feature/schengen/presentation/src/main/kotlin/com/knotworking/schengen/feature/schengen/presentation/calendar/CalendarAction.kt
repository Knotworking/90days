package com.knotworking.schengen.feature.schengen.presentation.calendar

import kotlinx.datetime.LocalDate

sealed interface CalendarAction {
    data object OnPreviousMonthClick : CalendarAction
    data object OnNextMonthClick : CalendarAction
    data object OnAddTripClick : CalendarAction
    data class OnDayClick(val date: LocalDate) : CalendarAction
}
