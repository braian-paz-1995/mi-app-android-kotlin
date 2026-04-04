package com.ationet.androidterminal.core.domain.initializer

import android.content.Context
import androidx.startup.Initializer
import com.ationet.androidterminal.core.domain.initializer.dependency_graph.DependencyGraphEntryPoint
import com.ationet.androidterminal.core.domain.initializer.dependency_graph.DependencyGraphInitializer
import com.ationet.androidterminal.core.domain.use_case.terminal_management.InitializeLogManagerUseCase
import javax.inject.Inject

class LogGenerationInitializer : Initializer<Unit> {
    @Inject
    lateinit var initializeLogManagerUseCase: InitializeLogManagerUseCase

    override fun create(context: Context) {
        DependencyGraphEntryPoint.resolve(context).initialize(this)

        initializeLogManagerUseCase.invoke()
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf(
        DependencyGraphInitializer::class.java
    )
}