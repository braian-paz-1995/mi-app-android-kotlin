package com.ationet.androidterminal.hal.card_reader.RfCard

import android.util.Log
import com.ationet.androidterminal.core.domain.hal.HALBuzzer
import com.ationet.androidterminal.hal.card_reader.EmvUtils
import com.ationet.androidterminal.hal.card_reader.config.Crypto
import com.ationet.androidterminal.hal.card_reader.config.EmvContactConfig
import com.ationet.androidterminal.hal.card_reader.config.Utils
import com.ationet.androidterminal.hal.card_reader.config.Utils.Companion.dateToString
import com.ationet.androidterminal.hal.card_reader.config.Utils.Companion.hexStringToByteArray
import com.verifone.payment_sdk.SdiEmvCtTransactionOption
import com.verifone.payment_sdk.SdiEmvOption
import com.verifone.payment_sdk.SdiEmvOptions
import com.verifone.payment_sdk.SdiEmvTransaction
import com.verifone.payment_sdk.SdiEmvTransactionOptions
import com.verifone.payment_sdk.SdiEmvTxn
import com.verifone.payment_sdk.SdiEmvTxnResponse
import com.verifone.payment_sdk.SdiManager
import com.verifone.payment_sdk.SdiResultCode
import kotlin.time.Duration.Companion.seconds

class RfCardReader(
    private val sdiManager: SdiManager,
    private val ctConfigData: EmvContactConfig,
    private val buzzer: HALBuzzer)
{
    private val crypto = Crypto(sdiManager)


    companion object {
        private const val TAG = "SdiCardCT"
    }
    fun playBeep(){
        buzzer.beep(1.seconds)
    }
    fun read(): String {
        initialize()
        val config = ctConfigData
        crypto.getSensitiveEncryptedData(config.sensitiveTags)
        startTransactionFlow()
        val track2Info = EmvUtils.retrieveTagsUsingApi(sdiManager, config.fetchTags)
        exit()
        playBeep()
        return track2Info
    }
    fun initialize(): SdiResultCode {
        val initOptions = SdiEmvOptions.create().apply {
            setOption(SdiEmvOption.TRACE, true)
            setOption(SdiEmvOption.TRACE_ADK_LOG, true)
        }
        sdiManager.emvCt.endTransaction(0)
        val result = sdiManager.emvCt?.initFramework(60, initOptions)
        return result!!
    }
    fun startTransactionFlow(): SdiResultCode {
        sdiManager.smartCardCt.activate()
        startTransaction()
        val response = continueOffline()
        when (response.result) {
            SdiResultCode.EMVSTATUS_TC -> Log.d(TAG, "processApprovedTransaction")
            SdiResultCode.EMVSTATUS_ARQC -> Log.d(TAG, "processApprovedTransactionAfterOffline")
            SdiResultCode.EMVSTATUS_AAC -> Log.d(TAG, "Offline Decline")
            SdiResultCode.EMVSTATUS_ABORT -> Log.d(TAG, "Transaction Aborted")
            else -> Log.d(TAG, "Transaction not approved or unknown result")
        }

        return SdiResultCode.OK
    }
    fun startTransaction(): SdiResultCode {

        // Configuración de la fecha y hora actuales
        val today = Utils.getCurrentDateTime()
        val date = today.dateToString("yyMMdd").hexStringToByteArray()
        val time = today.dateToString("hhmmss").hexStringToByteArray()

        // Configurar opciones de la transacción EMV
        val txnConfig = SdiEmvTxn.create()
        val txnOptions = SdiEmvTransactionOptions.create().apply {
            setCtOption(SdiEmvCtTransactionOption.EMV_CT_SELOP_CBCK_APPLI_SEL, true)
            setCtOption(SdiEmvCtTransactionOption.EMV_CT_TXNOP_LOCAL_CHCK_CALLBACK, true)
        }
        txnConfig.setTransactionOptions(txnOptions)
//        sdiManager.setEmvCallback(emvCallback)
        // Iniciar transacción EMV CT (39-10)
        val sdiEmvTxnResponse = sdiManager.emvCt.startTransaction(
            SdiEmvTransaction.GOODS_SERVICE,
            1,
            date,
            time,
            1L,
            txnConfig
        )
        sdiManager.emvCt.continueOffline(SdiEmvTxn.create())
        val onlineResult = true
        val onlineResp = byteArrayOf(0x30, 0x30)
        val onlineResponse =
            sdiManager.emvCt.continueOnline(onlineResult, onlineResp, SdiEmvTxn.create())

        return onlineResponse.result
    }
    fun continueOffline(): SdiEmvTxnResponse {

        val sdiEmvTxn = SdiEmvTxn.create()
        return sdiManager.emvCt.continueOffline(sdiEmvTxn).also {
            Log.d(TAG, "Command Result: ${it.result.name}")
        }
    }
    fun exit(): SdiResultCode {
        sdiManager.emvCt.endTransaction(0)
        val result = sdiManager.emvCt?.exitFramework(null)
        result ?: return SdiResultCode.FAIL
        sdiManager.nfc.fieldOff()
        sdiManager.nfc.close()
        sdiManager.setEmvCallback(null)
        sdiManager.emvCtls.endTransaction(null)
        val resultCtls = sdiManager.emvCtls?.exitFramework(null)
        resultCtls ?: return SdiResultCode.FAIL
        return SdiResultCode.OK
    }

}
