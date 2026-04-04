package com.ationet.androidterminal.standalone.completion.domain.model

import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import kotlinx.datetime.LocalDateTime

data class Completion(
    val id: Int = 0,
    val authorizationCode: String,
    val transactionDateTime: LocalDateTime,
    val transactionSequenceNumber: Long,
    val transactionData: TransactionData,
    val batchId: Int,
    val controllerType: Configuration.ControllerType
)

data class TransactionData(
    val primaryTrack: String,
    val product: ProductData
)

data class ProductData(
    val inputType: String,
    val name: String,
    val code: String,
    val unitPrice: Double,
    val quantity: Double,
    val amount: Double,
)