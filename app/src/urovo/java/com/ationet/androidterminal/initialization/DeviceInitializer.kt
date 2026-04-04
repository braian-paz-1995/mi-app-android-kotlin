package com.ationet.androidterminal.initialization

import android.content.Context
import android.device.DeviceManager
import android.util.Log
import androidx.startup.Initializer
import com.ationet.androidterminal.BuildConfig

class DeviceInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        val deviceManager = DeviceManager()

        if (BuildConfig.DEBUG) {
            // Enable recent apps
            deviceManager.leftKeyEnabled = true

            // Enable home
            deviceManager.enableHomeKey(true)
        } else {
            // Disable recent apps
            deviceManager.leftKeyEnabled = false

            // Disable home
            deviceManager.enableHomeKey(false)
        }

        Log.i("UrovoDevice", "Initialized Urovo terminal. DEBUG=${BuildConfig.DEBUG}")

        // Enable back
        deviceManager.rightKeyEnabled = true
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}