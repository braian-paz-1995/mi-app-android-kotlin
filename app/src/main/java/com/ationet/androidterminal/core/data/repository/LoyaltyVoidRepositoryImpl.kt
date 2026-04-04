package com.ationet.androidterminal.core.data.repository

import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_void_transaction.LoyaltyVoidTransactionEntity
import com.ationet.androidterminal.core.data.local.room.loyalty.LoyaltyVoidDao
import com.ationet.androidterminal.core.domain.model.loyalty_void_transaction.LoyaltyVoidTransaction
import com.ationet.androidterminal.core.domain.repository.LoyaltyVoidRepository
import javax.inject.Inject

class LoyaltyVoidRepositoryImpl @Inject constructor(
    private val loyaltyVoidDao: LoyaltyVoidDao,
) : LoyaltyVoidRepository {
    override suspend fun insertLoyaltyVoidTransaction(loyaltyVoidTransaction: LoyaltyVoidTransaction): LoyaltyVoidTransaction {
        val loyaltyVoidTransactionEntity = with(loyaltyVoidTransaction) {
            LoyaltyVoidTransactionEntity(
                transactionId = transactionId,
                authorizationCode = authorizationCode,
                transactionSequenceNumber = transactionSequenceNumber,
                oldAuthorizationCode = oldAuthorizationCode
            )
        }
        val rowId = loyaltyVoidDao.insertLoyaltyVoidTransaction(loyaltyVoidTransactionEntity)
        return loyaltyVoidTransaction.copy(id = rowId.toInt())
    }

    override suspend fun delete(id: Int): Boolean {
        return loyaltyVoidDao.delete(id) > 0
    }
    override suspend fun getTransactionsByAuthorizationCode(oldAuthorizationCode: String): Boolean {
        return loyaltyVoidDao.getTransactionsByAuthorizationCode(oldAuthorizationCode)
    }
}