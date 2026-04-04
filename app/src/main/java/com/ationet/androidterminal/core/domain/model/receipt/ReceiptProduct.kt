package com.ationet.androidterminal.core.domain.model.receipt


data class ReceiptProduct(
    val inputType: ReceiptProductInputType,
    val name: String,
    val code: String,
    val unitPrice: Double?,
    val quantity: Double?,
    val amount: Double?,
    val modifiers: List<TransactionModifier>
)

