package com.ationet.androidterminal.core.domain.model.transaction


import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import kotlinx.datetime.LocalDateTime

data class TransactionView(
    val id: Int,
    val authorizationCode: String,
    val type: TransactionType,
    val transactionSequenceNumber: Long,
    val dateTime: LocalDateTime,
    val transactionData: TransactionData,
    val batchId: Int,
    val controllerType: Configuration.ControllerType
)