package com.ationet.androidterminal.core.data.local.room.entity.task.reverseCC

import androidx.room.ColumnInfo

data class ProductData(
    @ColumnInfo(name = "input_type") val inputType: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "unit_price") val unitPrice: Double,
    @ColumnInfo(name = "quantity") val quantity: Double,
    @ColumnInfo(name = "amount") val amount: Double,
)