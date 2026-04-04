package com.ationet.androidterminal.core.domain.use_case.batch

import com.atio.log.Logger
import com.atio.log.util.d
import com.ationet.androidterminal.core.domain.model.batch.Batch
import com.ationet.androidterminal.core.domain.repository.BatchRepository
import javax.inject.Inject

class OpenBatchUseCase @Inject constructor(
    private val batchRepository: BatchRepository
) {
    companion object {
        private val logger = Logger("OpenBatchUseCase")
    }

    suspend operator fun invoke(batch: Batch): Batch {
        logger.d("Opening batch: $batch")
        val result = batchRepository.openBatch(batch)
        logger.d("Batch opened successfully: $result")
        return result
    }
}
