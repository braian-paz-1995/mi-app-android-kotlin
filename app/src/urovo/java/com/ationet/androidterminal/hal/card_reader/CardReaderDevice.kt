package com.ationet.androidterminal.hal.card_reader

import com.ationet.androidterminal.core.domain.hal.card_reader.CardReaderEvent
import kotlinx.coroutines.flow.Flow

interface CardReaderDevice {
    suspend fun read(): Flow<CardReaderEvent>
}