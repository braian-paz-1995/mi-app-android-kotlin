
package com.ationet.androidterminal.hal.card_reader.config

import android.util.Log
import com.ationet.androidterminal.hal.card_reader.config.Utils.Companion.hexStringToByteArray
import com.ationet.androidterminal.hal.card_reader.config.Utils.Companion.toHexString
import com.verifone.payment_sdk.SdiDataEncResponse
import com.verifone.payment_sdk.SdiDataOption
import com.verifone.payment_sdk.SdiIntegerResponse
import com.verifone.payment_sdk.SdiManager
import com.verifone.payment_sdk.SdiResultCode
import java.util.EnumSet

class Crypto(val sdiManager: SdiManager) {

    companion object {
        private const val TAG = "SDICrypto"
        private const val DATA_HANDLE = "TDES_DUKPT_DATA" // HostName used for DATA ENCRYPTION process

    }
    private fun open(host: String): Int? {
        Log.d(TAG, "Command Crypto Open (70-00)")
        val result: SdiIntegerResponse = sdiManager.crypto.open(host)
        Log.d(TAG, "Command Result: ${result.result} ")
        Log.d(TAG, "Command Result Response: ${result.response} ")
        if (result.result == SdiResultCode.OK) {
            return result.response
        }
        return null
    }

    // Get encrypted transaction data - Get Enc Data (29-00)
    fun getSensitiveEncryptedData(tagList: List<String>) {
        val handle = open(DATA_HANDLE)
        if (handle != null) {
            val tagListString = StringBuilder()
            for (tag in tagList) {
                tagListString.append(tag)
                tagListString.append("00") // Make the tag response as variable length
            }
            val appData = byteArrayOf() // Optional application data (BERTLV encoded)
            val options: EnumSet<SdiDataOption>? = null // data options truncation/padding
            val useStoredTx = false // Use stored transaction data
            val iv = null // optional initialization vector

            Log.d(TAG, "Command getEncData (29-00)")
            val encodedDataResponse: SdiDataEncResponse = sdiManager.data.getEncData(
                handle,
                tagListString.toString().hexStringToByteArray(),
                appData,
                options,
                useStoredTx,
                iv
            )
            Log.d(TAG, "Command Result: ${encodedDataResponse.result} ")
            Log.d(TAG, "Command Response: ${encodedDataResponse?.response?.toHexString()}")
            Log.d(TAG, "Command KSN: ${encodedDataResponse?.ksn?.toHexString()}")
            Log.d(TAG, "Command IV: ${encodedDataResponse?.iv?.toHexString()}")
            sdiManager.crypto.close(handle)
        } else {
            Log.d(TAG, "Command Result: Crypto Open Failed")
        }
    }
}