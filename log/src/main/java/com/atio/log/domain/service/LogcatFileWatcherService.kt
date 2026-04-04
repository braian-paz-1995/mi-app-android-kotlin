package com.atio.log.domain.service

import android.util.Log
import com.atio.log.util.KByte
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream

internal class LogcatFileWatcherService(
    private val logcatProcess: LogcatProcess,
    private val fileHandler: FileHandler,
    private val fileSize: Int,
    private val fileQuantity: Int,
) {
    suspend fun start() {
        val process = logcatProcess.startLogcat()

        val result = runCatching {
            // If file size exceeded, switch file
            if(fileHandler.currentFileSize() >= fileSize) {
                fileHandler.archiveCurrentFile(fileQuantity)
            }

            try {
                val inputStream = process.inputStream.buffered()
                inputStream.use {
                    readRedirectLogs(inputStream)
                }
            } catch (e: Throwable) {
                currentCoroutineContext().ensureActive()
                Log.e("LogcatService", "Error reading logs", e)
            } finally {
                Log.i("LogcatService", "Closing process")
                fileHandler.archiveCurrentFile(fileQuantity)
            }

            withContext(Dispatchers.IO) {
                process.waitFor()
            }
        }

        Log.i("LogcatService", "Process exited with code: $result")

        if(result.isSuccess && result.getOrThrow() != 0) {
            val error = process.errorStream.bufferedReader().use {
                it.readText()
            }
            Log.w("LogcatService", "Process exited with code: $result ($error)")
        }
    }

    private suspend fun readRedirectLogs(
        inputStream: BufferedInputStream,
    ) {
        val buffer = ByteArray(size = 8.KByte)

        var availableToRead = getFileAvailableSpaceToRead()
        availableToRead = minOf(buffer.size, availableToRead)

        var readLength = readStream(inputStream, buffer, availableToRead)
        while (readLength != -1) {
            currentCoroutineContext().ensureActive()

            if (readLength > 0) {
                availableToRead -= readLength
                if(availableToRead <= 0) {
                    availableToRead = minOf(buffer.size, getFileAvailableSpaceToRead())
                }

                fileHandler.write(buffer, readLength)
            }

            // If file size exceeded, switch file
            val currentFileSize = fileHandler.currentFileSize()
            if(currentFileSize >= fileSize) {
                fileHandler.archiveCurrentFile(fileQuantity)

                availableToRead = getFileAvailableSpaceToRead()
                availableToRead = minOf(buffer.size, availableToRead)
            }

            readLength = readStream(inputStream, buffer, availableToRead)
        }
    }

    private suspend fun readStream(
        inputStream: BufferedInputStream,
        buffer: ByteArray,
        length: Int
    ): Int {
        return withContext(Dispatchers.IO) {
            inputStream.read(buffer, 0, length)
        }
    }

    private suspend fun getFileAvailableSpaceToRead(): Int {
        return fileSize - fileHandler.currentFileSize()
    }
}