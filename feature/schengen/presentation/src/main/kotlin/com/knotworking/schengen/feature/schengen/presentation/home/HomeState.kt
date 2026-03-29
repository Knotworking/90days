package com.knotworking.schengen.feature.schengen.presentation.home

import androidx.compose.runtime.Stable
import com.knotworking.schengen.core.domain.UiText
import com.knotworking.schengen.feature.schengen.domain.SchengenStatus
import com.knotworking.schengen.feature.schengen.presentation.model.TripUi
import kotlinx.datetime.LocalDate

@Stable
data class HomeState(
    val isLoading: Boolean = true,
    val status: SchengenStatus? = null,
    val trips: List<TripUi> = emptyList(),
    val error: UiText? = null,
    val showBottomSheet: Boolean = false,
    val editingTripId: String? = null,
    val sheetEntryDate: LocalDate? = null,
    val sheetExitDate: LocalDate? = null,
    val sheetLabel: String = ""
)
