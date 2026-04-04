package com.ationet.androidterminal.core.data.local.room.entity.task.batch

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "batch")
data class BatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "transaction_date_time") val transactionDateTime: LocalDateTime,
    @ColumnInfo(name = "state") val state: State,
) {
    enum class State {
        OPEN,
        CLOSED
    }
}