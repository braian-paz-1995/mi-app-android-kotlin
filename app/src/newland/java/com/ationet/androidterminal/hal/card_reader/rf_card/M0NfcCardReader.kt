package com.ationet.androidterminal.hal.card_reader.rf_card

import android.util.Log
import com.ationet.androidterminal.core.domain.hal.card_reader.M0NfcReaderBase
import com.newland.sdk.module.rfcard.RFCardModule

class M0NfcReader(
    private val rf: RFCardModule
) : M0NfcReaderBase() {

    @OptIn(ExperimentalStdlibApi::class)
    override fun readPageData(page: Int, data: ByteArray): Boolean {
        Log.d(TAG, "Requested page: $page")

        val received = try {
            rf.m0ReadBlockData(page)
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to read data at page $page")
            return false
        }

        if(received == null) {
            Log.w(TAG, "Nothing received")
            return false
        }

        val endIndex = minOf(data.size, received.size)

        received.copyInto(
            destination = data,
            startIndex = 0,
            endIndex = endIndex
        )

        Log.v(TAG, "Received '${data.toHexString(HexFormat.UpperCase)}'")

        return true
    }

    companion object {
        private const val TAG : String = "M0NFCReader"
    }
}