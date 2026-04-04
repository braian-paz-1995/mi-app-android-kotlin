package com.ationet.androidterminal.core.data.repository

import com.ationet.androidterminal.core.data.local.room.entity.fleet.void_transaction.VoidTransactionEntity
import com.ationet.androidterminal.core.data.local.room.fleet.VoidDao
import com.ationet.androidterminal.core.domain.model.void_transaction.VoidTransaction
import com.ationet.androidterminal.core.domain.repository.VoidRepository
import javax.inject.Inject

class VoidRepositoryImpl @Inject constructor(
    private val voidDao: VoidDao,
) : VoidRepository {
    override suspend fun insertVoidTransaction(voidTransaction: VoidTransaction): VoidTransaction {
        val voidTransactionEntity = with(voidTransaction) {
            VoidTransactionEntity(
                transactionId = transactionId,
                authorizationCode = authorizationCode,
                transactionSequenceNumber = transactionSequenceNumber
            )
        }
        val rowId = voidDao.insertVoidTransaction(voidTransactionEntity)
        return voidTransaction.copy(id = rowId.toInt())
    }

    override suspend fun delete(id: Int): Boolean {
        return voidDao.delete(id) > 0
    }

    override suspend fun getTransactionsByAuthorizationCode(authorizationCode: String): Boolean {
        return voidDao.getTransactionsByAuthorizationCode(authorizationCode)
    }
}