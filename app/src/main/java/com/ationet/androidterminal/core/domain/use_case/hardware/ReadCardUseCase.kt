package com.ationet.androidterminal.core.domain.use_case.hardware

import com.ationet.androidterminal.core.domain.hal.card_reader.CardReader
import com.ationet.androidterminal.core.domain.hal.card_reader.CardReaderEvent
import com.ationet.androidterminal.core.domain.hal.card_reader.HALCardReader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.minutes

@Singleton
class ReadCardUseCase @Inject constructor(
    private val cardReader: HALCardReader
) {
    suspend operator fun invoke(): Flow<CardReaderEvent> {
        return cardReader.startRead(
            readers = listOf(
                CardReader.RfCard,
                CardReader.SmartCard,
                CardReader.MagneticStripeCard,
            ), timeout = 1.minutes
        ).cancellable().catch {
                emit(CardReaderEvent.CardReadError(reader = null))
            }
    }
}