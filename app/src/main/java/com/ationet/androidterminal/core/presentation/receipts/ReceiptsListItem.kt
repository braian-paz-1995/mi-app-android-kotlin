package com.ationet.androidterminal.core.presentation.receipts

import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionTypeName
import kotlinx.datetime.LocalDateTime

sealed interface ReceiptsListItem {
    data class Item(
        val receiptId: Int,
        val typeOfOperation: ReceiptTransactionTypeName,
        val localTransactionDateTime: LocalDateTime,
        val authCode: String,
        val vehicle: String? = null,
        val driver: String? = null,
        val amount: Double? = null,
        val quantity: Double? = null,
        val unitOfMeasure: String,
        val currencySymbol: String,
        val responseCode: String? = null,
        val responseMessage: String? = null,
        val pump: String? = null,
        val product: String? = null,
        val date: String? = null,
        val copy: Boolean = false
    ): ReceiptsListItem

    data class Separator(val dateTime: LocalDateTime): ReceiptsListItem
}