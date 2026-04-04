package com.ationet.androidterminal.core.domain.model.receipt

import kotlinx.datetime.LocalDateTime

data class ReceiptView(
    val receiptId: Int,
    val transactionName: ReceiptTransactionTypeName,
    val transactionDateTime: LocalDateTime,
    val authorizationCode: String,
    val vehicle: String?,
    val driver: String?,
    val amount: Double?,
    val quantity: Double?,
    val unitOfMeasure: String,
    val currencySymbol: String,
    val responseCode: String? = null,
    val responseMessage: String? = null,
    val pump: String? = null,
    val product: String? = null,
    val date: String? = null,
    val copy: Boolean = false
)
