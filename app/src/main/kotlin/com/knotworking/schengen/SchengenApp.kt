package com.knotworking.schengen

import android.app.Application
import com.knotworking.schengen.di.coreDatabaseModule
import com.knotworking.schengen.feature.schengen.data.di.schengenDataModule
import com.knotworking.schengen.feature.schengen.presentation.di.schengenPresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SchengenApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SchengenApp)
            modules(
                coreDatabaseModule,
                schengenDataModule,
                schengenPresentationModule
            )
        }
    }
}
