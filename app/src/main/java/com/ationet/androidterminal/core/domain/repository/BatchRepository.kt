package com.ationet.androidterminal.core.domain.repository

import com.ationet.androidterminal.core.domain.model.batch.Batch
import com.ationet.androidterminal.core.domain.model.batch.BatchTransaction

interface BatchRepository {
    suspend fun openBatch(batch: Batch): Batch
    suspend fun closeBatch(id: Long)
    suspend fun getLastOpenBatch(): Batch?
    suspend fun getTransactionsWithBatchId(batchId: Long, controllerType: String): List<BatchTransaction>
    suspend fun getTransactionsRechargeCCWithBatchId(batchId: Long, controllerType: String): List<BatchTransaction>
    suspend fun getTransactionsReverseCCWithBatchId(batchId: Long, controllerType: String): List<BatchTransaction>
    suspend fun getVoidTransactionsWithBatchId(batchId: Long): List<BatchTransaction>
}