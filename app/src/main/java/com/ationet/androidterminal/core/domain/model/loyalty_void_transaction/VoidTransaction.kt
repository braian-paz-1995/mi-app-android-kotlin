package com.ationet.androidterminal.core.domain.model.loyalty_void_transaction

data class LoyaltyVoidTransaction(
    val id: Int = 0,
    val transactionId: Int,
    val authorizationCode: String,
    val transactionSequenceNumber: Int,
    val oldAuthorizationCode: String
)
