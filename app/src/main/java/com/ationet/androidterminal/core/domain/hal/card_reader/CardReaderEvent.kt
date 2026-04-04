package com.ationet.androidterminal.core.domain.hal.card_reader

sealed interface CardReaderEvent {
    /**
     * Launched when card read process starts on the card, to retrieve information
     * */
    data class CardReadBegin(
        val reader: CardReader
    ) : CardReaderEvent

    /**
     * Launched when card is waiting to be removed
     * */
    data class CardWaitingToRemove(
        val reader: CardReader
    ) : CardReaderEvent

    /**
     * Launched when card failed to read.
     * */
    data class CardReadError(
        val reader: CardReader?
    ) : CardReaderEvent

    /**
     * Launched when read timeout is reached.
     * */
    data object ReaderTimeout : CardReaderEvent

    /**
     * Launched when card is read and result got.
     * */
    data class CardRead(
        val reader : CardReader,
        val card: String,
    ) : CardReaderEvent
}