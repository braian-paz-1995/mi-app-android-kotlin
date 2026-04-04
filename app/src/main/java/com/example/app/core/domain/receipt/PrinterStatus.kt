package com.ationet.androidterminal.core.domain.receipt

sealed interface PrinterStatus {
    data object Ok: PrinterStatus
    data object OutOfPaper: PrinterStatus
    data class Error(
        val errorCode: String,
    ): PrinterStatus
}