package com.ationet.androidterminal.core.data.local.room.entity.receipt

import androidx.room.ColumnInfo

data class ReceiptFooterEntity(
    @ColumnInfo("footer")
    val footer: String,
    @ColumnInfo("bottom_name")
    val bottomNote: String,
)