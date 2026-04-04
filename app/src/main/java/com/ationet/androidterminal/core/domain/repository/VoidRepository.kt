package com.ationet.androidterminal.core.domain.repository

import com.ationet.androidterminal.core.domain.model.void_transaction.VoidTransaction

interface VoidRepository {
    suspend fun insertVoidTransaction(
        voidTransaction: VoidTransaction
    ): VoidTransaction
    suspend fun delete(id: Int): Boolean
    suspend fun getTransactionsByAuthorizationCode(AuthorizationCode: String): Boolean
}