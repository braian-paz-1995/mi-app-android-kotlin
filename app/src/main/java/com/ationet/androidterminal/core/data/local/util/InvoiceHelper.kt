package com.ationet.androidterminal.core.data.local.util

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object InvoiceHelper {
    /**
     * Invoice file name
     * */
    private const val InvoiceFileName: String = "INVOICE"


    /**
     * Gets the next invoice number
     * */
    suspend fun getNextInvoiceNumber(context: Context): Long {
        val file = ResourcesHelper.getResourceFile(context, InvoiceFileName)
        return try {
            val currentTsn = getCurrentInvoice(file)
            setInvoice(file, currentTsn + 1)

            currentTsn
        } catch (e: Throwable) {
            Log.d("getNextInvoice", "Failed to get next invoice number", e)
            0
        }
    }

    suspend fun setInvoiceNumber(context: Context, invoiceNumber: Long) {
        val file = ResourcesHelper.getResourceFile(context, InvoiceFileName)
        try {
            setInvoice(file, invoiceNumber)
        } catch (e: Throwable) {
            Log.e("setInvoice", "Failed to set invoice", e)
        }
    }

    private fun getCurrentInvoice(file: File): Long {
        /**
         * Ensure parent directories exists
         * */
        file.parent?.let { parent ->
            File(parent).mkdirs()
        }

        if (!file.exists()) {
            return 1
        }

        return readInvoiceNumber(file)
    }

    private fun readInvoiceNumber(file: File): Long {
        val reader = file.bufferedReader()
        val text = reader.use {
            reader.readText()
        }

        return text.toLongOrNull() ?: 1
    }

    private suspend fun setInvoice(file: File, invoiceNumber: Long) {
        /**
         * Ensure parent directories exists
         * */
        file.parent?.let { parent ->
            File(parent).mkdirs()
        }

        /**
         * Try create INVOICE file. If it fails, it already exists.
         * */
        withContext(Dispatchers.IO) {
            file.createNewFile()
        }

        val writer = file.writer()
        withContext(Dispatchers.IO) {
            writer.use {
                writer.write(invoiceNumber.toString())
            }
        }
    }
}