package com.ationet.androidterminal.core.domain.model.receipt

import com.ationet.androidterminal.core.data.remote.ationet.model.LoyaltyReceiptData

data class ReceiptTransactionData(
    val responseCode: String?,
    val responseText: String,
    val terminalId: String,
    val primaryTrack: String,
    val secondaryTrack: String?,
    val authorizationCode: String?,
    val invoice: String?,
    val transactionSequenceNumber: String?,
    val receiptData: ReceiptData,
    val product: ReceiptProduct,
    val unitOfMeasure: String,
    val currencySymbol: String,
    val salesCounter: Int? = null,
    val salesTotal: Double? = null,
    val voidedCounter: Int? = null,
    val voidedTotal: Double? = null,
    val rechargeCCCounter: Int? = null,
    val rechargeCCTotal: Double? = null,
    val reverseCCCounter: Int? = null,
    val reverseCCTotal: Double? = null,
    val batchNumber: String? = null,
    val transactionAmount: Double? = null,
    val balance: Double? = null,
    val amount: Double? = null,
    val availableBalance: Double? = null,
    val batchClose: BatchCloseData = BatchCloseData(),
    val loyaltyBatchClose: LoyaltyBatchCloseData = LoyaltyBatchCloseData(),
)

data class LoyaltyReceiptTransactionData
    (
    val loyaltyReceiptData: LoyaltyReceiptData?
)