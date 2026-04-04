package com.ationet.androidterminal.core.domain.model.batch

data class BatchTransaction(
    val id: Int,
    val amount: Double,
    val quantity: Double,
    val unitPrice: Double,
    val paymentMethod: String?,
)
data class LoyaltyBatchTransaction(
    val id: Int,
    val amount: Double,
    val quantity: Double,
    val unitPrice: Double,
    val paymentMethod: String?,
)