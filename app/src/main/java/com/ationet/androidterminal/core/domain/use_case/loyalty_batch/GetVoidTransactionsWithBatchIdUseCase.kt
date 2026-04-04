package com.ationet.androidterminal.core.domain.use_case.loyalty_batch

import com.atio.log.Logger
import com.atio.log.util.d
import com.ationet.androidterminal.core.domain.model.batch.LoyaltyBatchTransaction
import com.ationet.androidterminal.core.domain.repository.LoyaltyBatchRepository
import javax.inject.Inject


class GetVoidTransactionsWithLoyaltyBatchIdUseCase @Inject constructor(
    private val batchRepository: LoyaltyBatchRepository
) {
    companion object {
        private val logger = Logger("GetVoidTransactionsWithBatchIdUseCase")
    }

    suspend operator fun invoke(batchId: Long): List<LoyaltyBatchTransaction> {
        logger.d ( "Getting void transactions for batch ID: $batchId" )
        val result = batchRepository.getVoidTransactionsWithLoyaltyBatchId(batchId)
        logger.d ( "Void transactions retrieved: $result" )
        return result
    }
}