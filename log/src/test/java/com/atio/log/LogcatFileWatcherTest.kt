package com.atio.log

import android.util.Log
import com.atio.log.domain.service.LogFileHandler
import com.atio.log.domain.service.LogcatFileWatcherService
import com.atio.log.domain.service.LogcatProcess
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.FunctionAnswer
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.yield
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class LogcatFileWatcherTest {

    // File watcher should cut file when size reaches the limit

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Logcat file watcher should switch files when size reached`() = runTest {
        val filesQuantity = 1
        val fileSize = 15

        val streamContent = """/!\TEST/!\/!\TEST/!\/!\TEST/!\/!\TEST/!\"""
        val expectedContent = """/!\TEST/!\"""

        // Create process mock
        val processMock = mockk<LogcatProcess>()

        val process = mockk<Process>()
        every { process.inputStream } returns ByteArrayInputStream(streamContent.toByteArray())


        val waitResponse = FunctionAnswer {
            Log.i("LogcatService", "Waiting for process")
            return@FunctionAnswer 0
        }
        coEvery { process.waitFor() } answers waitResponse

        coEvery { processMock.startLogcat() } returns process

        // Create files handler mock
        val directory = createTempDirectory().toFile()
        val filesHandler = LogFileHandler(
            coroutineContext = coroutineContext,
            archive = directory,
            temporaryDirectory = File(directory, "tmp"),
            prefix = "nat",
            header = null,
            onLogFileArchived = {
                Log.i("LogcatService", "File archived: ${it.path}")
            },
            onLogFileCreated = {
                Log.i("LogcatService", "File created: ${it.path}")
            },
            onArchivedLogsRemoved = {
                Log.i("LogcatService", "Files removed: $it")
            }
        )

//        /* Mock log */
//        mockkStatic(Log::class)
//        every { Log.e(any(), any(), any()) } returns 0
//        every { Log.i(any(), any()) } returns 0

        // Service under test
        val service = LogcatFileWatcherService(
            logcatProcess = processMock,
            fileHandler = filesHandler,
            fileSize = fileSize,
            fileQuantity = filesQuantity
        )

        // Act
        val job = launch {
            service.start()
        }
        advanceUntilIdle()

        job.join()

        // Assert
        /* Verify call to switch file once */
        val files = directory.listFiles()!!.filter { it.isFile }

        files.size shouldBe 1

        val firstFile = files.first()
        firstFile shouldNotBeNull {
            val readContent = readText()

            readContent shouldBe expectedContent
        }
    }
}