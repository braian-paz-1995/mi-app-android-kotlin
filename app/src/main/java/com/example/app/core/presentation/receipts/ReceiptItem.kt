package com.ationet.androidterminal.core.presentation.receipts

import kotlinx.datetime.LocalDateTime

data class ReceiptItem(
    val typeOfOperation: String,
    val localTransactionDateTime: LocalDateTime,
    val authCode: String,
    val vehicle: String? = null,
    val driver: String? = null,
    val amount: Double? = null,
    val quantity: Double? = null
)