package com.ationet.androidterminal.core.domain.hal.card_reader

sealed interface CardReaderResult {
    data object Cancelled : CardReaderResult
    data object Timeout : CardReaderResult
    data class Error(
        val message: String,
        val exception: Throwable? = null,
    ) : CardReaderResult

    data class Identifier(
        val track1: String? = null,
        val track2: String? = null,
        val track3: String? = null,
        val source: CardReader,
    ) : CardReaderResult
}