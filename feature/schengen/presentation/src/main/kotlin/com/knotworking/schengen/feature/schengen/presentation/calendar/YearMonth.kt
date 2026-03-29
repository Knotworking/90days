package com.knotworking.schengen.feature.schengen.presentation.calendar

import kotlinx.datetime.Clock
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class YearMonth(val year: Int, val month: Month) {

    // Month.value is 1-based (JVM: kotlinx.datetime.Month is a typealias for java.time.Month)
    fun previous(): YearMonth = if (month == Month.JANUARY) {
        YearMonth(year - 1, Month.DECEMBER)
    } else {
        YearMonth(year, Month.of(month.value - 1))
    }

    fun next(): YearMonth = if (month == Month.DECEMBER) {
        YearMonth(year + 1, Month.JANUARY)
    } else {
        YearMonth(year, Month.of(month.value + 1))
    }

    fun toJavaYearMonth(): java.time.YearMonth =
        java.time.YearMonth.of(year, month.value)

    companion object {
        fun now(): YearMonth {
            val today = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
            return YearMonth(today.year, today.month)
        }
    }
}
