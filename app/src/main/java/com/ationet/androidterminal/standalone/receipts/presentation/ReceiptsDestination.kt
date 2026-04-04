package com.ationet.androidterminal.standalone.receipts.presentation

import kotlinx.serialization.Serializable

@Serializable
sealed interface ReceiptsDestination {
    @Serializable
    data object ReceiptsList: ReceiptsDestination
    @Serializable
    data class PrintingCopy(
        val receiptId: Int
    ): ReceiptsDestination
}