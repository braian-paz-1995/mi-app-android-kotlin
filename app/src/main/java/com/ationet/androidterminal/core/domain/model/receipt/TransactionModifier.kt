package com.ationet.androidterminal.core.domain.model.receipt

data class TransactionModifier(
    val id: Int = 0,
    val type: ReceiptModifierType,
    val modifierClass: ReceiptModifierClass,
    val value: Double,
    val total: Double,
    val base: Double,
    val receiptId: Int = 0,
)