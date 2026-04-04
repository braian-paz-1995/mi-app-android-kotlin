package com.ationet.androidterminal.core.domain.repository


import com.ationet.androidterminal.core.domain.model.batch.LoyaltyBatch
import com.ationet.androidterminal.core.domain.model.batch.LoyaltyBatchTransaction

interface LoyaltyBatchRepository {
    suspend fun openLoyaltyBatch(batch: LoyaltyBatch): LoyaltyBatch
    suspend fun closeLoyaltyBatch(id: Long)
    suspend fun getLastOpenLoyaltyBatch(): LoyaltyBatch?
    suspend fun getTransactionsWithLoyaltyBatchId(batchId: Long, controllerType: String): List<LoyaltyBatchTransaction>
    suspend fun getTransactionsRedemptionPointsWithLoyaltyBatchId(batchId: Long, controllerType: String): List<LoyaltyBatchTransaction>
    suspend fun getTransactionsRedemptionRewardsWithLoyaltyBatchId(batchId: Long, controllerType: String): List<LoyaltyBatchTransaction>
    suspend fun getVoidTransactionsWithLoyaltyBatchId(batchId: Long): List<LoyaltyBatchTransaction>
}