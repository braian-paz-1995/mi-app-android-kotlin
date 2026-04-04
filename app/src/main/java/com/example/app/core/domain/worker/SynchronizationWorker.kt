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
import com.ationet.androidterminal.core.domain.use_case.terminal_management.SynchronizationResult
import com.ationet.androidterminal.core.domain.use_case.terminal_management.SynchronizeUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SynchronizationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val synchronizeUseCase: SynchronizeUseCase
) : CoroutineWorker(appContext, workerParams) {
    companion object {
        private const val WORK_NAME = "SynchronizationWorker"
        private val logger = Logger(WORK_NAME)

        fun enqueue(context: Context, repeatInterval: Long) {
            val workManager = WorkManager.getInstance(context)
            logger.info("Enqueuing synchronization")
            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                PeriodicWorkRequestBuilder<SynchronizationWorker>(
                    repeatInterval = repeatInterval,
                    repeatIntervalTimeUnit = java.util.concurrent.TimeUnit.MINUTES
                ).build()
            )
        }

        fun cancel(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelUniqueWork(WORK_NAME)
        }
    }

    override suspend fun doWork(): Result {
        return try {
            logger.info("Executing synchronization")
            val result = synchronizeUseCase.invoke()

            when (result) {
                SynchronizationResult.Failed -> {
                    Result.failure()
                }

                SynchronizationResult.Ok -> {
                    Result.success()
                }

                is SynchronizationResult.ValidationError -> {
                    Result.failure()
                }
            }

            Result.success()
        } catch (e: Exception) {
            logger.error("Error executing synchronization: ${e.message}", e)
            if (runAttemptCount > 5) {
                Result.failure()
            } else {
                Result.retry()
            }
        }
    }
}