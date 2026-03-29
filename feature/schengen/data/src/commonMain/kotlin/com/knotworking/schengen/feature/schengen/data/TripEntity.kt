package com.knotworking.schengen.feature.schengen.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val entryDate: Long,
    val exitDate: Long,
    val label: String
)
