package com.ationet.androidterminal.hal.card_reader.SmartCard

import android.util.Log
import com.verifone.payment_sdk.SdiManager
import com.verifone.payment_sdk.SdiNfc
import com.verifone.payment_sdk.SdiResultCode

class SdiNfcCard(private val sdiManager: SdiManager) {

    companion object {
        private const val TAG = "SdiNfcCard"
    }
    @Synchronized
    private fun initializeNfc(): SdiNfc? {

        try {
            Log.d(TAG, "NFC Client Init (31-10)")
            val result = sdiManager.nfc?.init(0)
            Log.d(TAG, "Command result: ${result?.name}")
            // Intentar limpiar el canal antes de abrirlo
            try {
                Log.d(TAG, "Intentando cerrar canal NFC previamente abierto (por si acaso)")
                sdiManager.nfc.fieldOff()
            } catch (e: Exception) {
                Log.w(TAG, "Error en fieldOff previo: ${e.message}")
            }

            try {
                sdiManager.nfc.close()
            } catch (e: Exception) {
                Log.w(TAG, "Error en close previo: ${e.message}")
            }
            val response = sdiManager.nfc.ping()
            // Abrir canal limpio
            val openResult = sdiManager.nfc.open()
            if (openResult != SdiResultCode.OK) {
                Log.e(TAG, "Fallo open(): $openResult")
                return null
            }

            val fieldOnResult = sdiManager.nfc.fieldOn()
            if (fieldOnResult != SdiResultCode.OK) {
                Log.e(TAG, "Fallo fieldOn(): $fieldOnResult")
                sdiManager.nfc.close()
              return null
            }

            Log.d(TAG, "Canal NFC listo")
            return sdiManager.nfc

        } catch (e: Exception) {
            Log.e(TAG, "Excepción en inicialización NFC: ${e.message}", e)
            return null
        }
    }



    fun getVersion(): String {
        val nfc = initializeNfc() ?: return "NFC INIT FAILED"
        val version = sdiManager.nfc.version.response
        Log.d(TAG, "Versión NFC obtenida: $version")

        try { nfc.fieldOff() } catch (_: Exception) {}
        try { nfc.close() } catch (_: Exception) {}

        return version
    }

}