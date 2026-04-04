package com.atio.log.domain.service

import java.io.File


interface FileHandler {
    val archive: File
    val temporaryDirectory: File
    suspend fun currentFileSize(): Int
    suspend fun write(buffer: ByteArray, amount: Int)
    suspend fun archiveCurrentFile(maxFileQuantity: Int)
    fun getArchiveLogs(): List<File>
    fun removeArchiveLogsByName(logs: List<String>)
}