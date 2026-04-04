package com.ationet.androidterminal.standalone.completion.data.local

import com.ationet.androidterminal.core.data.local.room.entity.fleet.completion.CompletionEntity
import com.ationet.androidterminal.core.data.local.room.entity.fleet.completion.ProductData
import com.ationet.androidterminal.core.data.local.room.entity.fleet.completion.TransactionData
import com.ationet.androidterminal.core.data.local.room.fleet.CompletionDao
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.repository.CompletionRepository
import com.ationet.androidterminal.standalone.completion.domain.model.Completion
import javax.inject.Inject

class CompletionRepositoryImpl @Inject constructor(
    private val completionDao: CompletionDao
) : CompletionRepository {
    override suspend fun createCompletion(completion: Completion): Completion {
        val completionEntity = with(completion) {
            CompletionEntity(
                authorizationCode = authorizationCode,
                transactionSequenceNumber = transactionSequenceNumber,
                transactionDateTime = transactionDateTime,
                transactionData = TransactionData(
                    primaryTrack = completion.transactionData.primaryTrack,
                    product = ProductData(
                        inputType = completion.transactionData.product.inputType,
                        name = completion.transactionData.product.name,
                        code = completion.transactionData.product.code,
                        unitPrice = completion.transactionData.product.unitPrice,
                        amount = completion.transactionData.product.amount,
                        quantity = completion.transactionData.product.quantity
                    )
                ),
                batchId = batchId,
                controllerType = controllerType.toString()
            )
        }

        val rowId = completionDao.create(completionEntity)
        return completion.copy(
            id = rowId.toInt()
        )
    }

    override suspend fun getCompletion(id: Int): Completion? {
        val databaseCompletion = completionDao.get(id) ?: return null

        return databaseCompletion.mapCompletionModelToDomain()
    }

    override suspend fun getAllCompletion(): List<Completion> {
        return completionDao.getAll().map { it.mapCompletionModelToDomain() }
    }

    override suspend fun deleteCompletion(id: Int): Boolean {
        return completionDao.delete(id) > 0
    }

    private fun CompletionEntity.mapCompletionModelToDomain(): Completion {
        return Completion(
            id = id,
            authorizationCode = authorizationCode,
            transactionDateTime = transactionDateTime,
            transactionSequenceNumber = transactionSequenceNumber,
            transactionData = com.ationet.androidterminal.standalone.completion.domain.model.TransactionData(
                primaryTrack = transactionData.primaryTrack,
                product = com.ationet.androidterminal.standalone.completion.domain.model.ProductData(
                    inputType = transactionData.product.inputType,
                    name = transactionData.product.name,
                    code = transactionData.product.code,
                    unitPrice = transactionData.product.unitPrice,
                    amount = transactionData.product.amount,
                    quantity = transactionData.product.quantity
                )
            ),
            batchId = batchId,
            controllerType = Configuration.ControllerType.valueOf(controllerType)
        )
    }
}