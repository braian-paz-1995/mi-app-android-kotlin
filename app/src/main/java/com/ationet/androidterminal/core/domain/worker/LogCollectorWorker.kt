package com.ationet.androidterminal.core.domain.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.atio.log.LogManager
import com.atio.log.domain.model.LogcatConfiguration
import com.atio.log.domain.service.FileHandler
import com.atio.log.util.MByte
import com.ationet.androidterminal.core.domain.model.configuration.TerminalManagement
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

@HiltWorker
class LogCollectorWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val fileHandler: FileHandler,
) : CoroutineWorker(context, params) {
    private val logManager: LogManager = createManager()

    private fun createManager(): LogManager {
        val verbosity = getLogVerbosity(inputData)

        return LogManager(
            fileHandler = fileHandler,
            configuration = LogcatConfiguration(
                packageName = context.packageName,
                fileSize = fileSize(inputData),
                fileQuantity = fileQuantity(inputData),
                enabledDebuggable = isDebugEnabled(verbosity),
                enabledInfo = isInfoEnabled(verbosity),
                enabledWarnings = isWarningEnabled(verbosity),
                enabledErrors = !isDebugEnabled(verbosity) && isInfoEnabled(
                    verbosity
                ) && !isWarningEnabled(
                    verbosity
                ), // Error is always enabled if the other levels are disabled
            )
        )
    }

    private fun fileSize(inputData: Data) : Int {
        return inputData.getInt(FILE_SIZE, -1).let {
            if (it != 4) {
                Log.d(TAG, "Log generation: Using default of 4MB")
                4.MByte
            } else {
                it.MByte
            }
        }
    }

    private fun fileQuantity(inputData: Data): Int {
        return inputData.getInt(FILE_QUANTITY, -1).apply {
            check(this > 0) { "Invalid file quantity" }
        }
    }

    private fun getLogVerbosity(inputData: Data): TerminalManagement.LevelReport {
        val logVerbosity = inputData.getString(VERBOSITY).orEmpty()
        return if (TerminalManagement.LevelReport.entries.any { it.name == logVerbosity }) {
            TerminalManagement.LevelReport.valueOf(logVerbosity)
        } else {
            TerminalManagement.LevelReport.VERY_DETAILED
        }
    }

    private fun isDebugEnabled(verbosity: TerminalManagement.LevelReport): Boolean {
        return verbosity == TerminalManagement.LevelReport.VERY_DETAILED

    }

    private fun isInfoEnabled(verbosity: TerminalManagement.LevelReport): Boolean {
        return verbosity == TerminalManagement.LevelReport.IMPORTANT_INFORMATION_ONLY
    }

    private fun isWarningEnabled(verbosity: TerminalManagement.LevelReport): Boolean {
        return verbosity == TerminalManagement.LevelReport.WARNING_AND_ERRORS
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.Default) {
            try {
                logManager.collectLogs()
            } catch (e: Throwable) {
                coroutineContext.ensureActive()

                Log.e(TAG, "Error collecting logs", e)

                return@withContext Result.failure()
            }

            Result.success()
        }
    }

    companion object {
        private const val TAG: String = "LogcatFileManager"
        private const val WORK_NAME: String = "REVOPOS-LOG-COLLECTION-WORKER"

        private const val FILE_SIZE = "file_size"
        private const val FILE_QUANTITY = "file_quantity"
        private const val VERBOSITY = "verbosity"

        /**
         * Max default file size, in MBytes
         * */
        private const val DEFAULT_FILE_SIZE = 4
        /**
         * Max default files in archive
         * */
        private const val DEFAULT_FILE_QUANTITY = 5

        /**
         * Default verbosity level
         * */
        private val DEFAULT_VERBOSITY = TerminalManagement.LevelReport.IMPORTANT_INFORMATION_ONLY

        fun enqueue(
            context: Context,
            fileSize: Int = DEFAULT_FILE_SIZE,
            fileQuantity: Int = DEFAULT_FILE_QUANTITY,
            verbosity: TerminalManagement.LevelReport = DEFAULT_VERBOSITY
        ) {
            val data = Data.Builder()
                .putInt(FILE_SIZE, fileSize)
                .putInt(FILE_QUANTITY, fileQuantity)
                .putString(VERBOSITY, verbosity.name)

            val workRequest = OneTimeWorkRequestBuilder<LogCollectorWorker>()
            workRequest.setInputData(data.build())

            val workManager = WorkManager.getInstance(context)
            workManager.enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                workRequest.build()
            )
        }
    }
}