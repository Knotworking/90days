package com.knotworking.schengen.feature.schengen.data

import com.knotworking.schengen.core.domain.DataError
import com.knotworking.schengen.core.domain.EmptyResult
import com.knotworking.schengen.core.domain.Result
import com.knotworking.schengen.feature.schengen.domain.Trip
import com.knotworking.schengen.feature.schengen.domain.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomTripRepository(private val dao: TripDao) : TripRepository {

    override fun observeTrips(): Flow<List<Trip>> =
        dao.observeAllTrips().map { entities -> entities.map { it.toTrip() } }

    override suspend fun upsertTrip(trip: Trip): EmptyResult<DataError.Local> {
        return try {
            dao.upsertTrip(trip.toTripEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun deleteTrip(id: String): EmptyResult<DataError.Local> {
        return try {
            dao.deleteTrip(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(DataError.Local.UNKNOWN)
        }
    }
}
