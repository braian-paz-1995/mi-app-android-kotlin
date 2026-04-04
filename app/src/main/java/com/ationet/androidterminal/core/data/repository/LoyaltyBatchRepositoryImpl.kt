package com.ationet.androidterminal.core.data.repository

import com.atio.log.Logger
import com.atio.log.util.d
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_batch.LoyaltyBatchEntity
import com.ationet.androidterminal.core.data.local.room.loyalty.LoyaltyBatchDao
import com.ationet.androidterminal.core.domain.model.batch.LoyaltyBatch
import com.ationet.androidterminal.core.domain.model.batch.LoyaltyBatchTransaction
import com.ationet.androidterminal.core.domain.repository.LoyaltyBatchRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoyaltyBatchRepositoryImpl @Inject constructor(
    private val batchDao: LoyaltyBatchDao
) : LoyaltyBatchRepository {
    companion object {
        private val logger = Logger("BatchRepositoryImpl")
    }

    override suspend fun openLoyaltyBatch(batch: LoyaltyBatch): LoyaltyBatch {
        logger.d("openBatch: $batch")
        val batchEntity = with(batch) {
            LoyaltyBatchEntity(
                transactionDateTime = transactionDateTime,
                state = when (state) {
                    LoyaltyBatch.State.OPEN -> LoyaltyBatchEntity.State.OPEN
                    LoyaltyBatch.State.CLOSED -> LoyaltyBatchEntity.State.CLOSED
                }
            )
        }
        val rowId = batchDao.openLoyaltyBatch(batchEntity)
        return batch.copy(id = rowId.toInt())
    }

    override suspend fun closeLoyaltyBatch(id: Long) {
        logger.d("closeBatch: $id")
        batchDao.closeLoyaltyBatch(id)
    }

    override suspend fun getLastOpenLoyaltyBatch(): LoyaltyBatch? {
        logger.d("getLastOpenBatch")
        val entity = batchDao.getLastOpenLoyaltyBatch()
        logger.d("getLastOpenLoyaltyBatch DAO result: $entity")
        return entity?.let {
            LoyaltyBatch(
                id = it.id.toInt(),
                transactionDateTime = it.transactionDateTime,
                state = when (it.state) {
                    LoyaltyBatchEntity.State.OPEN -> LoyaltyBatch.State.OPEN
                    LoyaltyBatchEntity.State.CLOSED -> LoyaltyBatch.State.CLOSED
                }
            )
        }
    }

    override suspend fun getTransactionsWithLoyaltyBatchId(batchId: Long, controllerType: String): List<LoyaltyBatchTransaction> {
        logger.d("getTransactionsWithBatchId: $batchId")
        return batchDao.getTransactionsWithLoyaltyBatchId(batchId, controllerType).map {
            LoyaltyBatchTransaction(
                id = it.id,
                amount = it.amount,
                quantity = it.quantity,
                unitPrice = it.unitPrice,
                paymentMethod = it.paymentMethod ?: ""
            )
        }
    }
    override suspend fun getTransactionsRedemptionPointsWithLoyaltyBatchId(batchId: Long, controllerType: String): List<LoyaltyBatchTransaction> {
        logger.d("getTransactionsRechargeCCWithBatchId: $batchId")
        return batchDao.getTransactionsRedemptionPointsWithLoyaltyBatchId(batchId, controllerType).map {
            LoyaltyBatchTransaction(
                id = it.id,
                amount = it.amount,
                quantity = it.quantity,
                unitPrice = it.unitPrice,
                paymentMethod = it.paymentMethod ?: ""
            )
        }
    }
    override suspend fun getTransactionsRedemptionRewardsWithLoyaltyBatchId(batchId: Long, controllerType: String): List<LoyaltyBatchTransaction> {
        logger.d("getTransactionsReverseCCWithBatchId: $batchId")
        return batchDao.getTransactionsRedemptionRewardsWithLoyaltyBatchId(batchId, controllerType).map {
            LoyaltyBatchTransaction(
                id = it.id,
                amount = it.amount,
                quantity = it.quantity,
                unitPrice = it.unitPrice,
                paymentMethod = it.paymentMethod ?: ""
            )
        }
    }

    override suspend fun getVoidTransactionsWithLoyaltyBatchId(batchId: Long): List<LoyaltyBatchTransaction> {
        logger.d("getVoidTransactionsWithBatchId: $batchId")
        return batchDao.getVoidTransactionsWithLoyaltyBatchId(batchId).map {
            LoyaltyBatchTransaction(
                id = it.id,
                amount = it.amount,
                quantity = it.quantity,
                unitPrice = it.unitPrice,
                paymentMethod = it.paymentMethod ?: ""
            )
        }
    }
}