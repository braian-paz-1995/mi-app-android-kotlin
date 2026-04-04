package com.ationet.androidterminal.core.domain.model.receipt

import kotlinx.datetime.LocalDateTime

data class ReceiptTransactionType(
    val dateTime: LocalDateTime,
    val name: ReceiptTransactionTypeName,
)
