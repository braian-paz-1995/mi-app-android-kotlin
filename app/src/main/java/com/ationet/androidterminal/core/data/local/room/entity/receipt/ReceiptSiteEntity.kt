package com.ationet.androidterminal.core.data.local.room.entity.receipt

import androidx.room.ColumnInfo

data class ReceiptSiteEntity(
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("code")
    val code: String,
    @ColumnInfo("address")
    val address: String,
    @ColumnInfo("cuit")
    val cuit: String,
)