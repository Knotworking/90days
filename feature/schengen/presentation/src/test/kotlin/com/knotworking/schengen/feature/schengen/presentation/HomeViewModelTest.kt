package com.knotworking.schengen.feature.schengen.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.knotworking.schengen.core.domain.DataError
import com.knotworking.schengen.core.domain.Result
import com.knotworking.schengen.feature.schengen.domain.Trip
import com.knotworking.schengen.feature.schengen.presentation.home.HomeAction
import com.knotworking.schengen.feature.schengen.presentation.home.HomeEvent
import com.knotworking.schengen.feature.schengen.presentation.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeTripRepository
    private lateinit var viewModel: HomeViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTripRepository()
        viewModel = HomeViewModel(repository, SavedStateHandle())
    }

    @AfterEach
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `after init isLoading is false`() {
        // With UnconfinedTestDispatcher the init coroutine runs eagerly,
        // collecting the empty initial StateFlow value and clearing isLoading.
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `trips from repository appear in state`() = runTest {
        val trips = listOf(
            Trip("1", LocalDate(2025, 3, 1), LocalDate(2025, 3, 10)),
            Trip("2", LocalDate(2025, 4, 1), LocalDate(2025, 4, 5))
        )
        // Emit before subscribing so turbine sees the already-updated state
        repository.emit(trips)

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.trips.size).isEqualTo(2)
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnAddTripClick shows bottom sheet`() = runTest {
        viewModel.state.test {
            awaitItem() // initial state
            viewModel.onAction(HomeAction.OnAddTripClick)
            val state = awaitItem()
            assertThat(state.showBottomSheet).isTrue()
            assertThat(state.editingTripId).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnDismissBottomSheet closes bottom sheet`() = runTest {
        viewModel.state.test {
            awaitItem() // initial
            viewModel.onAction(HomeAction.OnAddTripClick)
            awaitItem() // sheet opened
            viewModel.onAction(HomeAction.OnDismissBottomSheet)
            val state = awaitItem()
            assertThat(state.showBottomSheet).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnEditTripClick opens sheet with trip data`() = runTest {
        val trip = Trip("1", LocalDate(2025, 3, 1), LocalDate(2025, 3, 10), "Paris")
        repository.emit(listOf(trip))

        viewModel.state.test {
            awaitItem() // state with trip loaded
            viewModel.onAction(HomeAction.OnEditTripClick("1"))
            val state = awaitItem()
            assertThat(state.showBottomSheet).isTrue()
            assertThat(state.editingTripId).isEqualTo("1")
            assertThat(state.sheetEntryDate).isEqualTo(LocalDate(2025, 3, 1))
            assertThat(state.sheetExitDate).isEqualTo(LocalDate(2025, 3, 10))
            assertThat(state.sheetLabel).isEqualTo("Paris")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnSaveTripClick with no dates emits ShowError`() = runTest {
        viewModel.events.test {
            viewModel.onAction(HomeAction.OnSaveTripClick)
            assertThat(awaitItem()).isInstanceOf(HomeEvent.ShowError::class)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnSaveTripClick with exit before entry emits ShowError`() = runTest {
        // Epoch millis at midnight UTC for a given date via epoch days * 86400000
        viewModel.onAction(HomeAction.OnEntryDateSelected(LocalDate(2025, 3, 10).toEpochDays().toLong() * 86_400_000L))
        viewModel.onAction(HomeAction.OnExitDateSelected(LocalDate(2025, 3, 1).toEpochDays().toLong() * 86_400_000L))

        viewModel.events.test {
            viewModel.onAction(HomeAction.OnSaveTripClick)
            assertThat(awaitItem()).isInstanceOf(HomeEvent.ShowError::class)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnSaveTripClick with valid dates upserts and emits TripSaved`() = runTest {
        viewModel.onAction(HomeAction.OnEntryDateSelected(LocalDate(2025, 3, 1).toEpochDays().toLong() * 86_400_000L))
        viewModel.onAction(HomeAction.OnExitDateSelected(LocalDate(2025, 3, 10).toEpochDays().toLong() * 86_400_000L))

        viewModel.events.test {
            viewModel.onAction(HomeAction.OnSaveTripClick)
            assertThat(awaitItem()).isInstanceOf(HomeEvent.TripSaved::class)
            assertThat(repository.upsertedTrips).isNotEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnSaveTripClick when upsert fails emits ShowError`() = runTest {
        repository.upsertResult = Result.Failure(DataError.Local.UNKNOWN)
        viewModel.onAction(HomeAction.OnEntryDateSelected(LocalDate(2025, 3, 1).toEpochDays().toLong() * 86_400_000L))
        viewModel.onAction(HomeAction.OnExitDateSelected(LocalDate(2025, 3, 10).toEpochDays().toLong() * 86_400_000L))

        viewModel.events.test {
            viewModel.onAction(HomeAction.OnSaveTripClick)
            assertThat(awaitItem()).isInstanceOf(HomeEvent.ShowError::class)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnDeleteTripClick calls delete and emits TripDeleted`() = runTest {
        viewModel.events.test {
            viewModel.onAction(HomeAction.OnDeleteTripClick("trip-1"))
            assertThat(awaitItem()).isInstanceOf(HomeEvent.TripDeleted::class)
            assertThat(repository.deletedIds).isEqualTo(listOf("trip-1"))
            cancelAndIgnoreRemainingEvents()
        }
    }
}
