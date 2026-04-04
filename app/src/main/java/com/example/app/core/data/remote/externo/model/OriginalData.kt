package com.ationet.androidterminal.core.data.remote.ationet.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OriginalData(
    @SerialName("AuthorizationCode")
    val authorizationCode: String? = null,
    @SerialName("LocalTransactionDate")
    val localTransactionDate: String? = null,
    @SerialName("LocalTransactionTime")
    val localTransactionTime: String? = null,
    @SerialName("TransactionCode")
    val transactionCode: String? = null,
    @SerialName("TransactionSequenceNumber")
    val transactionSequenceNumber: String? = null
)