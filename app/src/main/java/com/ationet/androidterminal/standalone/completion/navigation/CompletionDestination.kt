package com.ationet.androidterminal.standalone.completion.navigation

import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.presentation.util.DisplayType
import kotlinx.serialization.Serializable

@Serializable
sealed interface CompletionDestination {
    @Serializable
    data object Identification : CompletionDestination

    @Serializable
    data class TransactionToCompleteConfirmation(
        val identification: String
    ) : CompletionDestination

    @Serializable
    data class TransactionCompletionAmount(
        val preAuthorization: PreAuthorizationData,
    ) : CompletionDestination

    @Serializable
    data class TransactionAmountConfirmation(
        val preAuthorization: PreAuthorizationData,
        val requestedQuantity: Double,
        val requestedAmount: Double,
        val preAuthorizationType: DisplayType,
        val isCompanyPrice: Boolean
    ) : CompletionDestination

    @Serializable
    data class CompletionSummary(
        val preAuthorization: PreAuthorizationData,
        val requestedQuantity: Double,
        val requestedAmount: Double,
    ) : CompletionDestination

    @Serializable
    data object Print : CompletionDestination
}

@Serializable
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
    val language: Configuration.LanguageType
)

@Serializable
data class PreAuthorizationProductData(
    val name: String,
    val code: String,
    val unitPrice: Double,
)
