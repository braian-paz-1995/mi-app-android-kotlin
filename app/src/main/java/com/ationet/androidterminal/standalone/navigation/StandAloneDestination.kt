package com.ationet.androidterminal.standalone.navigation

import kotlinx.serialization.Serializable

sealed interface StandAloneDestination {
    @Serializable
    data object Home : StandAloneDestination

    @Serializable
    data object PreAuthorization : StandAloneDestination

    @Serializable
    data object Completion : StandAloneDestination

    @Serializable
    data object Sale : StandAloneDestination

    @Serializable
    data object Receipts : StandAloneDestination

    @Serializable
    data object BalanceEnquiry : StandAloneDestination

    @Serializable
    data object ClearPendingTransactions : StandAloneDestination

    @Serializable
    data object VoidTransaction : StandAloneDestination

    @Serializable
    data object ChangePIN : StandAloneDestination

    @Serializable
    data object BatchClose : StandAloneDestination

    @Serializable
    data object Task : StandAloneDestination

    @Serializable
    data object Identification : StandAloneDestination {
        @Serializable
        data object Scan : StandAloneDestination

        @Serializable
        data object Manual : StandAloneDestination
    }

    @Serializable
    data object SelectFuel : StandAloneDestination

    @Serializable
    data object FuelingOption : StandAloneDestination

    @Serializable
    data object FillUp : StandAloneDestination

    @Serializable
    data object Confirmation : StandAloneDestination

    @Serializable
    data object Prompt : StandAloneDestination {
        @Serializable
        data object PIN : StandAloneDestination

        @Serializable
        data object Odometer : StandAloneDestination

        @Serializable
        data object QuantityOrAmount : StandAloneDestination

        @Serializable
        data object SupervisorPassword : StandAloneDestination

        @Serializable
        data class Numeric(val type: String) : StandAloneDestination
    }

    @Serializable
    data object Summary : StandAloneDestination

    @Serializable
    data class TransactionToComplete(val transactionType: String) : StandAloneDestination

    @Serializable
    data object ProcessTransaction : StandAloneDestination {
        @Serializable
        data class Ok(val authorizationCode: String) : StandAloneDestination

        @Serializable
        data class Error(val message: String) : StandAloneDestination
    }

    @Serializable
    data object Printing : StandAloneDestination

}