package com.ationet.androidterminal.core.domain.model.batch

import kotlinx.datetime.LocalDateTime

data class Batch(
    val id: Int = 0,
    val transactionDateTime: LocalDateTime,
    val state: State
) {
    enum class State {
        OPEN,
        CLOSED
    }
}
data class LoyaltyBatch(
    val id: Int = 0,
    val transactionDateTime: LocalDateTime,
    val state: State
) {
    enum class State {
        OPEN,
        CLOSED
    }
}