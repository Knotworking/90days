package com.knotworking.schengen.feature.schengen.presentation.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knotworking.schengen.core.domain.Result
import com.knotworking.schengen.core.domain.UiText
import com.knotworking.schengen.feature.schengen.domain.SchengenCalculator
import com.knotworking.schengen.feature.schengen.domain.Trip
import com.knotworking.schengen.feature.schengen.domain.TripRepository
import com.knotworking.schengen.feature.schengen.presentation.model.toTripUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID

class HomeViewModel(
    private val tripRepository: TripRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(
        HomeState(
            showBottomSheet = savedStateHandle["showBottomSheet"] ?: false,
            editingTripId = savedStateHandle["editingTripId"],
            sheetEntryDate = savedStateHandle.get<Long>("sheetEntryEpochDays")
                ?.let { LocalDate.fromEpochDays(it.toInt()) },
            sheetExitDate = savedStateHandle.get<Long>("sheetExitEpochDays")
                ?.let { LocalDate.fromEpochDays(it.toInt()) },
            sheetLabel = savedStateHandle["sheetLabel"] ?: ""
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<HomeEvent>()
    val events = _events.receiveAsFlow()

    private var currentTrips: List<Trip> = emptyList()

    init {
        viewModelScope.launch {
            tripRepository.observeTrips().collect { trips ->
                currentTrips = trips
                val today = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
                val status = SchengenCalculator.calculate(trips, today)
                _state.update { it.copy(
                    isLoading = false,
                    status = status,
                    trips = trips.map { it.toTripUi() }
                )}
            }
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnAddTripClick -> openSheet(editingTripId = null)
            is HomeAction.OnEditTripClick -> {
                val trip = currentTrips.find { it.id == action.id } ?: return
                openSheet(
                    editingTripId = trip.id,
                    entryDate = trip.entryDate,
                    exitDate = trip.exitDate,
                    label = trip.label
                )
            }
            is HomeAction.OnDeleteTripClick -> deleteTrip(action.id)
            is HomeAction.OnDismissBottomSheet -> closeSheet()
            is HomeAction.OnEntryDateSelected -> {
                val date = action.epochMillis.toLocalDate()
                savedStateHandle["sheetEntryEpochDays"] = date.toEpochDays().toLong()
                _state.update { it.copy(sheetEntryDate = date) }
            }
            is HomeAction.OnExitDateSelected -> {
                val date = action.epochMillis.toLocalDate()
                savedStateHandle["sheetExitEpochDays"] = date.toEpochDays().toLong()
                _state.update { it.copy(sheetExitDate = date) }
            }
            is HomeAction.OnLabelChanged -> {
                savedStateHandle["sheetLabel"] = action.label
                _state.update { it.copy(sheetLabel = action.label) }
            }
            is HomeAction.OnSaveTripClick -> saveTrip()
            is HomeAction.OnDismissError -> _state.update { it.copy(error = null) }
        }
    }

    private fun openSheet(
        editingTripId: String?,
        entryDate: LocalDate? = null,
        exitDate: LocalDate? = null,
        label: String = ""
    ) {
        savedStateHandle["showBottomSheet"] = true
        savedStateHandle["editingTripId"] = editingTripId
        savedStateHandle["sheetEntryEpochDays"] = entryDate?.toEpochDays()?.toLong()
        savedStateHandle["sheetExitEpochDays"] = exitDate?.toEpochDays()?.toLong()
        savedStateHandle["sheetLabel"] = label
        _state.update { it.copy(
            showBottomSheet = true,
            editingTripId = editingTripId,
            sheetEntryDate = entryDate,
            sheetExitDate = exitDate,
            sheetLabel = label
        )}
    }

    private fun closeSheet() {
        savedStateHandle["showBottomSheet"] = false
        savedStateHandle["editingTripId"] = null
        savedStateHandle["sheetEntryEpochDays"] = null
        savedStateHandle["sheetExitEpochDays"] = null
        savedStateHandle["sheetLabel"] = ""
        _state.update { it.copy(
            showBottomSheet = false,
            editingTripId = null,
            sheetEntryDate = null,
            sheetExitDate = null,
            sheetLabel = ""
        )}
    }

    private fun saveTrip() {
        val entryDate = _state.value.sheetEntryDate
        val exitDate = _state.value.sheetExitDate

        if (entryDate == null || exitDate == null) {
            viewModelScope.launch {
                _events.send(HomeEvent.ShowError(UiText.StringResource("error_invalid_date_range")))
            }
            return
        }
        if (exitDate < entryDate) {
            viewModelScope.launch {
                _events.send(HomeEvent.ShowError(UiText.StringResource("error_invalid_date_range")))
            }
            return
        }

        val trip = Trip(
            id = _state.value.editingTripId ?: UUID.randomUUID().toString(),
            entryDate = entryDate,
            exitDate = exitDate,
            label = _state.value.sheetLabel.trim()
        )

        viewModelScope.launch {
            when (tripRepository.upsertTrip(trip)) {
                is Result.Success -> {
                    closeSheet()
                    _events.send(HomeEvent.TripSaved)
                }
                is Result.Failure -> {
                    _events.send(HomeEvent.ShowError(UiText.StringResource("error_unknown")))
                }
            }
        }
    }

    private fun deleteTrip(id: String) {
        viewModelScope.launch {
            when (tripRepository.deleteTrip(id)) {
                is Result.Success -> _events.send(HomeEvent.TripDeleted)
                is Result.Failure -> _events.send(HomeEvent.ShowError(UiText.StringResource("error_unknown")))
            }
        }
    }
}

private fun Long.toLocalDate(): LocalDate =
    Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.UTC)
        .date
