package com.knotworking.schengen.feature.schengen.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knotworking.schengen.feature.schengen.domain.TripRepository
import com.knotworking.schengen.feature.schengen.presentation.model.toTripUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalendarViewModel(
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CalendarState())
    val state = _state.asStateFlow()

    private val _events = Channel<CalendarEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            tripRepository.observeTrips().collect { trips ->
                _state.update { it.copy(
                    isLoading = false,
                    trips = trips.map { it.toTripUi() }
                )}
            }
        }
    }

    fun onAction(action: CalendarAction) {
        when (action) {
            is CalendarAction.OnPreviousMonthClick ->
                _state.update { it.copy(currentYearMonth = it.currentYearMonth.previous()) }
            is CalendarAction.OnNextMonthClick ->
                _state.update { it.copy(currentYearMonth = it.currentYearMonth.next()) }
            is CalendarAction.OnAddTripClick ->
                viewModelScope.launch { _events.send(CalendarEvent.OpenAddTripSheet) }
            is CalendarAction.OnDayClick -> Unit
        }
    }
}
