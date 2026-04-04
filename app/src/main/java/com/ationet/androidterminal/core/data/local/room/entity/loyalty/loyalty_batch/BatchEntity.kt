package com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_batch

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "loyalty_batch")
data class LoyaltyBatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "transaction_date_time") val transactionDateTime: LocalDateTime,
    @ColumnInfo(name = "state") val state: State,
) {
    enum class State {
        OPEN,
        CLOSED
    }
}