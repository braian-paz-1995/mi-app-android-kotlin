package com.ationet.androidterminal.hal.card_reader.SmartCard

import android.util.Log
import com.ationet.androidterminal.hal.card_reader.SmartCard.SdiNfcCard.Companion
import com.ationet.androidterminal.hal.card_reader.config.*
import com.ationet.androidterminal.hal.card_reader.config.Crypto
import com.ationet.androidterminal.hal.card_reader.config.Utils.Companion.dateToString
import com.ationet.androidterminal.hal.card_reader.config.Utils.Companion.getCurrentDateTime
import com.ationet.androidterminal.hal.card_reader.config.Utils.Companion.hexStringToByteArray
import com.verifone.payment_sdk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class SdiContactless(
    private val sdiManager: SdiManager,
    private val ctlsConfigData: EmvCtlsConfig
) {
    private val crypto = Crypto(sdiManager)
    private val config = ctlsConfigData
    private val cardDetectCallback: CardDetectCallback = CardDetectCallback()

    companion object {
        private const val TAG = "SdiCardCTLS"
    }

    fun initial(): SdiResultCode {
        sdiManager.setCardDetectCallback(cardDetectCallback)

        return SdiResultCode.OK
    }

    fun initialize(): SdiResultCode {
        initial()
        val initOptions = SdiEmvOptions.create()
        initOptions.setOption(SdiEmvOption.TRACE, true)
        initOptions.setOption(SdiEmvOption.TRACE_ADK_LOG, true)
        initOptions.setOption(SdiEmvOption.LED_CBK_EXT, true)
        initOptions.setOption(SdiEmvOption.AUTO_RETAP, true)
        initOptions.setOption(SdiEmvOption.BEEP_CBK_EXT, true)
        val result = sdiManager.emvCtls?.initFramework(60, initOptions)
        return result!!
    }

    fun exit(): SdiResultCode {
        sdiManager.setEmvCallback(null)
        sdiManager.emvCtls.endTransaction(null)
        val result = sdiManager.emvCtls?.exitFramework(null)
        return result!!
    }

    fun startTransactionFlow(): SdiResultCode {
        val response = continueOffline()
        when (response.result) {
            SdiResultCode.EMVSTATUS_ARQC -> {
                crypto.getSensitiveEncryptedData(config.sensitiveTags)
            }

            SdiResultCode.EMVSTATUS_AAC -> {}
            SdiResultCode.EMVSTATUS_TC -> {}
            SdiResultCode.EMVSTATUS_ABORT -> {}
            else -> {}
        }

        return response.result
    }

    fun setupTransaction(): SdiEmvTxnResponse {
        val ctlsTxnConfig = SdiEmvTxn.create()
        val today = getCurrentDateTime()
        val date = today.dateToString("yyMMdd").hexStringToByteArray()
        val time = today.dateToString("hhmmss").hexStringToByteArray()
        val txnOptions = SdiEmvTransactionOptions.create()
        txnOptions.setCtlsOption(SdiEmvCtlsTransactionOption.EMV_CTLS_TXNOP_CANDLIST_CALLBACK, true)
        ctlsTxnConfig.setTransactionOptions(txnOptions)

        val sdiEmvTxnResponse = sdiManager.emvCtls.setupTransaction(
            SdiEmvTransaction.GOODS_SERVICE,
            1, date, time, 1.toLong(), ctlsTxnConfig
        )
        if (sdiEmvTxnResponse.result != SdiResultCode.OK) {
            Log.e(TAG, "Failed to setup transaction")
        }

        return sdiEmvTxnResponse
    }

    fun continueOffline(): SdiEmvTxnResponse {
        SdiEmvTxn.create()
        val response: SdiEmvTxnResponse? = sdiManager.emvCtls.continueOffline(null)
        return response!!
    }

    class CardDetectCallback : SdiCardDetectCallback() {
        override fun cardDetectCallback(
            returnCode: Int,
            tecOut: Short,
            sdiEmvTxn: SdiEmvTxn?,
            pluginResult: ByteArray?
        ) {
            Log.d(
                TAG,
                "CardDetectCallback $returnCode : $tecOut : ${sdiEmvTxn?.cardType}: ${pluginResult.contentToString()}"
            )
        }
    }


    //

    suspend fun readMifareOrUid(): String? = withContext(Dispatchers.IO) {

        val nfc = sdiManager.nfc
        Log.d(TAG, "Inicializando canal NFC version")
        val version = sdiManager.nfc.version.response
        Log.d(TAG, "Versión NFC obtenida: $version")
        try {
            nfc.fieldOff()
        } catch (_: Exception) {
        }
        try {
            nfc.close()
        } catch (_: Exception) {
        }

        val openResult = nfc.open()
        if (openResult != SdiResultCode.OK) {
            Log.e(TAG, "Fallo open(): $openResult")
            return@withContext null
        }

        val fieldOnResult = nfc.fieldOn()
        if (fieldOnResult != SdiResultCode.OK) {
            Log.e(TAG, "Fallo fieldOn(): $fieldOnResult")
            return@withContext null
        }

        try {
            val POLLING_TIMEOUT = 1000L
            val technologyBitmap = EnumSet.of(SdiNfcPollingBitmap.A, SdiNfcPollingBitmap.B, SdiNfcPollingBitmap.F_DEP)
            val nfcPollResponse =sdiManager.nfc.fieldPollingExt(technologyBitmap, POLLING_TIMEOUT, byteArrayOf(0x00))

            val pollingTechs = EnumSet.of(SdiNfcPollingBitmap.A, SdiNfcPollingBitmap.B, SdiNfcPollingBitmap.F_DEP)
            val pollResponse = try {
                nfc.fieldPolling(pollingTechs, 1000L, ByteArray(0))
            } catch (e: Exception) {
                Log.e(TAG, "Error en fieldPolling: ${e.message}")
                return@withContext null
            }

            if (pollResponse.result != SdiResultCode.OK || nfcPollResponse.detectedCards.isNullOrEmpty()) {
                Log.w(TAG, "No se detectaron tarjetas")
                return@withContext null
            }

            val card = nfcPollResponse.detectedCards[0]

            if (card.cardInfo == null || card.cardInfo.isEmpty()) {
                Log.d(TAG,"mCardInfo:${card.cardInfo}")
                Log.e(TAG, "cardInfo vacío, no se puede activar tarjeta")
                return@withContext null
            }
            Log.d(TAG,"mCardType::${card.cardType}")
            Log.d(TAG,"mCardInfo:${card.cardInfo}")
            Log.d(TAG,"mAtq:${card.atq}")
            Log.d(TAG,"sak:${card.sak}")
            Log.d(TAG,"mCardTypeFull:${card.cardTypeFull}")

//            val isMifareCard = isMifareCard(card.sak, card.atq.toShort())
//
//            if (!isMifareCard){
//                Log.e(TAG, "Tarjeta Presentada no es Mifare posible tarjeta Bancaria")
//                return@withContext null
//            }


            val uidBytes = extractField(card, "mCardInfo") ?: extractField(card, "uid")
            val uid = uidBytes?.toHexString() ?: return@withContext null
            Log.d(TAG, "UID detectado: $uid")

           val ndefText = readNdefText(nfc, card.cardType)

            if (ndefText != null) Log.d(TAG, "Texto NDEF leído: $ndefText")

            return@withContext ndefText ?: uid

        } catch (e: Exception) {
            Log.e(TAG, "Error general en lectura NFC: ${e.message}", e)
            return@withContext null
        } finally {
            try {
                nfc.fieldOff()
            } catch (_: Exception) {
            }
            try {
                nfc.close()
            } catch (_: Exception) {
            }
        }
    }
    fun isMifareCard(sak: Short, atqa: Short): Boolean {
        val mifareCombinations = listOf(
            Pair(8.toShort(), 1024.toShort()),     // MIFARE Classic 1K (SAK 0x08, ATQA 0x0400)
            Pair(24.toShort(), 512.toShort()),     // MIFARE Classic 4K (SAK 0x18, ATQA 0x0200)
            Pair(9.toShort(), 1024.toShort()),     // MIFARE Mini (SAK 0x09, ATQA 0x0400)
            Pair(0.toShort(), 17476.toShort()),    // MIFARE Ultralight (SAK 0x00, ATQA 0x4400)
            Pair(8.toShort(), 17408.toShort()),    // MIFARE Ultralight (SAK 0x00, ATQA 0x4400)
            Pair(32.toShort(), 836.toShort())      // MIFARE DESFire (SAK 0x20, ATQA 0x0344)
        )

        return mifareCombinations.contains(Pair(sak, atqa))
    }

    private suspend fun readNdefText(nfc: SdiNfc, cardType: Short): String? {
        val startBlock: Short = 0
        val blocksToRead: Short  =  6// Ajustá según necesidad
        val fullData = ByteArray(blocksToRead * 16)

        for (i in 0 until blocksToRead) {
            val blockNum = i.toString(radix = 16).toShort(16)

            val response = nfc.mifareRead( cardType,  blockNum, 0x04)
            if (response.getResult() != SdiResultCode.OK) {
                Log.e(TAG, "Error leyendo bloque $blockNum: ${response.getResult()}")
                return null
            }

            System.arraycopy(response.getResponse(), 0, fullData, i * 16, 16)
        }

        return parseNdefText(fullData)
    }
    private fun parseNdefText(data: ByteArray): String? {
        val tlvIndex = data.indexOf(0x03.toByte())
        if (tlvIndex == -1) return null

        val length = data.getOrNull(tlvIndex + 1)?.toInt()?.and(0xFF) ?: return null
        val ndefStart = tlvIndex + 2
        if (ndefStart + length > data.size) return null

        if (data[ndefStart] != 0xD1.toByte()) return null

        val typeLength = data[ndefStart + 1].toInt() and 0xFF
        val payloadLength = data[ndefStart + 2].toInt() and 0xFF
        val payloadStart = ndefStart + 3 + typeLength

        if (payloadStart + payloadLength > data.size) return null

        val payload = data.copyOfRange(payloadStart, payloadStart + payloadLength)
        val status = payload[0].toInt()
        val langCodeLen = status and 0x3F

        return payload.copyOfRange(1 + langCodeLen, payload.size).toString(Charsets.UTF_8)
    }
    private fun ByteArray.toHexString() = joinToString("") { "%02X".format(it) }
    private fun extractField(obj: Any, fieldName: String): ByteArray? {
        return try {
            val field = obj.javaClass.getDeclaredField(fieldName)
            field.isAccessible = true
            val value = field.get(obj) as? ByteArray
            if (value != null) Log.d(TAG, "$fieldName: ${value.toHexString()}")
            value
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo obtener '$fieldName': ${e.message}")
            null
        }
    }
}