package com.ationet.androidterminal.core.domain.use_case.terminal_management

import android.content.Context
import android.util.Log
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.core.domain.worker.LogCollectorWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InitializeLogManagerUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getConfiguration: GetConfiguration,
) {
    operator fun invoke() {
        val configuration: Configuration = getConfiguration.invoke()

        LogCollectorWorker.enqueue(
            context = context,
            fileSize = configuration.terminalManagement.fileSize,
            fileQuantity = 5,
            verbosity = configuration.terminalManagement.levelReport,
        )

        Log.i(TAG, "Log manager: launched log generation")
    }

    private companion object {
        private const val TAG: String = "LogManager"
    }
}