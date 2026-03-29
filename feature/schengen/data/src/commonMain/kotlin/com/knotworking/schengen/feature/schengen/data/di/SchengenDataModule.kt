package com.knotworking.schengen.feature.schengen.data.di

import com.knotworking.schengen.feature.schengen.data.RoomTripRepository
import com.knotworking.schengen.feature.schengen.domain.TripRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val schengenDataModule = module {
    singleOf(::RoomTripRepository) bind TripRepository::class
}
