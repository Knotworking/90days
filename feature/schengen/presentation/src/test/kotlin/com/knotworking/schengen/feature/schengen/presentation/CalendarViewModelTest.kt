package com.knotworking.schengen.feature.schengen.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.knotworking.schengen.feature.schengen.domain.Trip
import com.knotworking.schengen.feature.schengen.presentation.calendar.CalendarAction
import com.knotworking.schengen.feature.schengen.presentation.calendar.CalendarEvent
import com.knotworking.schengen.feature.schengen.presentation.calendar.CalendarViewModel
import com.knotworking.schengen.feature.schengen.presentation.calendar.YearMonth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeTripRepository
    private lateinit var viewModel: CalendarViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTripRepository()
        viewModel = CalendarViewModel(repository)
    }

    @AfterEach
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `trips from repository appear in state`() = runTest {
        val trips = listOf(
            Trip("1", LocalDate(2025, 3, 1), LocalDate(2025, 3, 10))
        )
        // Emit before subscribing so turbine sees the already-updated state
        repository.emit(trips)

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.trips.size).isEqualTo(1)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnPreviousMonthClick decrements month`() = runTest {
        viewModel.state.test {
            val initial = awaitItem()
            viewModel.onAction(CalendarAction.OnPreviousMonthClick)
            val state = awaitItem()
            assertThat(state.currentYearMonth).isEqualTo(initial.currentYearMonth.previous())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnNextMonthClick increments month`() = runTest {
        viewModel.state.test {
            val initial = awaitItem()
            viewModel.onAction(CalendarAction.OnNextMonthClick)
            val state = awaitItem()
            assertThat(state.currentYearMonth).isEqualTo(initial.currentYearMonth.next())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `YearMonth wraps from January to December on previous`() {
        val january = YearMonth(2025, Month.JANUARY)
        val previous = january.previous()
        assertThat(previous.month).isEqualTo(Month.DECEMBER)
        assertThat(previous.year).isEqualTo(2024)
    }

    @Test
    fun `YearMonth wraps from December to January on next`() {
        val december = YearMonth(2025, Month.DECEMBER)
        val next = december.next()
        assertThat(next.month).isEqualTo(Month.JANUARY)
        assertThat(next.year).isEqualTo(2026)
    }

    @Test
    fun `OnAddTripClick emits OpenAddTripSheet event`() = runTest {
        viewModel.events.test {
            viewModel.onAction(CalendarAction.OnAddTripClick)
            assertThat(awaitItem()).isInstanceOf(CalendarEvent.OpenAddTripSheet::class)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
