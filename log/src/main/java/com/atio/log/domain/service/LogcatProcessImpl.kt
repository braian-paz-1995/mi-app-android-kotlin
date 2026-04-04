package com.atio.log.domain.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class LogcatProcessImpl(
    private val packageName: String,
    private val debugEnabled: Boolean,
    private val warningEnabled: Boolean,
    private val errorEnabled: Boolean,
    private val infoEnabled: Boolean,
) : LogcatProcess {
    override suspend fun startLogcat(): Process {
        return startProcess()
    }

    private suspend fun startProcess(): Process {
        val command = buildList {
            add("sh")
            add("-c")
            add("logcat")
            add("-v")
            add("threadtime")
            add("--pid=\$(pidof -s $packageName)")

            if (debugEnabled) {
                add("*:D")
            }

            if (warningEnabled) {
                add("*:W")
            }

            if (errorEnabled) {
                add("*:E")
            }

            if (infoEnabled) {
                add("*:I")
            }
        }

        val processBuilder = ProcessBuilder(command)

        return withContext(Dispatchers.IO) {
            return@withContext processBuilder.start()
        }
    }
}