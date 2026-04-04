package com.ationet.androidterminal.core.domain.initializer.dependency_graph

import android.content.Context
import com.ationet.androidterminal.core.domain.initializer.LogGenerationInitializer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DependencyGraphEntryPoint {

    fun initialize(logGenerationInitializer: LogGenerationInitializer)

    companion object {
        fun resolve(context: Context): DependencyGraphEntryPoint {
            val applicationContext = context.applicationContext!!

            return EntryPointAccessors.fromApplication(
                applicationContext,
                DependencyGraphEntryPoint::class.java
            )
        }
    }
}