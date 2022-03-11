package com.hescul.urgent

import android.app.Application
import timber.log.Timber

class UrgentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}