package com.knotworking.schengen.feature.schengen.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY entryDate DESC")
    fun observeAllTrips(): Flow<List<TripEntity>>

    @Upsert
    suspend fun upsertTrip(trip: TripEntity)

    @Query("DELETE FROM trips WHERE id = :id")
    suspend fun deleteTrip(id: String)
}
