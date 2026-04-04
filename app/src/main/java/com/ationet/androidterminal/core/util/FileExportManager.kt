package com.ationet.androidterminal.core.util

import android.content.Context
import java.io.File

class FileExportManager(
    private val context: Context
) {
    fun exportConfigAndDatabase(): Boolean {
        return try {
            val exportDir = File(context.getExternalFilesDir(null), "backup")
            if (!exportDir.exists()) exportDir.mkdirs()

            // -------- DB --------
            val dbFile = context.getDatabasePath("aat_database.db")
            val dbExport = File(exportDir, "aat_database.db")

            if (dbFile.exists()) {
                dbFile.copyTo(dbExport, overwrite = true)
            }

            // -------- CONFIG (DataStore) --------
            val configFile = File(
                context.filesDir,
                "datastore/configuration.preferences_pb"
            )

            val configExport = File(exportDir, "configuration.preferences_pb")

            if (configFile.exists()) {
                configFile.copyTo(configExport, overwrite = true)
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}