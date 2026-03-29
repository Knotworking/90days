package com.knotworking.schengen.feature.schengen.data

import com.knotworking.schengen.feature.schengen.domain.Trip
import kotlinx.datetime.LocalDate

fun TripEntity.toTrip(): Trip = Trip(
    id = id,
    entryDate = LocalDate.fromEpochDays(entryDate.toInt()),
    exitDate = LocalDate.fromEpochDays(exitDate.toInt()),
    label = label
)

fun Trip.toTripEntity(): TripEntity = TripEntity(
    id = id,
    entryDate = entryDate.toEpochDays().toLong(),
    exitDate = exitDate.toEpochDays().toLong(),
    label = label
)
