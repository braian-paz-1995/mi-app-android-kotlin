package com.ationet.androidterminal.core.domain.use_case.terminal_management

import android.content.Context
import android.util.Log
import com.ationet.androidterminal.core.domain.model.configuration.TerminalManagement
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import com.ationet.androidterminal.core.domain.worker.LogCollectorWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveLogConfigurationUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val configurationUseCase: ConfigurationUseCase,
) {
    operator fun invoke(
        verbosity: TerminalManagement.LevelReport,
        fileSize: Long,
        fileQuantity: Int,
        automaticLogReportEnabled: Boolean,
    ) {
//        if (!LogConfigurationValidation.isValidFileSize(fileSize)) {
//            throw IllegalArgumentException("Invalid file size")
//        }
//
//        if (!LogConfigurationValidation.isValidFileQuantity(fileQuantity)) {
//            throw IllegalArgumentException("Invalid file quantity")
//        }

        configurationUseCase.updateConfiguration {
            it.copy(
                terminalManagement = it.terminalManagement.copy(
                    levelReport = verbosity,
//                    fileSize = fileSize.toInt(),
                    sendReportAutomatically = automaticLogReportEnabled
                )
            )
        }

        LogCollectorWorker.enqueue(
            context = context,
            fileSize = fileSize.toInt(),
            fileQuantity = 5,
            verbosity = verbosity,
        )

        Log.i(TAG, "Log manager: log generation relaunched")
    }

    private companion object {
        private const val TAG: String = "LogManager"
    }
}

