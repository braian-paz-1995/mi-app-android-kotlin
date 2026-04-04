package com.ationet.androidterminal.core.domain.use_case.batch

import com.atio.log.Logger
import com.atio.log.util.d
import com.ationet.androidterminal.core.domain.model.batch.BatchTransaction
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.repository.BatchRepository
import javax.inject.Inject

class GetTransactionsReverseCCWithBatchIdUseCase @Inject constructor(
    private val batchRepository: BatchRepository
) {
    companion object {
        private val logger = Logger("GetTransactionsRverseCCWithBatchIdUseCase")
    }

    suspend operator fun invoke(batchId: Long, controllerType: Configuration.ControllerType): List<BatchTransaction> {
        logger.d("Getting transactions for batch ID: $batchId")
        val result = batchRepository.getTransactionsReverseCCWithBatchId(batchId, controllerType.toString())
        logger.d("Transactions retrieved: $result")
        return result
    }
}
