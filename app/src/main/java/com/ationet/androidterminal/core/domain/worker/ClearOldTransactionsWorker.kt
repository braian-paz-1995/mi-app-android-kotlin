package com.ationet.androidterminal.core.domain.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.atio.log.Logger
import com.atio.log.util.error
import com.atio.log.util.info
import com.ationet.androidterminal.core.data.local.preauthorization.PreAuthorizationStandAloneRepository
import com.ationet.androidterminal.core.domain.model.transaction.TransactionView
import com.ationet.androidterminal.core.domain.repository.CompletionRepository
import com.ationet.androidterminal.core.domain.repository.ReceiptRepository
import com.ationet.androidterminal.core.domain.repository.SaleRepository
import com.ationet.androidterminal.core.domain.repository.TransactionRepository
import com.ationet.androidterminal.core.domain.repository.VoidRepository
import com.ationet.androidterminal.core.domain.use_case.batch.GetLastOpenBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.standalone.batch_close.domain.use_case.GetAllPreAuthorizationUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import java.time.Duration

@HiltWorker
class ClearOldTransactionsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val getConfiguration: GetConfiguration,
    private val preGetAllPreAuthorizationUseCase: GetAllPreAuthorizationUseCase,
    private val preAuthorizationRepository: PreAuthorizationStandAloneRepository,
    private val saleRepository: SaleRepository,
    private val completionRepository: CompletionRepository,
    private val receiptRepository: ReceiptRepository,
    private val getLastOpenBatchUseCase: GetLastOpenBatchUseCase,
    private val voidRepository: VoidRepository,
    private val transactionRepository: TransactionRepository,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        try {
            val configuration = getConfiguration()
            val transactionExpirationDays = configuration.transactionExpirationDays
            val currentInstant = Clock.System.now().minus(transactionExpirationDays, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
            val systemTimeZone = TimeZone.currentSystemDefault()
            val transactionExpirationLocalDateTime = currentInstant.toLocalDateTime(systemTimeZone)

            logger.info("Executing clear old transactions worker")
            logger.info("Transaction expiration days: $transactionExpirationDays")
            coroutineScope {
                val transactionsDeferred = mutableListOf<Deferred<Boolean>>()

                val lastOpenBatch = getLastOpenBatchUseCase.invoke() ?: return@coroutineScope
                logger.info("Last open batch: ${lastOpenBatch.id}")

                val preAuthorizations = preGetAllPreAuthorizationUseCase.invoke()
                logger.info("Pre-authorizations retrieved: ${preAuthorizations.size}")

                preAuthorizations.filter {
                    val transactionDate = it.preAuthorization.createAt
                    transactionDate < transactionExpirationLocalDateTime
                }.forEach { preAuthorization ->
                    logger.info("#${transactionsDeferred.size} Pre-authorization: ${preAuthorization.preAuthorization.id}")
                    transactionsDeferred.plus(async { preAuthorizationRepository.deletePreAuthorization(preAuthorization.preAuthorization.id) })
                }

                val sales = saleRepository.getAll()
                logger.info("Sales retrieved: ${sales.size}")

                sales.filter {
                    val transactionDate = it.transactionDateTime
                    transactionDate < transactionExpirationLocalDateTime && it.batchId != lastOpenBatch.id
                }.forEach { sale ->
                    logger.info("#${transactionsDeferred.size} Sale: ${sale.id}")
                    transactionsDeferred.plus(async { saleRepository.delete(sale.id) })

                    val voidDetail = getTransactionView(sale.authorizationCode, lastOpenBatch.id)
                    if (voidDetail != null) {
                        logger.info("#${transactionsDeferred.size} Void detail: ${voidDetail.id}")
                        transactionsDeferred.plus(async { voidRepository.delete(voidDetail.id) })
                    }
                }

                val completions = completionRepository.getAllCompletion()
                logger.info("Completions retrieved: ${completions.size}")

                completions.filter {
                    val transactionDate = it.transactionDateTime
                    transactionDate < transactionExpirationLocalDateTime && it.batchId != lastOpenBatch.id
                }.forEach { completion ->
                    logger.info("#${transactionsDeferred.size} Completion: ${completion.id}")
                    transactionsDeferred.plus(async { completionRepository.deleteCompletion(completion.id) })

                    val voidDetail = getTransactionView(completion.authorizationCode, lastOpenBatch.id)
                    if (voidDetail != null) {
                        logger.info("Void detail: ${voidDetail.id}")
                        transactionsDeferred.plus(async { voidRepository.delete(voidDetail.id) })
                    }
                }

                val receipt = receiptRepository.getAll()
                logger.info("Receipts retrieved: ${receipt.size}")

                receipt.filter {
                    val transactionDate = it.createdDateTime
                    transactionDate < transactionExpirationLocalDateTime && it.batchId != lastOpenBatch.id
                }.forEach { receipt ->
                    logger.info("#${transactionsDeferred.size} Receipt: ${receipt.id}")
                    transactionsDeferred.plus(async { receiptRepository.delete(receipt.id) })
                }

                logger.info("Transactions deferred: ${transactionsDeferred.size}")
                val transactions = transactionsDeferred.awaitAll()

                logger.info("Transactions: ${transactions.size}")
                transactions.forEachIndexed { index, _ ->
                    logger.info("Transaction deleted: $index")
                }
            }

        } catch (e: Exception) {
            logger.error("Error executing clear old transactions worker", e)
            return Result.failure()
        }
        return Result.success()
    }

    private suspend fun getTransactionView(authorizationCode: String, batchId: Int): TransactionView? {
        return transactionRepository.getTransactionByAuthorizationCode(
            authorizationCode = authorizationCode,
            batchId = batchId
        )
    }

    companion object {
        private const val WORK_NAME = "ClearOldTransactionsWorker"
        private val logger = Logger(WORK_NAME)

        fun enqueue(context: Context) {
            logger.info("Enqueueing clear old transactions worker")
            val request = PeriodicWorkRequestBuilder<ClearOldTransactionsWorker>(Duration.ofDays(1))
                .addTag(WORK_NAME)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }

}