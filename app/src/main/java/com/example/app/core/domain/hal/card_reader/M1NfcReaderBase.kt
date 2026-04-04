package com.ationet.androidterminal.core.domain.hal.card_reader

import android.util.Log

abstract class M1NfcReaderBase : NfcReader {
    override fun getUid(): ByteArray? {
        val manufacturerData = readBlock(
            block = MANUFACTURER_DATA_BLOCK,
            key = ManufacturerDataKey
        ) ?: readBlock(
            block = MANUFACTURER_DATA_BLOCK,
            key = FactoryDataKey
        ) ?: readBlock(
            block = MANUFACTURER_DATA_BLOCK,
            key = DefaultKey
        )
        if (manufacturerData == null) {
            Log.w(TAG, "Manufacturer data not found")
            return null
        }

        if (manufacturerData.size < UID_SIZE) {
            Log.w(TAG, "Manufacturer data block of invalid size")
            return null
        }

        return manufacturerData.sliceArray(0 until UID_SIZE)
    }

    override fun read(offset: Int, quantity: Int): ByteArray? {
        val bufferRead = mutableListOf<Byte>()
        var localOffset = offset

        var size = quantity
        while (size > 0) {
            val blockNumber = getBlockNumber(localOffset)
            val blockOffset = getBlockOffset(localOffset)

            val result = try {
                readBlock(blockNumber)
            } catch (e: Throwable) {
                Log.e(TAG, "Failed to read data at block: $blockNumber - offset: $offset")
                return bufferRead.toByteArray()
            }

            if (result == null) {
                Log.d(TAG, "Failed to read. Return buffer")
                return bufferRead.toByteArray()
            }

            val read = if (blockOffset > 0) {
                bufferRead.addAll(result.drop(blockOffset))
                result.size - blockOffset
            } else {
                bufferRead.addAll(result.asIterable())
                result.size
            }

            size -= read
            localOffset += read
        }

        return bufferRead.toByteArray()
    }


    @OptIn(ExperimentalStdlibApi::class)
    private fun readBlock(block: Int, key: ByteArray = DefaultKey): ByteArray? {
        return try {
            if (!authenticate(block, key)) {
                Log.w(TAG, "Block $block authentication failed")
                return null
            }

            val blockData = ByteArray(size = 16)
            val readSuccess = readBlockData(block, blockData)
            if (!readSuccess) {
                Log.w(TAG, "Nothing received")
            } else {
                Log.v(TAG, "GOT '${blockData.toHexString(HexFormat.UpperCase)}'")
            }

            blockData
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to read block $block", e)
            return null
        }
    }

    protected abstract fun readBlockData(block: Int, data: ByteArray) : Boolean
    protected abstract fun authenticate(block: Int, key: ByteArray): Boolean

    private fun getBlockOffset(offset: Int): Int {
        return offset % BLOCK_SIZE
    }

    /**
     * Converts a linear offset to a block in card
     * */
    private fun getBlockNumber(offset: Int): Int {
        var blockNumber = offset.div(BLOCK_SIZE)

        if (blockIsTrailer(blockNumber)) {
            blockNumber += 1
        }

        return blockNumber + SMALL_SECTOR_SIZE // Remove the first sector
    }

    private fun blockIsTrailer(block: Int): Boolean {
        return when {
            block < SMALL_SECTOR_MAX_VIRTUAL_BLOCK -> block % SMALL_SECTOR_SIZE == SMALL_SECTOR_BLOCK_TRAILER
            else -> (block - SMALL_SECTOR_MAX_VIRTUAL_BLOCK) % LARGE_SECTOR_SIZE == LARGE_SECTOR_BLOCK_TRAILER
        }
    }

    companion object {
        private const val TAG: String = "M1NfcCardReader"
        private val DefaultKey: ByteArray = byteArrayOf(
            0xD3.toByte(),
            0xF7.toByte(),
            0xD3.toByte(),
            0xF7.toByte(),
            0xD3.toByte(),
            0xF7.toByte()
        )
        private val ManufacturerDataKey: ByteArray = byteArrayOf(
            0xA0.toByte(),
            0xA1.toByte(),
            0xA2.toByte(),
            0xA3.toByte(),
            0xA4.toByte(),
            0xA5.toByte()
        )

        private val FactoryDataKey: ByteArray = byteArrayOf(
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte()
        )

        private const val UID_SIZE: Int = 7

        private const val MANUFACTURER_DATA_BLOCK: Int = 0

        private const val BLOCK_SIZE: Int = 16

        private const val SMALL_SECTOR_SIZE: Int = 4
        private const val SMALL_SECTOR_QUANTITY: Int = 32
        private const val SMALL_SECTOR_MAX_VIRTUAL_BLOCK: Int =
            SMALL_SECTOR_QUANTITY * SMALL_SECTOR_SIZE


        private const val SMALL_SECTOR_BLOCK_TRAILER: Int = 3
        private const val LARGE_SECTOR_SIZE: Int = 16
        private const val LARGE_SECTOR_BLOCK_TRAILER: Int = 15
    }
}