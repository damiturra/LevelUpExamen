package com.example.levelupgamer

import android.app.Application
import org.osmdroid.config.Configuration

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // ⚠️ Requerido por osmdroid: define un user agent válido (usa el packageName)
        Configuration.getInstance().userAgentValue = packageName

        instance = this
    }

    companion object {
        lateinit var instance: App
            private set
    }
}
