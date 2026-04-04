package com.ationet.androidterminal.core.domain.use_case.loyalty_batch

import com.atio.log.Logger
import com.atio.log.util.d
import com.ationet.androidterminal.core.domain.model.batch.LoyaltyBatchTransaction
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.repository.LoyaltyBatchRepository
import javax.inject.Inject


class GetTransactionsRedemptionRewardsWithLoyaltyBatchIdUseCase @Inject constructor(
    private val batchRepository: LoyaltyBatchRepository
) {
    companion object {
        private val logger = Logger("GetTransactionsRverseCCWithBatchIdUseCase")
    }

    suspend operator fun invoke(batchId: Long, controllerType: Configuration.ControllerType): List<LoyaltyBatchTransaction> {
        logger.d("Getting transactions for batch ID: $batchId")
        val result = batchRepository.getTransactionsRedemptionRewardsWithLoyaltyBatchId(batchId, controllerType.toString())
        logger.d("Transactions retrieved: $result")
        return result
    }
}