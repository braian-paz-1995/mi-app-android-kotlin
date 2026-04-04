package com.ationet.androidterminal.core.domain.initializer.dependency_graph

import android.content.Context
import androidx.startup.Initializer

class DependencyGraphInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        DependencyGraphEntryPoint.resolve(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> =
        mutableListOf()
}