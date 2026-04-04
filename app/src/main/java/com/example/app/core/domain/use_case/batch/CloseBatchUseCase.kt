package com.ationet.androidterminal.core.domain.use_case.batch

import com.atio.log.Logger
import com.atio.log.util.d
import com.ationet.androidterminal.core.domain.repository.BatchRepository
import javax.inject.Inject

class CloseBatchUseCase @Inject constructor(
    private val batchRepository: BatchRepository
) {
    companion object {
        private val logger = Logger("CloseBatchUseCase")
    }

    suspend operator fun invoke(id: Long) {
        logger.d("Closing batch with ID: $id")
        batchRepository.closeBatch(id)
        logger.d("Batch closed successfully")
    }
}
