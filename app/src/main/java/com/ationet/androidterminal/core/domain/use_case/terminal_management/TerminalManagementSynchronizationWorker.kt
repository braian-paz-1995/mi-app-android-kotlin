package com.ationet.androidterminal.core.domain.use_case.terminal_management

import android.content.Context
import com.atio.log.Logger
import com.atio.log.util.info
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.core.domain.worker.SynchronizationWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.DurationUnit

@Singleton
class TerminalManagementSynchronizationWorker @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getConfiguration: GetConfiguration,
    private val saveLogConfigurationUseCase: SaveLogConfigurationUseCase
) {
    companion object {
        private val logger = Logger("InitializeTerminalManagementUseCase")
    }

    operator fun invoke() {
        val configuration = getConfiguration()

        if (configuration.terminalManagement.terminalManagementEnabled) {
            logger.info("Initializing terminal management")
            SynchronizationWorker.enqueue(
                context = context,
                repeatInterval = configuration.terminalManagement.pollInterval.toLong(DurationUnit.MINUTES)
            )
            with(configuration.terminalManagement) {
                saveLogConfigurationUseCase.invoke(
                    verbosity = levelReport,
                    fileSize = fileSize.toLong(),
                    fileQuantity = 4,
                    automaticLogReportEnabled = sendReportAutomatically
                )
            }
        } else {
            logger.info("Terminal management is disabled")
            SynchronizationWorker.cancel(context)
            with(configuration.terminalManagement) {
                saveLogConfigurationUseCase.invoke(
                    verbosity = levelReport,
                    fileSize = fileSize.toLong(),
                    fileQuantity = 4,
                    automaticLogReportEnabled = false
                )
            }
        }
    }
}