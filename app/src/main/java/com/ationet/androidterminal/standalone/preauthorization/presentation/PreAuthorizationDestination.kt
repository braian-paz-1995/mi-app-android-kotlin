package com.ationet.androidterminal.standalone.preauthorization.presentation

import kotlinx.serialization.Serializable

sealed interface PreAuthorizationDestination {
    @Serializable
    data object Identification : PreAuthorizationDestination

    @Serializable
    data object PendingTransaction : PreAuthorizationDestination

    @Serializable
    data object ProductSelection : PreAuthorizationDestination

    @Serializable
    data object ProductOption : PreAuthorizationDestination

    @Serializable
    data object TransactionAmount : PreAuthorizationDestination

    @Serializable
    data object TransactionConfirmation : PreAuthorizationDestination

    @Serializable
    data object TransactionProcess : PreAuthorizationDestination

    @Serializable
    data object PreAuthorizationSummary : PreAuthorizationDestination

    @Serializable
    data object Print : PreAuthorizationDestination
}