package com.ationet.androidterminal.hal.card_reader.ic_card

import android.util.Log
import com.github.devnied.emvnfccard.parser.EmvTemplate
import com.github.devnied.emvnfccard.parser.IProvider
import com.newland.sdk.module.iccard.ICCardModule
import com.newland.sdk.module.iccard.ICCardSlot
import com.newland.sdk.module.iccard.ICCardType
import kotlinx.coroutines.CancellationException

class IcCardReader(
    private val icCardModule: ICCardModule,
) {
    fun readCardAtioFile(): String? {
        Log.d(TAG, "Trying to read card with ATIO DF")
        try {
            for (type in ICCardType.entries) {
                if (powerOn(type) == null) {
                    continue
                }

                val selectResult = transmit(type, SelectATIODF)
                if (!selectResult.isValidResponse()) {
                    continue
                }

                val fileReadResult = transmit(type, ReadATIODF) ?: continue
                if (!fileReadResult.isValidResponse()) {
                    continue
                }

                powerOff(type)

                if (fileReadResult.size < 4) {
                    Log.w(TAG, "Invalid response size")
                    return null
                }

                return fileReadResult
                    .slice(0..3)
                    .toUnsignedLong()
                    .toString()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to read ATIO Directory")
        }

        return null
    }

    fun readEmvCard(): String? {
        try {
            for (type in ICCardType.entries) {
                val provider = object  : IProvider {
                    override fun transceive(pCommand: ByteArray?): ByteArray {
                        if (pCommand == null) {
                            return byteArrayOf()
                        }

                        return transmit(type, pCommand) ?: byteArrayOf()
                    }

                    override fun getAt(): ByteArray {
                        return powerOn(type) ?: byteArrayOf()
                    }

                    fun parse() : String? {
                        return parser.readEmvCard()?.cardNumber
                    }

                    private val config = EmvTemplate.Config()
                        .setContactLess(false)
                        .setReadTransactions(false)
                        .setReadCplc(false)

                    private val parser: EmvTemplate = EmvTemplate.Builder()
                        .setConfig(config)
                        .setProvider(this)
                        .build()
                }

                val cardNumber  = try {
                    provider.parse()
                } catch (e: Throwable) {
                    Log.e(TAG, "Failed to parse card", e)
                    continue
                } finally {
                    powerOff(type)
                }

                return cardNumber
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to read EMV data")
        }

        return null
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun powerOn(type: ICCardType): ByteArray? {
        // Try to power on
        val atr = try {
            icCardModule.powerOn(ICCardSlot.IC1, type)
        } catch (_: Throwable) {
            return null
        } ?: return null

        Log.d(
            TAG,
            "GOT Answer To Reset: '${atr.toHexString(HexFormat.UpperCase)}' - Type $type"
        )
        return atr
    }

    private fun powerOff(cardType: ICCardType) {
        try {
            icCardModule.powerOff(ICCardSlot.IC1, cardType)
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to power off IC card reader", e)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun transmit(cardType: ICCardType, buffer: ByteArray): ByteArray {
        Log.v(TAG, "Sending '${buffer.toHexString(HexFormat.UpperCase)}' - Type $cardType")
        val received = icCardModule.transmit(ICCardSlot.IC1, cardType, buffer, 1)
        if (received != null) {
            Log.d(
                TAG,
                "GOT response: '${received.toHexString(HexFormat.UpperCase)}' - Type $cardType"
            )
        } else {
            Log.w(TAG, "Response missing from card type $cardType")
        }

        return received
    }

    companion object {
        private const val TAG: String = "ICCardReader"
        private val SelectATIODF: ByteArray = byteArrayOf(
            0x00.toByte(), // CLA
            0xA4.toByte(), // INS - Select file
            0x01.toByte(), // P1 - Select by DF, EF or MF
            0x00.toByte(), // P2
            0x02.toByte(), // Lc - Command length (2 bytes)
            0x97.toByte(), // D0 - Atio DF
            0x00.toByte(), // D1 - Atio DF
        )

        private val ReadATIODF: ByteArray = byteArrayOf(
            0x00.toByte(),  // CLA
            0xB0.toByte(),  // INS - Read binary
            0x81.toByte(),  // P1 - read CARD file
            0x00.toByte(),  // P2 - Offset from the beginning
            0x04.toByte(),  // Le - Length of response
        )

        private fun ByteArray?.isValidResponse(): Boolean {
            if (this == null) {
                return false
            }

            if (size < 2) {
                return false
            }

            val sw1 = this[lastIndex - 1]
            val sw2 = this[lastIndex]
            return when {
                sw1 == 0x61.toByte() -> true
                sw1 == 0x90.toByte() && sw2 == 0x00.toByte() -> true
                else -> false
            }
        }

        private fun List<Byte>.toUnsignedLong(): ULong {
            if (this.size < 4) {
                throw IllegalStateException("Invalid length")
            }
            return (this[0].toULong() and 0xFFu) shl 24 or
                    (this[1].toULong() and 0xFFu) shl 16 or
                    (this[2].toULong() and 0xFFu) shl 8 or
                    (this[3].toULong() and 0xFFu)
        }
    }
}