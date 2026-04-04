package com.ationet.androidterminal.core.domain.use_case.loyalty_batch

import com.atio.log.Logger
import com.atio.log.util.d
import com.ationet.androidterminal.core.domain.model.batch.LoyaltyBatch
import com.ationet.androidterminal.core.domain.repository.LoyaltyBatchRepository
import javax.inject.Inject


class GetLastOpenLoyaltyBatchUseCase @Inject constructor(
    private val batchRepository: LoyaltyBatchRepository
) {
    companion object {
        private val logger = Logger("GetLastOpenBatchUseCase")
    }

    suspend operator fun invoke(): LoyaltyBatch? {
        logger.d("Getting last open batch")
        val result = batchRepository.getLastOpenLoyaltyBatch()
        logger.d("Last open batch: $result")
        return result
    }
}