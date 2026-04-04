package com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_accumulation

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(
    tableName = "loyaltyAccumulation",
    indices = [
        Index(value = ["authorization_code", "batch_id", "transaction_date_time"]),
        Index(value = ["controller_type"])
    ]
)
data class LoyaltyAccumulationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "authorization_code")
    val authorizationCode: String,
    @ColumnInfo(name = "transaction_date_time")
    val transactionDateTime: LocalDateTime,
    @ColumnInfo(name = "transaction_sequence_number")
    val transactionSequenceNumber: Long,
    @Embedded(prefix = "transaction_") val transactionData: TransactionData,
    @ColumnInfo(name = "batch_id") val batchId: Int,
    @ColumnInfo(name = "controller_type", defaultValue = "STAND_ALONE") val controllerType: String
)
