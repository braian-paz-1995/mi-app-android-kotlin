package com.ationet.androidterminal.core.data.local.room.entity.fleet.pre_authorization

import androidx.room.ColumnInfo
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class OriginalData(
    @ColumnInfo(name = "od_transaction_sequence_number") val transactionSequenceNumber: Long,
    @ColumnInfo(name = "od_authorization_code") val authorizationCode: String,
    @ColumnInfo(name = "od_transaction_code") val transactionCode: String,
    @ColumnInfo(name = "od_local_transaction_date") val localTransactionDate: LocalDate? = null,
    @ColumnInfo(name = "od_local_transaction_time") val localTransactionTime: LocalTime? = null,
)