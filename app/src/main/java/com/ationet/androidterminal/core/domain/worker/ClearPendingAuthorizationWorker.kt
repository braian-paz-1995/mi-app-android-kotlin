package com.ationet.androidterminal.core.domain.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.atio.log.Logger
import com.atio.log.util.error
import com.atio.log.util.info
import com.ationet.androidterminal.standalone.batch_close.domain.use_case.GetAllPreAuthorizationUseCase
import com.ationet.androidterminal.standalone.clear_pending.domain.use_case.ExecuteClearPendingUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ClearPendingAuthorizationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val executeClearPendingUseCase: ExecuteClearPendingUseCase,
    private val getAllPreAuthorizationUseCase: GetAllPreAuthorizationUseCase
) : CoroutineWorker(appContext, params) {
    companion object {
        private val logger = Logger("ClearPendingAuthorizationWorker")
        private const val WORK_NAME = "ClearPendingAuthorizationWorker"

        fun enqueue(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequestBuilder<ClearPendingAuthorizationWorker>().build()
            )
        }
    }

    override suspend fun doWork(): Result {
        return try {
            val authorizations = getAllPreAuthorizationUseCase.invoke()
            authorizations.forEach { authorization ->
                logger.info("Executing clear pending for pre-authorization #${authorization.preAuthorization.id}")
                executeClearPendingUseCase.invoke(
                    preAuthorizationId = authorization.preAuthorization.id,
                    productName = authorization.productName,
                    productCode = authorization.productCode,
                    unitPrice = authorization.productUnitPrice,
                    completionAmount = 0.0,
                    completionQuantity = 0.0
                )
            }
            Result.success()
        } catch (e: Exception) {
            logger.error("Error executing clear pending authorization: ${e.message}", e)
            Result.failure()
        }
    }
}
