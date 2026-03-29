package com.knotworking.schengen.feature.schengen.presentation.di

import com.knotworking.schengen.feature.schengen.presentation.calendar.CalendarViewModel
import com.knotworking.schengen.feature.schengen.presentation.home.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val schengenPresentationModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::CalendarViewModel)
}
