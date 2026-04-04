package com.ationet.androidterminal.core.domain.use_case.terminal_management

import android.content.Context
import android.util.Log
import com.atio.log.createFileHandler
import com.atio.log.domain.service.FileHandler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

class CreateFileHandlerUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    operator fun invoke() : FileHandler {
        return createFileHandler(
            coroutineContext = Dispatchers.Default + SupervisorJob(),
            prefix = FILE_NAME_PREFIX,
            rootPath = getRootPath(context),
            onLogFileArchived = {
                Log.d(TAG, "Log file archived: ${it.name}")
            },
            onLogFileCreated = {
                Log.d(TAG, "Log file created: ${it.name}")
            },
            onArchivedLogsRemoved = {
                Log.d(TAG, "Logs removed: ${it.joinToString()}")
            }
        )
    }

    private fun getRootPath(context: Context): String {
        return context.getExternalFilesDir(null)?.path ?: run {
            Log.d(TAG, "Log initialization: External files directory not available. Writing to internal storage...")
            context.filesDir.path
        }
    }

    private companion object {
        private const val FILE_NAME_PREFIX: String = "aat"
        private const val TAG: String = "LogcatFileManager"
    }
}