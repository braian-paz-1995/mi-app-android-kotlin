package com.ationet.androidterminal.core.data.local.room.entity.fleet.pre_authorization

import androidx.room.ColumnInfo
import kotlinx.datetime.LocalDateTime

data class AuthorizationData(
    @ColumnInfo(name = "authorization_code") val authorizationCode: String,
    @ColumnInfo(name = "transaction_sequence_number") val transactionSequenceNumber: Long,
    @ColumnInfo(name = "invoice") val invoice: String,
    @ColumnInfo(name = "local_datetime") val localDateTime: LocalDateTime,
    @ColumnInfo(name = "authorized_amount") val amount: Double?,
    @ColumnInfo(name = "authorized_quantity") val quantity: Double?,
    @ColumnInfo(name = "authorized_price") val unitPrice: Double?,
)