package com.ationet.androidterminal.core.domain.model.receipt

data class ReceiptPrintConfiguration(
    val printDriver: Boolean,
    val printVehicle: Boolean,
    val printCompanyName: Boolean,
    val printPrimaryTrack: Boolean,
    val printSecondaryTrack: Boolean,
    val printTransactionDetails: Boolean,
    val printInvoiceNumber: Boolean,
    val printProductInColumns: Boolean
)