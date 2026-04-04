package com.atio.log

import com.atio.log.domain.model.LogcatConfiguration
import com.atio.log.domain.service.FileHandler
import com.atio.log.domain.service.LogFileHandler
import com.atio.log.domain.service.LogcatFileWatcherService
import com.atio.log.domain.service.LogcatProcessImpl
import java.io.File
import kotlin.coroutines.CoroutineContext

fun createFileHandler(
    coroutineContext: CoroutineContext,
    prefix: String,
    rootPath: String,
    onLogFileArchived: ((File) -> Unit)? = null,
    onLogFileCreated: ((File) -> Unit)? = null,
    onArchivedLogsRemoved: ((List<String>) -> Unit)? = null
) : FileHandler {
    return LogFileHandler(
        coroutineContext = coroutineContext,
        prefix = prefix,
        archive = File(rootPath),
        temporaryDirectory = File(rootPath, "tmp"),
        header = null,
        onLogFileArchived = onLogFileArchived,
        onLogFileCreated = onLogFileCreated,
        onArchivedLogsRemoved = onArchivedLogsRemoved
    )
}

class LogManager(
    private val configuration: LogcatConfiguration,
    private val fileHandler: FileHandler
) {
    /**
     *  Starts collecting logs to a file. This method blocks until reading is cancelled.
     *  */
    suspend fun collectLogs() {
        val currentConfiguration = configuration

        val logcatProcess = LogcatProcessImpl(
            packageName = currentConfiguration.packageName,
            debugEnabled = currentConfiguration.enabledDebuggable,
            warningEnabled = currentConfiguration.enabledWarnings,
            errorEnabled = currentConfiguration.enabledErrors,
            infoEnabled = currentConfiguration.enabledInfo
        )

        val service = LogcatFileWatcherService(
            logcatProcess = logcatProcess,
            fileHandler = fileHandler,
            fileSize = currentConfiguration.fileSize,
            fileQuantity = currentConfiguration.fileQuantity
        )

        service.start()
    }
}