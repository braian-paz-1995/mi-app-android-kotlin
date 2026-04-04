package com.ationet.androidterminal.hal.card_reader.ic

import android.device.IccManager
import android.util.Log
import com.ationet.androidterminal.core.domain.hal.card_reader.CardReader
import com.ationet.androidterminal.core.domain.hal.card_reader.CardReaderEvent
import com.ationet.androidterminal.hal.card_reader.CardReaderDevice
import com.github.devnied.emvnfccard.parser.EmvTemplate
import com.github.devnied.emvnfccard.parser.IProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class IcReader @Inject constructor(
    private val iccManager: IccManager
) : CardReaderDevice {
    override suspend fun read(): Flow<CardReaderEvent> = flow {
        open()

        /* Wait for card detection */
        waitForCardInSlot()

        emit(CardReaderEvent.CardReadBegin(CardReader.SmartCard))

        val cardReadResult = runCatching {
            readChipData()
        }

        val cardData = cardReadResult.getOrNull()
        if(cardReadResult.isFailure || cardData.isNullOrBlank()) {
            emit(CardReaderEvent.CardReadError(CardReader.SmartCard))

            return@flow
        }

        emit(CardReaderEvent.CardWaitingToRemove(CardReader.SmartCard))

        waitRemoval()

        emit(
            CardReaderEvent.CardRead(
                reader = CardReader.SmartCard,
                card = cardData
            )
        )
    }.onCompletion {
        close()
    }

    private suspend fun open(): Boolean {
        for (voltage in ICC_Voltages) {
            val voltageString = getVoltageString(voltage)
            Log.d(TAG, "Using voltage val=$voltageString")

            val openResult = withContext(Dispatchers.IO) {
                ensureActive()
                iccManager.open(SLOT_IC, TYPE_IC, voltage)
            }

            if (openResult == 0) {
                return true
            }
        }

        return false
    }

    private suspend fun close() {
        val deactivationSuccess = withContext(Dispatchers.IO) {
            ensureActive()
            iccManager.deactivate() >= 0
        }

        Log.d(TAG, "Card deactivated. Success: $deactivationSuccess")

        val closeSuccess = withContext(Dispatchers.IO) {
            iccManager.close() >= 0
        }

        Log.d(TAG, "Smart card reader closed. Success: $closeSuccess")
    }

    private suspend fun readChipData(): String {
        // Try reset
        val cardReset = resetCardInSlot()
        if (!cardReset) {
            throw IllegalStateException("Failed to reset card in slot")
        }

        return readData()
    }

    private suspend fun waitForCardInSlot() {
        do {
            val cardDetected = tryDetectCard()
            if (cardDetected) {
                break
            }

            delay(500.milliseconds)
        } while (true)
    }

    private suspend fun waitRemoval() {
        while (tryDetectCard()) {
            delay(250.milliseconds)
        }
    }

    private suspend fun tryDetectCard(): Boolean {
        return withContext(Dispatchers.IO) {
            /* 0 = Detected || Otherwise not detected */
            iccManager.detect() == 0
        }
    }

    private suspend fun resetCardInSlot(): Boolean {
        return withContext(Dispatchers.IO) {
            val buf = ByteArray(64)
            ensureActive()
            iccManager.activate(buf) >= 0
        }
    }

    private fun getVoltageString(voltage: Byte): String {
        return when (voltage) {
            VOLT_1_8 -> "1.8v"
            VOLT_3 -> "3v"
            VOLT_5 -> "5v"
            else -> "??v"
        }
    }

    private suspend fun readData(): String {
        // Read ATIO Directory File
        if (canSelectAtioDirectory()) {
            Log.d(TAG, "GOT Atio DF tag")
            return readAtioDF()
        }

        /* Read EMV card */
        val emvPan = readEmvCard()
        if (emvPan == null) {
            Log.w(TAG, "Failed to read Smart Card (IC)")
            return ""
        }

        Log.d(TAG, "GOT EMV tag")

        return emvPan
    }

    private suspend fun canSelectAtioDirectory(): Boolean {
        val selectResult = withContext(Dispatchers.IO) {
            ensureActive()
            transmit(SelectATIODF)
        }

        return selectResult.isValidResponse()
    }

    private suspend fun readAtioDF(): String {
        val fileReadResult = withContext(Dispatchers.IO) {
            ensureActive()
            transmit(ReadATIODF)
        }

        if (!fileReadResult.isValidResponse()) {
            Log.w(TAG, "Invalid response")
            return ""
        }

        if (fileReadResult.size < 4) {
            Log.w(TAG, "Invalid response size")
            return ""
        }

        return fileReadResult
            .sliceArray(0..3)
            .toUnsignedLong()
            .toString()
            .toByteArray()
            .decodeToString()
    }


    private suspend fun readEmvCard(): String? {
        val provider = object : IProvider {
            override fun transceive(pCommand: ByteArray?): ByteArray {
                return transmit(pCommand)
            }

            override fun getAt(): ByteArray {
                throw NotImplementedError("This method should not be called")
            }
        }

        val parser = EmvTemplate.Builder()
            .setConfig(config)
            .setProvider(provider)
            .build()

        return withContext(Dispatchers.IO) {
            ensureActive()
            parser.readEmvCard()?.cardNumber
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun transmit(pCommand: ByteArray?): ByteArray {
        if (pCommand == null) {
            return byteArrayOf()
        }

        val buf = ByteArray(258)
        val sw = ByteArray(2)

        Log.d(TAG, "Sending '${pCommand.toHexString(format = BufferFormatter)}'")

        val responseSize = iccManager.apduTransmit(pCommand, pCommand.size, buf, sw)
        if (responseSize < 0) {
            Log.w(TAG, "NOTHING RECEIVED")
            return byteArrayOf()
        }

        val response = buf.copyOfRange(0, responseSize)
        Log.d(TAG, "GOT '${response.toHexString(format = BufferFormatter)}'")

        return response
    }

    companion object {
        private const val TAG: String = "ICReader"

        private const val SLOT_IC: Byte = 0x0
        private const val TYPE_IC: Byte = 0x1
        private const val VOLT_1_8: Byte = 0x3
        private const val VOLT_3: Byte = 0x1
        private const val VOLT_5: Byte = 0x2

        private val ICC_Voltages = listOf(VOLT_1_8, VOLT_3, VOLT_5)

        private val config = EmvTemplate.Config()
            .setContactLess(false)
            .setReadAllAids(false)
            .setReadTransactions(false)
            .setReadAt(false)

        @OptIn(ExperimentalStdlibApi::class)
        private val BufferFormatter: HexFormat = HexFormat {
            upperCase = true
            bytes {
                byteSeparator = " "
            }
        }

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

        private fun ByteArray.toUnsignedLong(): ULong {
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