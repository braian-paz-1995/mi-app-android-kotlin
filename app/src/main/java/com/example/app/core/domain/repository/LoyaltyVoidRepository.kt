package com.ationet.androidterminal.core.domain.repository

import com.ationet.androidterminal.core.domain.model.loyalty_void_transaction.LoyaltyVoidTransaction

interface LoyaltyVoidRepository {
    suspend fun insertLoyaltyVoidTransaction(
        loyaltyVoidTransaction: LoyaltyVoidTransaction
    ): LoyaltyVoidTransaction
    suspend fun delete(id: Int): Boolean

    suspend fun getTransactionsByAuthorizationCode(oldAuthorizationCode: String): Boolean
}