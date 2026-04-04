package com.ationet.androidterminal.hal.card_reader.SmartCard

import android.util.Log
import com.ationet.androidterminal.core.domain.hal.HALBuzzer
import com.ationet.androidterminal.hal.card_reader.EmvUtils
import com.ationet.androidterminal.hal.card_reader.config.Crypto
import com.ationet.androidterminal.hal.card_reader.config.EmvContactConfig
import com.ationet.androidterminal.hal.card_reader.config.EmvCtlsConfig
import com.verifone.payment_sdk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

class SmartCardReader(
    private val sdiManager: SdiManager,
    private val ctlsConfigData: EmvCtlsConfig,
    private val buzzer: HALBuzzer,
) {

    companion object {
        private const val TAG = "SmartCardReader"
    }

    private val configData = ctlsConfigData
    private val crypto = Crypto(sdiManager)
    private val sdiContactless = SdiContactless(sdiManager, ctlsConfigData)


    fun playBeep() {
        buzzer.beep(1.seconds)
    }

    suspend fun read(): String = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🟢 Iniciando flujo EMV Contactless")
            sdiContactless.startTransactionFlow()
            crypto.getSensitiveEncryptedData(ctlsConfigData.sensitiveTags)
            val track2Info = EmvUtils.retrieveTagsUsingApi(sdiManager, ctlsConfigData.fetchTags)

            playBeep()
            return@withContext track2Info
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error en lectura EMV Contactless", e)
            return@withContext "Error leyendo tarjeta EMV CTLS"
        }
    }


    fun startContactlessTransaction(): String {
        Log.d(TAG, "🟡 Inicializando configuración Contactless")
        sdiContactless.initialize()
        sdiContactless.setupTransaction()
        return "Contactless setup completo"
    }


    suspend fun readNFC(mifareDetected: String): String = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🟢 Leyendo tarjeta NFC MIFARE/UID: $mifareDetected")
            playBeep()
            val track2Info = EmvUtils.retrieveTagsUsingApiNfc(sdiManager,mifareDetected)
            return@withContext track2Info
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error leyendo NFC MIFARE", e)
            "Error leyendo NFC MIFARE"
        }
    }

    suspend fun startNFCTransaction(): String? {
        Log.d(TAG, "🔵 Iniciando flujo NFC Contactless...")
        var Detect = sdiContactless.readMifareOrUid()
        return Detect
    }

}
