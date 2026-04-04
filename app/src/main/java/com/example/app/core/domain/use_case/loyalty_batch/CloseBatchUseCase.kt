package com.ationet.androidterminal.core.domain.use_case.loyalty_batch

import com.atio.log.Logger
import com.atio.log.util.d
import com.ationet.androidterminal.core.domain.repository.LoyaltyBatchRepository
import javax.inject.Inject


class CloseLoyaltyBatchUseCase @Inject constructor(
    private val batchRepository: LoyaltyBatchRepository
) {
    companion object {
        private val logger = Logger("CloseBatchUseCase")
    }

    suspend operator fun invoke(id: Long) {
        logger.d("Closing batch with ID: $id")
        batchRepository.closeLoyaltyBatch(id)
        logger.d("Batch closed successfully")
    }
}