package com.ationet.androidterminal.core.domain.use_case.batch

import com.atio.log.Logger
import com.atio.log.util.d
import com.ationet.androidterminal.core.domain.model.batch.Batch
import com.ationet.androidterminal.core.domain.repository.BatchRepository
import javax.inject.Inject

class GetLastOpenBatchUseCase @Inject constructor(
    private val batchRepository: BatchRepository
) {
    companion object {
        private val logger = Logger("GetLastOpenBatchUseCase")
    }

    suspend operator fun invoke(): Batch? {
        logger.d("Getting last open batch")
        val result = batchRepository.getLastOpenBatch()
        logger.d("Last open batch: $result")
        return result
    }
}
