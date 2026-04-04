package com.ationet.androidterminal.core.data.local.room.entity.receipt

import androidx.room.ColumnInfo
import kotlinx.datetime.LocalDateTime

data class ReceiptTransactionTypeEntity(
    @ColumnInfo("date_time")
    val dateTime: LocalDateTime,
    @ColumnInfo("name")
    val name: String,
)