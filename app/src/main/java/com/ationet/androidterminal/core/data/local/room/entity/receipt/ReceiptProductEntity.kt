package com.ationet.androidterminal.core.data.local.room.entity.receipt

import androidx.room.ColumnInfo


data class ReceiptProductEntity(
    @ColumnInfo("input_type")
    val inputType: String,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("code")
    val code: String,
    @ColumnInfo("unit_price")
    val unitPrice: Double?,
    @ColumnInfo("quantity")
    val quantity: Double?,
    @ColumnInfo("amount")
    val amount: Double?,
)

