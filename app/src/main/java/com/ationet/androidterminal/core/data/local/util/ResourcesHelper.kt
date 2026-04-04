package com.ationet.androidterminal.core.data.local.util

import android.content.Context
import java.io.File

/**
 * Contains helper methods to access resources
 * */
object ResourcesHelper {
    private const val DirectoryName: String = "AtionetTerminal"
    const val ConfigEntryName: String = "config.txt"

    /**
     * Gets a resource file location
     * */
    fun getResourceFile(
        context: Context,
        resourceName: String,
        directory: File? = getResourcesDirectory(context)
    ): File {
        val dir = directory ?: getResourcesDirectory(context)
        val file = File(dir, resourceName)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    private fun getResourcesDirectory(context: Context): File {
        val externalDirectory = context.filesDir
        val dir = File(externalDirectory, DirectoryName)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
}