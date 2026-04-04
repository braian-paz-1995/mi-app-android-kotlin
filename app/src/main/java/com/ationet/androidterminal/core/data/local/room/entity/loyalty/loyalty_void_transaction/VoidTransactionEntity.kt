package com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_void_transaction

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loyalty_void_transaction")
data class LoyaltyVoidTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "transaction_id") val transactionId: Int,
    @ColumnInfo(name = "authorization_code") val authorizationCode: String,
    @ColumnInfo(name = "old_Authorization_Code") val oldAuthorizationCode: String,
    @ColumnInfo(name = "transaction_sequence_number", defaultValue = "0") val transactionSequenceNumber: Int,
)