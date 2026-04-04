package com.ationet.androidterminal.hal.card_reader.rf_card

import android.util.Log
import com.github.devnied.emvnfccard.parser.EmvTemplate
import com.github.devnied.emvnfccard.parser.IProvider
import com.newland.sdk.module.rfcard.RFCardModule

class RfEmvCardReader(
    private val rf: RFCardModule
) {
    private val provider = object : IProvider {
        @OptIn(ExperimentalStdlibApi::class)
        override fun transceive(pCommand: ByteArray?): ByteArray {
            if (pCommand == null) {
                return byteArrayOf()
            }
            Log.v(TAG, "Sending=${pCommand.toHexString(HexFormat.UpperCase)}")

            val received = try {
                rf.transmit(pCommand, 60L)
            } catch (e: Throwable) {
                Log.e(TAG, "ERR W/Communication", e)
                return byteArrayOf()
            }

            if(received == null) {
                Log.w(TAG, "NOTHING RECEIVED")
                return byteArrayOf()
            }

            Log.v(TAG, "GOT=${received.toHexString(HexFormat.UpperCase)}")

            return received
        }

        override fun getAt(): ByteArray {
            throw NotImplementedError("AT should be disabled")
        }
    }

    fun readCard(): String? {
        val parser : EmvTemplate = EmvTemplate.Builder()
            .setConfig(config)
            .setProvider(provider)
            .build()

        val card = try {
            parser.readEmvCard()
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to read EMV card")
            return null
        }

        return card?.cardNumber
    }

    companion object {
        private val TAG: String = RfEmvCardReader::class.java.simpleName

        private val config = EmvTemplate.Config()
            .setContactLess(true)
            .setReadTransactions(false)
            .setReadCplc(false)
            .setReadAt(false)
    }
}
