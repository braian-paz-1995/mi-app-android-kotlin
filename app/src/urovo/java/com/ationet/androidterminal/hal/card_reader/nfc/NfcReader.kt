package com.ationet.androidterminal.hal.card_reader.nfc

import android.util.Log
import com.ationet.androidterminal.core.domain.hal.card_reader.CardReader
import com.ationet.androidterminal.core.domain.hal.card_reader.CardReaderEvent
import com.ationet.androidterminal.hal.card_reader.CardReaderDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class NfcReader @Inject constructor(
    private val nfcEventReceiver: NfcEventReceiver
) : CardReaderDevice {
    override suspend fun read(): Flow<CardReaderEvent> = flow {
        nfcEventReceiver.sendCommand(NfcCommand.Read)

        var tagContent = ""
        nfcEventReceiver.events.collect { event ->
            Log.v(TAG, "Received event from app's NFC reader: $event")
            when (event) {
                NfcEvent.TagPresented -> {
                    emit(CardReaderEvent.CardReadBegin(CardReader.RfCard))
                }

                is NfcEvent.TagRead -> {
                    tagContent = event.tag.content
                    emit(CardReaderEvent.CardWaitingToRemove(CardReader.RfCard))
                }

                NfcEvent.TagRemoved -> {
                    val cardReaderEvent = CardReaderEvent.CardRead(
                        reader = CardReader.RfCard,
                        card = tagContent
                    )

                    emit(cardReaderEvent)
                }

                is NfcEvent.Error -> {
                    emit(CardReaderEvent.CardReadError(CardReader.RfCard))
                }
            }
        }
    }.onCompletion {
        nfcEventReceiver.sendCommand(NfcCommand.Cancel)
    }.onEach {
        Log.v(TAG, "Emitting NFC event: $it")
    }

    private companion object {
        private const val TAG: String = "NfcReader"
    }
}