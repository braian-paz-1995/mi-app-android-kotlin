package com.ationet.androidterminal.core.domain.model.void_transaction

data class VoidTransaction(
    val id: Int = 0,
    val transactionId: Int,
    val authorizationCode: String,
    val transactionSequenceNumber: Int
)
