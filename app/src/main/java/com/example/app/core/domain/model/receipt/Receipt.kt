package com.ationet.androidterminal.core.domain.model.receipt

import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class Receipt(
    val id: Int = 0,
    val copy: Boolean,
    val controllerOwner: Configuration.ControllerType,
    val header: ReceiptHeader,
    val footer: ReceiptFooter,
    val transactionLine: ReceiptTransactionType,
    val site: ReceiptSite,
    val printConfiguration: ReceiptPrintConfiguration,
    val transactionData: ReceiptTransactionData,
    val loyaltyTransactionData: LoyaltyReceiptTransactionData? = null,
    val createdDateTime: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    val batchId: Int,
    val pumpId: String? = null
)
