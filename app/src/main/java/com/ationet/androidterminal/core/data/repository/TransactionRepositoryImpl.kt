package com.ationet.androidterminal.core.data.repository

import com.ationet.androidterminal.core.data.local.room.TransactionDao
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.model.transaction.ProductData
import com.ationet.androidterminal.core.domain.model.transaction.TransactionData
import com.ationet.androidterminal.core.domain.model.transaction.TransactionType
import com.ationet.androidterminal.core.domain.model.transaction.TransactionView
import com.ationet.androidterminal.core.domain.repository.TransactionRepository
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
): TransactionRepository {
    override suspend fun getTransactionByAuthorizationCode(
        authorizationCode: String,
        batchId: Int,
    ): TransactionView? {
        val transaction = transactionDao.getTransactionByAuthorizationCode(
            authorizationCode = authorizationCode,
            batchId = batchId,
        )

        return transaction?.let { view ->
            TransactionView(
                id = view.id,
                authorizationCode = view.authorizationCode,
                batchId = view.batchId,
                controllerType = Configuration.ControllerType.valueOf(view.controllerType),
                type = TransactionType.valueOf(view.type),
                transactionSequenceNumber = view.transactionSequenceNumber,
                dateTime = view.dateTime,
                transactionData = view.transactionData.let { transactionData ->
                    TransactionData(
                        primaryTrack = transactionData.primaryTrack,
                        product = transactionData.product.let { productData ->
                            ProductData(
                                inputType = productData.inputType.orEmpty(),
                                name = productData.name.orEmpty(),
                                code = productData.code.orEmpty(),
                                unitPrice = productData.unitPrice ?: 0.0,
                                quantity = productData.quantity ?: 0.0,
                                amount = productData.amount ?: 0.0
                            )
                        }
                    )
                }
            )
        }
    }
}