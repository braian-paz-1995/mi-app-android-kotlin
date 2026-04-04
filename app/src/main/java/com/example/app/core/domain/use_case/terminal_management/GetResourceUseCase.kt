package com.ationet.androidterminal.core.domain.use_case.terminal_management

import com.atio.terminal_management.TerminalManagement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetResourceUseCase @Inject constructor(
    private val terminalManagementModule: TerminalManagement,
) {
    suspend operator fun invoke(url: String): File? {
        val file = withContext(Dispatchers.IO) {
            File.createTempFile("terminal_management", "download")
        }

        val inputStream= terminalManagementModule.executeGetResource(url)
        inputStream.buffered().use {
            val outputStream = file.outputStream()
            outputStream.use {
                inputStream.copyTo(outputStream)
                outputStream.flush()
            }
        }

        return file
    }
}