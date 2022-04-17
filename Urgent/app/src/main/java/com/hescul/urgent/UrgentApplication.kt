package com.hescul.urgent

import android.app.Application
import android.content.res.Configuration
import timber.log.Timber
import java.util.*

class UrgentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        systemLocale = Locale.getDefault()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        systemLocale = newConfig.locales[0]
    }

    companion object {
        lateinit var systemLocale: Locale
            private set
    }
}