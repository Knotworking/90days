package com.knotworking.schengen.feature.schengen.domain

import com.knotworking.schengen.core.domain.DataError
import com.knotworking.schengen.core.domain.EmptyResult
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun observeTrips(): Flow<List<Trip>>
    suspend fun upsertTrip(trip: Trip): EmptyResult<DataError.Local>
    suspend fun deleteTrip(id: String): EmptyResult<DataError.Local>
}
