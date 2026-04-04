package com.ationet.androidterminal.core.data.local.room.entity.receipt

import androidx.room.ColumnInfo

data class ReceiptHeaderEntity(
    @ColumnInfo("title")
    val title: String,
    @ColumnInfo("subtitle")
    val subtitle: String,
)