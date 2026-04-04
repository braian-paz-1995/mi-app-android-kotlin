package com.atio.log.domain.service

import android.util.Log
import com.atio.log.util.FileHelper
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import kotlin.coroutines.CoroutineContext

internal class LogFileHandler(
    private val coroutineContext: CoroutineContext,
    private val prefix: String,
    override val archive: File,
    override val temporaryDirectory: File,
    private val header: String?,
    private val onLogFileArchived: ((File) -> Unit)? = null,
    private val onLogFileCreated: ((File) -> Unit)? = null,
    private val onArchivedLogsRemoved: ((List<String>) -> Unit)? = null
) : FileHandler {
    private val fileMutex: Mutex = Mutex()
    private var currentLogFile: File? = null
    private var currentFileWriter: BufferedWriter? = null

    override suspend fun currentFileSize(): Int {
        return fileMutex.withLock {
            val file = getCurrentLogFile()
            val fileSize = file.length().toInt()
            return@withLock fileSize
        }
    }

    override suspend fun write(buffer: ByteArray, amount: Int) {
        if(amount <= 0) {
            throw IllegalArgumentException("Invalid write amount")
        }

        fileMutex.withLock {
            val file = getWriter()

            withContext(coroutineContext) {
                file.append(buffer.decodeToString(endIndex = amount))
                file.flush()
            }
        }
    }

    override suspend fun archiveCurrentFile(maxFileQuantity: Int) {
        fileMutex.withLock {
            ensureLogOpen()

            val logFile = getCurrentLogFile()
            if(logFile.length() == 0L) {
                return
            }

            closeCurrentFile()

            deleteOldFiles(maxFileQuantity)

            ensureLogOpen()
        }
    }

    override fun getArchiveLogs(): List<File> {
        val archivedFiles = FileHelper.filterFiles(archive, ".log", prefix)
        return archivedFiles
    }

    override fun removeArchiveLogsByName(logs: List<String>) {
        if(!archive.exists()) {
            archive.mkdirs()
            return
        }

        for (logFileName in logs) {
            val logFile = File(archive, logFileName)
            if(logFile.isFile) {
                logFile.delete()
            }
        }
    }

    private suspend fun closeCurrentFile() {
        currentFileWriter?.let {
            withContext(coroutineContext) {
                it.flush()
                it.close()
            }
        }

        currentLogFile?.let {
            val archiveFileName = File(archive, it.name)
            it.renameTo(archiveFileName)
            onLogFileArchived?.invoke(archiveFileName)
        }

        currentFileWriter = null
        currentLogFile = null
    }

    private fun deleteOldFiles(quantity: Int) {
        if(quantity <= 0) {
            throw IllegalArgumentException("Invalid files to delete quantity")
        }

        val files = FileHelper.filterFiles(archive, LOG_EXTENSION, prefix)
        if(files.isEmpty()) {
            return
        }

        if(files.count() <= quantity) {
            return
        }

        val filesToDelete = files.sortedByDescending { it.lastModified() }.drop(quantity)
        for (file in filesToDelete) {
            file.delete()
        }
        onArchivedLogsRemoved?.invoke(filesToDelete.map { it.name })
    }

    private suspend fun getCurrentLogFile(): File {
        ensureLogOpen()
        return currentLogFile!!
    }


    private suspend fun createWriter(
        logFile: File
    ): BufferedWriter {
        return withContext(coroutineContext) {
            BufferedWriter(FileWriter(logFile, true))
        }
    }

    private suspend fun getWriter(): BufferedWriter {
        ensureLogOpen()
        return currentFileWriter!!
    }

    private suspend fun ensureLogOpen() {
        val logFile = currentLogFile.let {
            if(it == null) {
                val nextFile = getNextFile()
                currentLogFile = nextFile
                currentFileWriter = createWriter(nextFile)

                return
            }

            it
        }

        if(currentFileWriter == null) {
            currentFileWriter = createWriter(logFile)
        }
    }

    private suspend fun getNextFile(): File {
        try {
            if(!temporaryDirectory.exists()) {
                /* Create path if not exists */
                temporaryDirectory.mkdirs()
            }
        } catch (e: Throwable) {
            Log.e("LogcatService", "Error creating temporary directory", e)
            throw e
        }

        val files = FileHelper.filterFiles(temporaryDirectory, LOG_EXTENSION, prefixName = prefix)
        val lastModifiedFile = files.maxByOrNull { it.lastModified() }
        return lastModifiedFile ?: createNextFile()
    }

    private suspend fun createNextFile(): File {
        if(!temporaryDirectory.exists()) {
            /* Create path if not exists */
            temporaryDirectory.mkdirs()
        }

        val fileNamePrefix = prefix.uppercase()

        val date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val formattedDate = LogNameDateFormatter.format(date)

        val fileName = "$fileNamePrefix-$formattedDate$LOG_EXTENSION"

        return File(temporaryDirectory, fileName).also { file ->
            val fileCreated = withContext(coroutineContext) {
                file.createNewFile()
            }

            if (fileCreated) {
                // Append header
                if(!header.isNullOrBlank()) {
                    file.writeText(header)
                }
            }
        }.apply {
            onLogFileCreated?.invoke(this)
        }
    }

    companion object {
        private const val LOG_EXTENSION = ".log"

        private val LogNameDateFormatter: DateTimeFormat<LocalDateTime> = LocalDateTime.Format {
            year()
            monthNumber()
            dayOfMonth()
            char('_')
            hour()
            minute()
            second()
            secondFraction()
        }
    }
}