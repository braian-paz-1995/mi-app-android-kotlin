package com.ationet.androidterminal.hal.card_reader

import com.ationet.androidterminal.core.domain.hal.card_reader.CardReader
import com.ationet.androidterminal.core.domain.hal.card_reader.CardReaderEvent
import com.ationet.androidterminal.core.domain.hal.card_reader.HALCardReader
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.time.Duration

class HALCardReaderImpl @Inject constructor() : HALCardReader {

    override suspend fun startRead(
        readers: List<CardReader>,
        timeout: Duration
    ): Flow<CardReaderEvent> {

        return flow {
            // Simula que empezó la lectura
            emit(CardReaderEvent.CardReadBegin(CardReader.RfCard))

            delay(500)

            // Simula esperando remover tarjeta
            emit(CardReaderEvent.CardWaitingToRemove(CardReader.RfCard))

            delay(500)

            // Devuelve un valor fake (para que nunca falle)
            emit(
                CardReaderEvent.CardRead(
                    reader = CardReader.RfCard,
                    card = "1234567890FAKE"
                )
            )
        }
    }
}