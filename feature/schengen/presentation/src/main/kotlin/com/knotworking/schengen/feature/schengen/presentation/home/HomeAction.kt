package com.knotworking.schengen.feature.schengen.presentation.home

sealed interface HomeAction {
    data object OnAddTripClick : HomeAction
    data class OnEditTripClick(val id: String) : HomeAction
    data class OnDeleteTripClick(val id: String) : HomeAction
    data object OnDismissBottomSheet : HomeAction
    data class OnEntryDateSelected(val epochMillis: Long) : HomeAction
    data class OnExitDateSelected(val epochMillis: Long) : HomeAction
    data class OnLabelChanged(val label: String) : HomeAction
    data object OnSaveTripClick : HomeAction
    data object OnDismissError : HomeAction
}
