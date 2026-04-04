package com.ationet.androidterminal.standalone.clear_pending.domain.model

import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.model.receipt.Receipt
import com.ationet.androidterminal.core.presentation.util.DisplayType
import kotlinx.datetime.LocalDateTime

data class ClearPendingOperationState(
    val identifier: Identifier = Identifier(),
    val preAuthorizationData: PreAuthorizationData? = null,
    val receipt: Receipt? = null,
)

data class Identifier(
    val primaryTrack: String = "",
)

data class PreAuthorizationData(
    val id: Int,
    val authorizationCode: String,
    val amount: Double?,
    val quantity: Double?,
    val maxAmount: Double?,
    val maxQuantity: Double?,
    val identification: String,
    val preAuthorizationType: DisplayType,
    val product: PreAuthorizationProductData,
    val currencySymbol: String,
    val quantityUnit: String,
    val language: Configuration.LanguageType,
    val date: LocalDateTime
)

data class PreAuthorizationProductData(
    val name: String,
    val code: String,
    val unitPrice: Double,
)