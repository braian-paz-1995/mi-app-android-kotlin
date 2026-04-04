package com.ationet.androidterminal.core.domain.hal.card_reader

import android.util.Log

object NDefCardReader : NfcCardReader {
    private const val TAG: String = "NfcReader"
    private const val RECORD_1: Int = 0

    private const val BLOCK_SIZE: Int = 16

    /**
     * The TLV NDEF Type.
     * */
    private const val NDEF_TYPE : Byte = 0x03
    /**
     * The amount of bytes to read to get TLV information
     * */
    private const val TLV_READ_SIZE : Int = 2

    @OptIn(ExperimentalStdlibApi::class)
    override fun readCard(reader: NfcReader): NfcCard? {
        // Get UID
        val uid = reader.getUid()
        if(uid == null) {
            Log.w(TAG, "Failed to get card UID")
            return null
        }

        Log.d(TAG, "GOT UID: ${uid.toHexString(HexFormat.UpperCase)}")

        // Read record 1 first block
        val record1 = reader.read(
            offset = RECORD_1,
            quantity = TLV_READ_SIZE
        )
        if (record1 == null) {
            Log.w(TAG, "Received empty register one buffer")
            return NfcCard(
                uid = uid.toList(),
                message = null
            )
        }

        if (record1.size < TLV_READ_SIZE) {
            Log.w(TAG, "Received record one header block buffer of invalid size")
            return NfcCard(
                uid = uid.toList(),
                message = null
            )
        }

        if (record1.isUnformatted()) {
            Log.w(TAG, "Card unformatted")
            return NfcCard(
                uid = uid.toList(),
                message = null
            )
        }

        /*
         * Each NDEF record starts with a TLV (Type-Length-Value) data.
         * First, we try to detect if we actually have a NDEF TLV type.
         * If we don't, why bother?
         * */
        if(record1[0] != NDEF_TYPE) {
            val type = record1[0].toHexString(
                format = HexFormat {
                    bytes {
                        upperCase = true
                        byteSeparator = " "
                    }
                }
            )
            Log.w(TAG, "Card has no NDEF record (GOT '$type')")
            return NfcCard(
                uid = uid.toList(),
                message = null
            )
        }

        /* Get the LENGTH of TLV */
        val length = record1[1].toInt()

        /* Skip Type and Length bytes */
        val offset = RECORD_1 * BLOCK_SIZE + 2
        val value = reader.read(
            offset = offset,
            quantity = length
        )
        if (value == null) {
            Log.w(TAG, "Failed to read card")
            return NfcCard(
                uid = uid.toList(),
                message = null
            )
        }

        Log.d(TAG, "NDEF read (length=$length): ${value.toHexString()}")

        return NfcCard(
            uid = uid.toList(),
            message = NdefMessage.parse(value, length)
        )
    }

    private fun ByteArray.isUnformatted(index: Int = 0): Boolean {
        if (size < 4) {
            return false
        }

        if (!indices.contains(index)) {
            return false
        }

        if (!indices.contains(index + 3)) {
            return false
        }

        return this[index] == 0xFF.toByte() && this[index + 1] == 0xFF.toByte() && this[index + 2] == 0xFF.toByte() && this[index + 3] == 0xFF.toByte()
    }
}