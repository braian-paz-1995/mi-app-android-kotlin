package com.ationet.androidterminal.core.domain.receipt

import com.ationet.androidterminal.core.domain.model.receipt.Receipt

interface ReceiptPrinter {
    suspend fun printReceipt(receipt: Receipt) : PrinterStatus
}

