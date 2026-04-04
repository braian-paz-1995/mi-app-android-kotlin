package com.ationet.androidterminal.core.data.repository

import com.atio.log.Logger
import com.atio.log.util.d
import com.ationet.androidterminal.core.data.local.room.entity.task.batch.BatchEntity
import com.ationet.androidterminal.core.data.local.room.task.BatchDao
import com.ationet.androidterminal.core.domain.model.batch.Batch
import com.ationet.androidterminal.core.domain.model.batch.BatchTransaction
import com.ationet.androidterminal.core.domain.repository.BatchRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BatchRepositoryImpl @Inject constructor(
    private val batchDao: BatchDao
) : BatchRepository {
    companion object {
        private val logger = Logger("BatchRepositoryImpl")
    }

    override suspend fun openBatch(batch: Batch): Batch {
        logger.d("openBatch: $batch")
        val batchEntity = with(batch) {
            BatchEntity(
                transactionDateTime = transactionDateTime,
                state = when (state) {
                    Batch.State.OPEN -> BatchEntity.State.OPEN
                    Batch.State.CLOSED -> BatchEntity.State.CLOSED
                }
            )
        }
        val rowId = batchDao.openBatch(batchEntity)
        return batch.copy(id = rowId.toInt())
    }

    override suspend fun closeBatch(id: Long) {
        logger.d("closeBatch: $id")
        batchDao.closeBatch(id)
    }

    override suspend fun getLastOpenBatch(): Batch? {
        logger.d("getLastOpenBatch")
        return batchDao.getLastOpenBatch()?.let {
            Batch(
                id = it.id.toInt(),
                transactionDateTime = it.transactionDateTime,
                state = when (it.state) {
                    BatchEntity.State.OPEN -> Batch.State.OPEN
                    BatchEntity.State.CLOSED -> Batch.State.CLOSED
                }
            )
        }
    }

    override suspend fun getTransactionsWithBatchId(batchId: Long, controllerType: String): List<BatchTransaction> {
        logger.d("getTransactionsWithBatchId: $batchId")
        return batchDao.getTransactionsWithBatchId(batchId, controllerType).map {
            BatchTransaction(
                id = it.id,
                amount = it.amount,
                quantity = it.quantity,
                unitPrice = it.unitPrice,
                paymentMethod = it.paymentMethod ?: ""
            )
        }
    }
    override suspend fun getTransactionsRechargeCCWithBatchId(batchId: Long, controllerType: String): List<BatchTransaction> {
        logger.d("getTransactionsRechargeCCWithBatchId: $batchId")
        return batchDao.getTransactionsRechargeCCWithBatchId(batchId, controllerType).map {
            BatchTransaction(
                id = it.id,
                amount = it.amount,
                quantity = it.quantity,
                unitPrice = it.unitPrice,
                paymentMethod = it.paymentMethod ?: ""
            )
        }
    }
    override suspend fun getTransactionsReverseCCWithBatchId(batchId: Long, controllerType: String): List<BatchTransaction> {
        logger.d("getTransactionsReverseCCWithBatchId: $batchId")
        return batchDao.getTransactionsReverseCCWithBatchId(batchId, controllerType).map {
            BatchTransaction(
                id = it.id,
                amount = it.amount,
                quantity = it.quantity,
                unitPrice = it.unitPrice,
                paymentMethod = it.paymentMethod ?: ""
            )
        }
    }

    override suspend fun getVoidTransactionsWithBatchId(batchId: Long): List<BatchTransaction> {
        logger.d("getVoidTransactionsWithBatchId: $batchId")
        return batchDao.getVoidTransactionsWithBatchId(batchId).map {
            BatchTransaction(
                id = it.id,
                amount = it.amount,
                quantity = it.quantity,
                unitPrice = it.unitPrice,
                paymentMethod = it.paymentMethod ?: ""
            )
        }
    }
}