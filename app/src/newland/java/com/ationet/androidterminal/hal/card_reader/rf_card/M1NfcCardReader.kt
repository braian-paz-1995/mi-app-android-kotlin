package com.ationet.androidterminal.hal.card_reader.rf_card

import android.util.Log
import com.ationet.androidterminal.core.domain.hal.card_reader.M1NfcReaderBase
import com.newland.sdk.module.rfcard.RFCardModule
import com.newland.sdk.module.rfcard.RFKeyMode

class M1NfcCardReader(
    private val snr: ByteArray,
    private val rf: RFCardModule
) : M1NfcReaderBase() {
    override fun readBlockData(block: Int, data: ByteArray): Boolean {
        val blockData = try {
            rf.m1ReadBlockData(block)
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to read block $block data")
            return false
        }

        val copySize = minOf(data.size, blockData.size)
        blockData.copyInto(
            destination = data,
            endIndex = copySize
        )

        return true
    }

    override fun authenticate(block: Int, key: ByteArray): Boolean {
        return try {
            Log.d(TAG, "Authenticating block $block")

            val success = rf.m1Authenticate(RFKeyMode.KEYA_0X60, snr, block, key)

            if(success) {
                Log.d(TAG, "Block $block authenticated")
            } else {
                Log.w(TAG, "Failed to authenticate block $block")
            }

            success
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to authenticate", e)
            return false
        }
    }


    companion object {
        private const val TAG: String = "M1NfcCardReader"
    }
}