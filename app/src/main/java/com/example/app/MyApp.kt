package com.example.app

import android.app.Application
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent

@HiltAndroidApp
class MyApp : Application(), CameraXConfig.Provider, Configuration.Provider {
    override fun getCameraXConfig(): CameraXConfig {
        return CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig()).build()
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AATEntryPoint {
        fun workerFactory(): HiltWorkerFactory
    }

    override fun getWorkManagerConfiguration(): Configuration = Configuration.Builder()
        .setWorkerFactory(
            EntryPointAccessors.fromApplication(
                applicationContext,
                AATEntryPoint::class.java
            ).workerFactory()
        )
        .build()
}