package com.ationet.androidterminal.standalone.sale.presentation

import kotlinx.serialization.Serializable

sealed interface SaleDestination {
    @Serializable
    data object Identification : SaleDestination

    @Serializable
    data object ProductSelection : SaleDestination
    @Serializable
    data object OperationType : SaleDestination
    @Serializable
    data object SKUProductSelection : SaleDestination

    @Serializable
    data object TransactionAmount : SaleDestination
    @Serializable
    data object SKUTransactionAmount : SaleDestination

    @Serializable
    data object Summary : SaleDestination

    @Serializable
    data object TransactionConfirmation : SaleDestination

    @Serializable
    data object TransactionProcess : SaleDestination

    @Serializable
    data object Print : SaleDestination
}