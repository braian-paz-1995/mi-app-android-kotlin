package com.ationet.androidterminal.core.domain.use_case.batch

import com.atio.log.Logger
import com.atio.log.util.d
import com.ationet.androidterminal.core.domain.model.batch.BatchTransaction
import com.ationet.androidterminal.core.domain.repository.BatchRepository
import javax.inject.Inject

class GetVoidTransactionsWithBatchIdUseCase @Inject constructor(
    private val batchRepository: BatchRepository
) {
    companion object {
        private val logger = Logger("GetVoidTransactionsWithBatchIdUseCase")
    }

    suspend operator fun invoke(batchId: Long): List<BatchTransaction> {
        logger.d ( "Getting void transactions for batch ID: $batchId" )
        val result = batchRepository.getVoidTransactionsWithBatchId(batchId)
        logger.d ( "Void transactions retrieved: $result" )
        return result
    }
}
