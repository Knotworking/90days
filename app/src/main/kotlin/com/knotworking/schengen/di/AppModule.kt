package com.knotworking.schengen.di

import androidx.room.Room
import com.knotworking.schengen.feature.schengen.data.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreDatabaseModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "schengen.db"
        ).build()
    }
    single { get<AppDatabase>().tripDao() }
}
