package com.example.app.nfc

import android.nfc.NdefRecord
import android.nfc.cardemulation.HostApduService
import android.os.Bundle

/**
 * Emula una etiqueta NFC Forum Type 4 (NDEF).
 *
 * Esto permite que un lector NFC genérico (incluido otro teléfono)
 * pueda leer el texto guardado en [NfcPayloadStore] como si fuera una etiqueta.
 */
class MiNfcEmisorService : HostApduService() {

    private var selectedFile: ByteArray? = null

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null || commandApdu.isEmpty()) return STATUS_FAILED

        return when {
            isSelectAid(commandApdu) -> {
                selectedFile = null
                STATUS_SUCCESS
            }

            isSelectFile(commandApdu, FILE_ID_CC) -> {
                selectedFile = FILE_ID_CC
                STATUS_SUCCESS
            }

            isSelectFile(commandApdu, FILE_ID_NDEF) -> {
                selectedFile = FILE_ID_NDEF
                STATUS_SUCCESS
            }

            isReadBinary(commandApdu) -> readBinary(commandApdu)
            else -> STATUS_FAILED
        }
    }

    override fun onDeactivated(reason: Int) {
        selectedFile = null
    }

    private fun readBinary(commandApdu: ByteArray): ByteArray {
        val fileBytes = when {
            selectedFile.contentEquals(FILE_ID_CC) -> CAPABILITY_CONTAINER
            selectedFile.contentEquals(FILE_ID_NDEF) -> buildNdefFile()
            else -> return STATUS_FILE_NOT_FOUND
        }

        if (commandApdu.size < 5) return STATUS_FAILED

        val offset = ((commandApdu[2].toInt() and 0xFF) shl 8) or (commandApdu[3].toInt() and 0xFF)
        val le = commandApdu[4].toInt() and 0xFF

        if (offset >= fileBytes.size) return STATUS_END_OF_FILE

        val end = (offset + le).coerceAtMost(fileBytes.size)
        return fileBytes.copyOfRange(offset, end) + STATUS_SUCCESS
    }

    private fun buildNdefFile(): ByteArray {
        val payload = NfcPayloadStore.getPayload(this)
        val languageCode = "es".toByteArray(Charsets.US_ASCII)
        val textBytes = payload.toByteArray(Charsets.UTF_8)
        val status = languageCode.size.toByte()

        val ndefPayload = byteArrayOf(status) + languageCode + textBytes
        val textRecord = NdefRecord(
            NdefRecord.TNF_WELL_KNOWN,
            NdefRecord.RTD_TEXT,
            ByteArray(0),
            ndefPayload
        )

        val recordBytes = textRecord.toByteArray()
        val nlen = recordBytes.size.coerceAtMost(MAX_NDEF_BYTES)

        return byteArrayOf(
            ((nlen shr 8) and 0xFF).toByte(),
            (nlen and 0xFF).toByte()
        ) + recordBytes.copyOf(nlen)
    }

    private fun isSelectAid(apdu: ByteArray): Boolean =
        apdu.size >= 5 + NDEF_APP_AID.size &&
            apdu[0] == 0x00.toByte() &&
            apdu[1] == 0xA4.toByte() &&
            apdu[2] == 0x04.toByte() &&
            apdu[3] == 0x00.toByte() &&
            apdu[4].toInt() == NDEF_APP_AID.size &&
            apdu.copyOfRange(5, 5 + NDEF_APP_AID.size).contentEquals(NDEF_APP_AID)

    private fun isSelectFile(apdu: ByteArray, fileId: ByteArray): Boolean =
        apdu.size >= 7 &&
            apdu[0] == 0x00.toByte() &&
            apdu[1] == 0xA4.toByte() &&
            apdu[2] == 0x00.toByte() &&
            apdu[3] == 0x0C.toByte() &&
            apdu[4] == 0x02.toByte() &&
            apdu[5] == fileId[0] &&
            apdu[6] == fileId[1]

    private fun isReadBinary(apdu: ByteArray): Boolean =
        apdu.size >= 5 &&
            apdu[0] == 0x00.toByte() &&
            apdu[1] == 0xB0.toByte()

    companion object {
        private val NDEF_APP_AID = byteArrayOf(
            0xD2.toByte(),
            0x76.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x85.toByte(),
            0x01.toByte(),
            0x01.toByte()
        )

        private val FILE_ID_CC = byteArrayOf(0xE1.toByte(), 0x03.toByte())
        private val FILE_ID_NDEF = byteArrayOf(0xE1.toByte(), 0x04.toByte())

        // NFC Forum Type 4 Tag Capability Container (15 bytes)
        private val CAPABILITY_CONTAINER = byteArrayOf(
            0x00, 0x0F,
            0x20,
            0x00, 0x3B,
            0x00, 0x34,
            0x04, 0x06,
            FILE_ID_NDEF[0], FILE_ID_NDEF[1],
            0x00, 0x32,
            0x00, 0x00
        )

        private const val MAX_NDEF_BYTES = 0x0032

        private val STATUS_SUCCESS = byteArrayOf(0x90.toByte(), 0x00.toByte())
        private val STATUS_FAILED = byteArrayOf(0x6A.toByte(), 0x82.toByte())
        private val STATUS_FILE_NOT_FOUND = byteArrayOf(0x6A.toByte(), 0x82.toByte())
        private val STATUS_END_OF_FILE = byteArrayOf(0x62.toByte(), 0x82.toByte())
    }
}
