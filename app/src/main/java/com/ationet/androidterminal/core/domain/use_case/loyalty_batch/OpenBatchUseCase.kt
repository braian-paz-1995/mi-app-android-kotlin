package com.ationet.androidterminal.core.domain.use_case.loyalty_batch

import com.atio.log.Logger
import com.atio.log.util.d
import com.ationet.androidterminal.core.domain.model.batch.LoyaltyBatch
import com.ationet.androidterminal.core.domain.repository.LoyaltyBatchRepository
import javax.inject.Inject


class OpenLoyaltyBatchUseCase @Inject constructor(
    private val batchRepository: LoyaltyBatchRepository
) {
    companion object {
        private val logger = Logger("OpenBatchUseCase")
    }

    suspend operator fun invoke(batch: LoyaltyBatch): LoyaltyBatch {
        logger.d("Opening batch: $batch")
        val result = batchRepository.openLoyaltyBatch(batch)
        logger.d("Batch opened successfully: $result")
        return result
    }
}