package com.ationet.androidterminal.core.data.local.util

import android.content.Context
import com.atio.log.Logger
import com.atio.log.util.debug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object TsnHelper {
    /**
     * TSN file name
     * */
    private const val TsnFileName: String = "TSN"

    /**
     * Gets the current sequence number, and increments it.
     * */
    suspend fun getNextTransactionSequenceNumber(context: Context): Long {
        val file = ResourcesHelper.getResourceFile(context, TsnFileName)
        return try {
            val currentTsn = getTransactionSequenceNumber(file)
            setTransactionSequenceNumber(file, currentTsn + 1)

            currentTsn
        } catch (e: Throwable) {
            Logger.debug("getNextTSN", e.message.orEmpty())
            0
        }
    }

    suspend fun setTransactionSequenceNumber(context: Context, tsn: Long) {
        val file = ResourcesHelper.getResourceFile(context, TsnFileName)
        setTransactionSequenceNumber(file, tsn)
    }

    private fun getTransactionSequenceNumber(file: File): Long {
        if (!file.exists()) {
            file.parent?.let { File(it).mkdirs() }
            return 0
        }

        return readTransactionSequenceNumber(file)
    }

    private suspend fun setTransactionSequenceNumber(file: File, tsn: Long) {
        /**
         * Try create TSN file. If it fails, it already exists.
         * */
        withContext(Dispatchers.IO) {
            file.createNewFile()
        }

        val writer = file.writer()
        withContext(Dispatchers.IO) {
            writer.use {
                writer.write(tsn.toString())
            }
        }
    }

    private fun readTransactionSequenceNumber(file: File): Long {
        val reader = file.bufferedReader()
        val text = reader.use {
            reader.readText()
        }

        return text.toLongOrNull() ?: 0
    }
}