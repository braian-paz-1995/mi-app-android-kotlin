package com.ationet.androidterminal.core.data.local.room.entity.receipt

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class ReceiptTransactionDataEntity(
    @ColumnInfo("response_code")
    val responseCode: String?,
    @ColumnInfo("response_text")
    val responseText: String,
    @ColumnInfo("terminal_id")
    val terminalId: String,
    @ColumnInfo("authorization_code")
    val authorizationCode: String?,
    @ColumnInfo("invoice")
    val invoice: String?,
    @ColumnInfo("tsn")
    val transactionSequenceNumber: String?,
    @ColumnInfo("primary_track")
    val primaryTrack: String,
    @ColumnInfo("secondary_track")
    val secondaryTrack: String?,
    @ColumnInfo("currency_symbol")
    val currencySymbol: String,
    @ColumnInfo("unit_of_measure")
    val unitOfMeasure: String,
    @Embedded
    val receiptData: ReceiptDataEntity,
    @Embedded(prefix = "product_")
    val product: ReceiptProductEntity,
    @ColumnInfo("transaction_amount", defaultValue = 0.0.toString())
    val transactionAmount: Double
)