package com.ationet.androidterminal.core.domain.hal.card_reader

import android.util.Log

abstract class M0NfcReaderBase : NfcReader {
    override fun getUid(): ByteArray? {
        val uidPages = readFromDevice(offset = 0, quantity = PAGE_SIZE * 2)
        if(uidPages == null) {
            Log.w(TAG, "Nothing read from device")
            return null
        }

        if (uidPages.size < PAGE_SIZE * 2) {
            Log.w(TAG, "UID blocks of invalid size")
            return null
        }

        return uidPages.readUid()
    }

    override fun read(offset: Int, quantity: Int): ByteArray? {
        /* Skip the first block to read data */
        val localOffset = offset + BLOCK_0_SIZE * PAGE_SIZE
        return readFromDevice(localOffset, quantity)
    }

    private fun readFromDevice(offset: Int, quantity: Int): ByteArray? {
        val bufferRead = mutableListOf<Byte>()
        /* Skip the first block to read data */
        var localOffset = offset

        var size = quantity
        while (size > 0) {
            val page = getPage(localOffset)
            val pageOffset = getPageOffset(localOffset)

            val pageData = ByteArray(PAGE_SIZE)

            val success = try {
                readPageData(
                    page = page,
                    data = pageData
                )
            } catch (e: Throwable) {
                Log.e(TAG, "Failed to read data at page: $page - offset: $offset")
                return null
            }

            if (!success) {
                Log.w(TAG, "Failed to read data at page: $page - offset: $offset")
                return null
            }

            val elementsToAdd = if (pageOffset > 0) {
                pageData.drop(pageOffset).take(size)
            } else {
                pageData.take(size)
            }

            bufferRead.addAll(elementsToAdd)

            size -= elementsToAdd.size
            localOffset += elementsToAdd.size
        }

        return bufferRead.toByteArray()
    }

    abstract fun readPageData(page: Int, data: ByteArray): Boolean

    private fun getPage(offset: Int): Int = offset.div(PAGE_SIZE)
    private fun getPageOffset(offset: Int) = offset % PAGE_SIZE
    private fun ByteArray.readUid(): ByteArray {
        return (this.sliceArray(0..2) + this.sliceArray(4..7))
    }

    companion object {
        private const val TAG: String = "M0NFCReader"
        private const val PAGE_SIZE: Int = 4
        private const val BLOCK_0_SIZE: Int = 4
    }
}