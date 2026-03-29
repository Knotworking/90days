package com.knotworking.schengen.feature.schengen.domain

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertNotNull

class SchengenCalculatorTest {

    private fun date(year: Int, month: Int, day: Int) = LocalDate(year, month, day)

    private fun trip(
        entry: LocalDate,
        exit: LocalDate,
        id: String = "1"
    ) = Trip(id = id, entryDate = entry, exitDate = exit)

    @Test
    fun `no trips returns zero days used and 90 remaining`() {
        val result = SchengenCalculator.calculate(emptyList(), date(2025, 6, 1))

        assertEquals(0, result.daysUsed)
        assertEquals(90, result.daysRemaining)
        assertNull(result.currentEntryDate)
        assertNull(result.latestExitDate)
    }

    @Test
    fun `single trip fully within window counts all days`() {
        // 10-day trip entirely in window
        val trip = trip(entry = date(2025, 5, 1), exit = date(2025, 5, 10))
        val result = SchengenCalculator.calculate(listOf(trip), date(2025, 6, 1))

        assertEquals(10, result.daysUsed)
        assertEquals(80, result.daysRemaining)
    }

    @Test
    fun `trip partially before 180-day window counts only overlapping days`() {
        // Reference date: 2025-06-01. Window starts 180 days back = 2024-12-04.
        // Trip: 2024-11-01 to 2024-12-10 → only Dec 4–10 (7 days) inside window
        val trip = trip(entry = date(2024, 11, 1), exit = date(2024, 12, 10))
        val result = SchengenCalculator.calculate(listOf(trip), date(2025, 6, 1))

        assertEquals(7, result.daysUsed)
    }

    @Test
    fun `trip entirely outside window is not counted`() {
        // Trip older than 180 days
        val trip = trip(entry = date(2024, 1, 1), exit = date(2024, 1, 30))
        val result = SchengenCalculator.calculate(listOf(trip), date(2025, 6, 1))

        assertEquals(0, result.daysUsed)
    }

    @Test
    fun `current trip sets currentEntryDate`() {
        val today = date(2025, 6, 10)
        val trip = trip(entry = date(2025, 6, 1), exit = date(2025, 6, 20))
        val result = SchengenCalculator.calculate(listOf(trip), today)

        assertEquals(date(2025, 6, 1), result.currentEntryDate)
        assertNotNull(result.latestExitDate)
    }

    @Test
    fun `trip in past does not set currentEntryDate`() {
        val trip = trip(entry = date(2025, 1, 1), exit = date(2025, 1, 31))
        val result = SchengenCalculator.calculate(listOf(trip), date(2025, 6, 1))

        assertNull(result.currentEntryDate)
        assertNull(result.latestExitDate)
    }

    @Test
    fun `90 days used clamps daysRemaining to zero`() {
        // 90-day trip
        val trip = trip(entry = date(2025, 3, 3), exit = date(2025, 5, 31))
        val result = SchengenCalculator.calculate(listOf(trip), date(2025, 6, 1))

        assertEquals(90, result.daysUsed)
        assertEquals(0, result.daysRemaining)
    }

    @Test
    fun `multiple trips days are summed`() {
        val trips = listOf(
            trip(entry = date(2025, 4, 1), exit = date(2025, 4, 10), id = "1"), // 10 days
            trip(entry = date(2025, 5, 1), exit = date(2025, 5, 5), id = "2")   // 5 days
        )
        val result = SchengenCalculator.calculate(trips, date(2025, 6, 1))

        assertEquals(15, result.daysUsed)
        assertEquals(75, result.daysRemaining)
    }

    @Test
    fun `latestExitDate is at least today when inside Schengen`() {
        val today = date(2025, 6, 1)
        val trip = trip(entry = today, exit = today)
        val result = SchengenCalculator.calculate(listOf(trip), today)

        assertNotNull(result.latestExitDate)
        assert(result.latestExitDate!! >= today)
    }
}
