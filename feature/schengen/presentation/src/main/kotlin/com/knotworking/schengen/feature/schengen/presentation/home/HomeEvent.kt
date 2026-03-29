package com.knotworking.schengen.feature.schengen.presentation.home

import com.knotworking.schengen.core.domain.UiText

sealed interface HomeEvent {
    data class ShowError(val message: UiText) : HomeEvent
    data object TripSaved : HomeEvent
    data object TripDeleted : HomeEvent
}
