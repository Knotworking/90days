package com.knotworking.schengen.feature.schengen.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.knotworking.schengen.feature.schengen.presentation.calendar.CalendarRoot
import com.knotworking.schengen.feature.schengen.presentation.home.HomeRoot

fun NavGraphBuilder.schengenNavGraph(
    onCalendarAddTrip: () -> Unit = {}
) {
    composable<HomeRoute> {
        HomeRoot()
    }
    composable<CalendarRoute> {
        CalendarRoot(onAddTripClick = onCalendarAddTrip)
    }
}
