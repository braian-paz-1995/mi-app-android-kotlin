package com.ationet.androidterminal.hal.card_reader.msr

import android.device.MagManager
import android.util.Log
import com.ationet.androidterminal.core.domain.hal.card_reader.CardReader
import com.ationet.androidterminal.core.domain.hal.card_reader.CardReaderEvent
import com.ationet.androidterminal.hal.card_reader.CardReaderDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class MagneticCardReader @Inject constructor(
    private val magManager: MagManager,
) : CardReaderDevice {

    override suspend fun read(): Flow<CardReaderEvent> = flow {
        /* Open reader */
        open()

        while (checkCardSwiped()) {
            delay(100.milliseconds)
            continue
        }

        Log.d(TAG, "Card swipe detected")

        emit(CardReaderEvent.CardReadBegin(CardReader.MagneticStripeCard))

        val magneticCardData = readMagneticCard()

        /** Note: It's not necessary to emit CardReadEvent.WaitingToRemove,
         * because card was already removed */

        emit(
            CardReaderEvent.CardRead(
                reader = CardReader.MagneticStripeCard,
                card = magneticCardData
            )
        )
    }.onCompletion {
        close()
    }


    private suspend fun open(): Boolean {
        val openSuccess = withContext(Dispatchers.IO) {
            magManager.open() >= 0
        }

        Log.d(TAG, "Open magnetic card reader slot: Success $openSuccess")
        return openSuccess
    }

    private suspend fun close() {
        val closeSuccess = withContext(Dispatchers.IO) {
            magManager.close() >= 0
        }

        Log.d(TAG, "Closed magnetic card reader slot. Success: $closeSuccess")
    }

    private suspend fun readMagneticCard(): String {
        val cardData = ByteArray(1024)
        /* Card read. Try to get stripe info */
        val dataLength = withContext(Dispatchers.IO) {
            magManager.getAllStripInfo(cardData)
        }

        Log.d(TAG, "Read $dataLength bytes from magnetic card")

        if (dataLength <= 0) {
            Log.w(TAG, "No data read from card")
            return ""
        }

        val cardTracks = parseCardTracks(cardData)

        Log.d(TAG, "Selected track 2: ${cardTracks[1]}")

        return cardTracks[1].orEmpty()
    }

    private suspend fun checkCardSwiped(): Boolean {
        return withContext(Dispatchers.IO) {
            magManager.checkCard() != 0
        }
    }

    private fun parseCardTracks(cardData: ByteArray): List<String?> {
        /**
         * Format:
         * 01 <LEN:1> <DATA:N> 02 <LEN:1> <DATA:M> 03 <LEN:1> <DATA>
         * 01 03 0A 0B 0C 02 02 0D 0E 03 01 0F
         *
         * Track 1 mark is at index 0
         * Track 1 length is at index 1
         * Track 1 data is at index 2
         *
         * Track 2 mark is at index 2 + N
         * Track 2 length is at index 3 + N
         * Track 2 data is at index 4 + N
         *
         * Track 3 mark is at index 4 + N + M
         * Track 3 length is at index 5 + N + M
         * Track 3 data is at index 6 + N + M
         * */

        var dataStartOffset = TRACK_1_DATA_OFFSET
        val track1Length = cardData[TRACK_1_LEN_IX].toInt()
        val track1 = cardData.decodeToString(
            startIndex = dataStartOffset,
            endIndex = dataStartOffset + track1Length
        )

        val track2Length = cardData[TRACK_2_LEN_IX + track1Length].toInt()
        dataStartOffset = TRACK_2_DATA_OFFSET + track1Length
        val track2 = cardData.decodeToString(
            startIndex = dataStartOffset,
            endIndex = dataStartOffset + track2Length
        )

        val track3Length = cardData[TRACK_3_LEN_IX + track1Length + track2Length].toInt()
        dataStartOffset = TRACK_3_DATA_OFFSET + track1Length + track2Length
        val track3 = cardData.decodeToString(
            startIndex = dataStartOffset,
            endIndex = dataStartOffset + track3Length
        )

        return listOf(track1, track2, track3)
    }

    companion object {
        private const val TAG: String = "MagneticCardReader"

        private const val TRACK_1_LEN_IX: Int = 1
        private const val TRACK_1_DATA_OFFSET: Int = TRACK_1_LEN_IX + 1

        private const val TRACK_2_LEN_IX: Int = 3
        private const val TRACK_2_DATA_OFFSET: Int = TRACK_2_LEN_IX + 1

        private const val TRACK_3_LEN_IX: Int = 5
        private const val TRACK_3_DATA_OFFSET: Int = TRACK_3_LEN_IX + 1
    }
}