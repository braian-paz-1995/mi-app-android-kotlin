package com.ationet.androidterminal.core.domain.hal.card_reader

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface HALCardReader {
    suspend fun startRead(readers: List<CardReader>, timeout: Duration) : Flow<CardReaderEvent>
}