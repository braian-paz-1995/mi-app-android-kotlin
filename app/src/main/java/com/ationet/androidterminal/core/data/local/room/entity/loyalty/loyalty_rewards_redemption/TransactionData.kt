package com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_rewards_redemption

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class TransactionData(
    @ColumnInfo(name = "primary_track") val withdrawalCode: String,
    @ColumnInfo(name = "payment_method") val paymentMethod: String,
    @Embedded("product_") val product: ProductData
)