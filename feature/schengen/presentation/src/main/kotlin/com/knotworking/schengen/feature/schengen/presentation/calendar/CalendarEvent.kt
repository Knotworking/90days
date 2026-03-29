package com.knotworking.schengen.feature.schengen.presentation.calendar

sealed interface CalendarEvent {
    data object OpenAddTripSheet : CalendarEvent
}
