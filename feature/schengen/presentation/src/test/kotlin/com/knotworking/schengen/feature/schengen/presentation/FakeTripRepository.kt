package com.knotworking.schengen.feature.schengen.presentation

import com.knotworking.schengen.core.domain.DataError
import com.knotworking.schengen.core.domain.EmptyResult
import com.knotworking.schengen.core.domain.Result
import com.knotworking.schengen.feature.schengen.domain.Trip
import com.knotworking.schengen.feature.schengen.domain.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeTripRepository : TripRepository {

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())

    var upsertResult: EmptyResult<DataError.Local> = Result.Success(Unit)
    var deleteResult: EmptyResult<DataError.Local> = Result.Success(Unit)

    val upsertedTrips = mutableListOf<Trip>()
    val deletedIds = mutableListOf<String>()

    fun emit(trips: List<Trip>) {
        _trips.value = trips
    }

    override fun observeTrips(): Flow<List<Trip>> = _trips

    override suspend fun upsertTrip(trip: Trip): EmptyResult<DataError.Local> {
        upsertedTrips += trip
        return upsertResult
    }

    override suspend fun deleteTrip(id: String): EmptyResult<DataError.Local> {
        deletedIds += id
        return deleteResult
    }
}
