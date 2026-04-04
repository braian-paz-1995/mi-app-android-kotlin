package com.ationet.androidterminal.core.domain.use_case.terminal_management

import androidx.annotation.StringRes
import com.atio.log.Logger
import com.atio.log.domain.service.FileHandler
import com.atio.log.util.d
import com.atio.log.util.e
import com.atio.log.util.info
import com.atio.log.util.w
import com.atio.log.util.warn
import com.atio.terminal_management.TerminalManagement
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

sealed interface PushLogResult{
    data object Ok: PushLogResult
    data class ValidationError(@StringRes val message: Int): PushLogResult
    data object Failed: PushLogResult
}

@Singleton
class PushLogUseCase @Inject constructor(
    private val terminalManagementModule: TerminalManagement,
    private val deviceInfo: HALDeviceInfo,
    private val fileHandler: FileHandler,
) {
    suspend operator fun invoke(
        host: String,
        terminalId: String?,
        archiveCurrent: Boolean
    ): PushLogResult {
        if (host.isBlank()) {
            Logger.warn(TAG, "Host missing")
            return PushLogResult.ValidationError(message = R.string.url_is_empty)
        }

        if (terminalId.isNullOrBlank()) {
            Logger.warn(TAG, "Terminal ID missing")
            return PushLogResult.ValidationError(message = R.string.terminal_id_id_empty)
        }

        val logs = getLogFiles(archiveCurrent = archiveCurrent)
        if(logs.isEmpty()) {
            Logger.d(TAG, "Nothing to push")
            return PushLogResult.Ok
        }

        Logger.info(
            TAG,
            "Terminal id '$terminalId' pushing ${logs.count()} log(s)"
        )
        val synchronizedLogs = mutableListOf<String>()
        val pushResults = supervisorScope {
            val synchronizationJobs = buildList {
                for (log in logs) {
                    if(log.length() == 0L) {
                        Logger.w(TAG, "Log file ${log.name} is empty. Ignoring...")
                        continue
                    }

                    val job = async {
                        try {
                            Logger.d(TAG, "Pushing log file '${log.name}'")

                            val result = terminalManagementModule.executePushLog(
                                host = host,
                                terminalId = terminalId,
                                agentSerialNumber = deviceInfo.serialNumber,
                                log = log.inputStream()
                            )

                            if (result != null && result.pushedLogs.isNotEmpty()) {
                                result.pushedLogs.forEach {
                                    Logger.d(TAG, "Log file '${log.name}' pushed. Result: $it")
                                }

                                synchronizedLogs += log.name
                            } else {
                                Logger.w(TAG, "Failed to push log file '${log.name}'")
                            }

                            result
                        } catch (e: Throwable) {
                            currentCoroutineContext().ensureActive()
                            Logger.e(TAG, "Error pushing log file '${log.name}'", e)
                            null
                        }
                    }

                    add(job)
                }
            }

            synchronizationJobs.awaitAll()
        }

        if (synchronizedLogs.isNotEmpty()) {
            Logger.d(TAG, "Removing ${synchronizedLogs.count()} pushed logs")
            withContext(Dispatchers.IO) {
                fileHandler.removeArchiveLogsByName(synchronizedLogs)
            }
        }

        if (pushResults.any { it == null }) {
            return PushLogResult.Failed
        }

        return PushLogResult.Ok
    }

    private suspend fun getLogFiles(
        archiveCurrent: Boolean
    ): List<File> {
        return withContext(Dispatchers.IO) {
            if(archiveCurrent) {
                fileHandler.archiveCurrentFile(5)
            }

            fileHandler.getArchiveLogs()
        }
    }

    companion object {
        private const val TAG: String = "PushLogs"
    }
}