package com.knotworking.schengen.feature.schengen.domain

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlinx.datetime.minus

object SchengenCalculator {

    fun calculate(trips: List<Trip>, referenceDate: LocalDate): SchengenStatus {
        val windowStart = referenceDate - DatePeriod(days = 179)
        val daysUsed = trips.sumOf { daysInWindow(it, windowStart, referenceDate) }
        val daysRemaining = (90 - daysUsed).coerceIn(0, 90)

        val currentTrip = trips.firstOrNull {
            it.entryDate <= referenceDate && it.exitDate >= referenceDate
        }
        val latestExitDate = currentTrip?.let { computeLatestExitDate(trips, referenceDate, it) }

        return SchengenStatus(
            daysUsed = daysUsed,
            daysRemaining = daysRemaining,
            currentEntryDate = currentTrip?.entryDate,
            latestExitDate = latestExitDate
        )
    }

    // Returns the number of days a trip overlaps with the window [windowStart, windowEnd], inclusive.
    private fun daysInWindow(trip: Trip, windowStart: LocalDate, windowEnd: LocalDate): Int {
        val clampedStart = maxOf(trip.entryDate, windowStart)
        val clampedEnd = minOf(trip.exitDate, windowEnd)
        if (clampedStart > clampedEnd) return 0
        return clampedStart.daysUntil(clampedEnd) + 1
    }

    // Simulates extending currentTrip's exit date forward day-by-day until the rolling
    // 180-day count would exceed 90. Returns the last valid day.
    private fun computeLatestExitDate(
        trips: List<Trip>,
        referenceDate: LocalDate,
        currentTrip: Trip
    ): LocalDate {
        val otherTrips = trips.filter { it.id != currentTrip.id }
        var candidate = referenceDate

        while (true) {
            val nextDay = candidate + DatePeriod(days = 1)
            val nextWindowStart = nextDay - DatePeriod(days = 179)
            val virtualTrip = currentTrip.copy(exitDate = nextDay)
            val daysUsed = (otherTrips + virtualTrip).sumOf {
                daysInWindow(it, nextWindowStart, nextDay)
            }
            if (daysUsed > 90) break
            candidate = nextDay
        }

        return candidate
    }
}
