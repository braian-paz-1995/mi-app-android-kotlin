package com.ationet.androidterminal.core.domain.repository

import com.ationet.androidterminal.core.domain.model.transaction.TransactionView

interface TransactionRepository {
    suspend fun getTransactionByAuthorizationCode(
        authorizationCode: String,
        batchId: Int,
    ): TransactionView?
}