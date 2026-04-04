package com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_batch

import androidx.room.ColumnInfo

data class LoyaltyBatchTransactionData(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "transaction_product_amount") val amount: Double,
    @ColumnInfo(name = "transaction_product_quantity") val quantity: Double,
    @ColumnInfo(name = "transaction_product_unit_price") val unitPrice: Double,
    @ColumnInfo(name = "transaction_payment_method") val paymentMethod: String?,
)
