package com.ationet.androidterminal.standalone.void_transaction.presentation

import kotlinx.serialization.Serializable

sealed interface VoidTransactionDestination {
    @Serializable
    data object SupervisorPassword : VoidTransactionDestination

    @Serializable
    data object AuthorizationCode : VoidTransactionDestination

    @Serializable
    data class Confirmation(val authorizationCode: String, val typeTransaction: String) : VoidTransactionDestination

    @Serializable
    data class Summary(val newAuthorizationCode: String) : VoidTransactionDestination

    @Serializable
    data object Print : VoidTransactionDestination
}