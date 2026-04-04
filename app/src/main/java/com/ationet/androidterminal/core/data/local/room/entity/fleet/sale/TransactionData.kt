package com.ationet.androidterminal.core.data.local.room.entity.fleet.sale

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class TransactionData(
    @ColumnInfo(name = "primary_track") val primaryTrack: String,
    @Embedded("product_") val product: ProductData
)