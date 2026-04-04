package com.atio.log.util

import java.io.File

object FileHelper {
    fun filterFiles(directory: File?, extension: String, prefixName: String = ""): List<File> =
        directory?.listFiles { _, name ->
            name.endsWith(extension) && name.contains(prefixName, ignoreCase = true)
        }.orEmpty().sortedBy { it.lastModified() }
}