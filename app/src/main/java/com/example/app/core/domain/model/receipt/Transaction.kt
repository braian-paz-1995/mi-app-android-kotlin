package com.ationet.androidterminal.core.domain.model.receipt

data class Transaction(
    val productUnitPrice: Double,
    val productUnitPriceBase: Double,
    val productAmount: Double,
    val transactionAmount: Double,
    val modifiers: List<TransactionModifier>
)