package com.ationet.androidterminal.standalone.balance_enquiry.presentation

import com.ationet.androidterminal.standalone.balance_enquiry.presentation.summary.SummaryData
import kotlinx.serialization.Serializable

sealed interface BalanceEnquiryDestination {
    @Serializable
    data object Identification : BalanceEnquiryDestination

    @Serializable
    data object ProductSelection : BalanceEnquiryDestination

    @Serializable
    data object TransactionProcess : BalanceEnquiryDestination

    @Serializable
    data class Summary(val summary: SummaryData) : BalanceEnquiryDestination

    @Serializable
    data object Print : BalanceEnquiryDestination
}