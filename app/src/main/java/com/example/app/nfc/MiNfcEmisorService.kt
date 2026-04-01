package com.example.app.nfc

import android.nfc.cardemulation.HostApduService
import android.os.Bundle

/**
 * Emula una tarjeta NFC tipo ISO-DEP (HCE).
 *
 * Otro teléfono en modo lector podrá "detectar" este dispositivo
 * y seleccionar el AID definido para recibir el payload.
 */
class MiNfcEmisorService : HostApduService() {

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null) return STATUS_FAILED

        return if (isSelectAidCommand(commandApdu)) {
            NfcPayloadStore.getPayload(this).toByteArray(Charsets.UTF_8) + STATUS_SUCCESS
        } else {
            STATUS_FAILED
        }
    }

    override fun onDeactivated(reason: Int) {
        // No-op
    }

    private fun isSelectAidCommand(commandApdu: ByteArray): Boolean {
        if (commandApdu.size < SELECT_APDU_HEADER_LENGTH) return false

        return commandApdu[0] == 0x00.toByte() && // CLA
            commandApdu[1] == 0xA4.toByte() &&     // INS: SELECT
            commandApdu[2] == 0x04.toByte() &&     // P1: select by name
            commandApdu[3] == 0x00.toByte() &&     // P2
            commandApdu[4].toInt() == AID.size &&
            commandApdu.copyOfRange(5, 5 + AID.size).contentEquals(AID)
    }

    companion object {
        // AID de ejemplo. Si cambias este valor, también actualiza res/xml/apduservice.xml
        private val AID = byteArrayOf(
            0xF0.toByte(), 0x12.toByte(), 0x34.toByte(),
            0x56.toByte(), 0x78.toByte(), 0x90.toByte()
        )

        private const val SELECT_APDU_HEADER_LENGTH = 5

        private val STATUS_SUCCESS = byteArrayOf(0x90.toByte(), 0x00.toByte())
        private val STATUS_FAILED = byteArrayOf(0x6A.toByte(), 0x82.toByte())
    }
}
