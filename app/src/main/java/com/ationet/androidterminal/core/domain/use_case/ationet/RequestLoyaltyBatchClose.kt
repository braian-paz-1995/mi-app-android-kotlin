package com.ationet.androidterminal.core.domain.use_case.ationet

import com.atio.log.Logger
import com.atio.log.util.error
import com.ationet.androidterminal.BuildConfig
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

class RequestLoyaltyBatchClose @Inject constructor(
    private val nativeInterfaceFactory: NativeInterfaceFactory,
    private val getConfigurationUseCase: ConfigurationUseCase,
    private val deviceInfo: HALDeviceInfo
) {
    companion object {
        val logger = Logger("RequestBatchClose")
    }

    suspend operator fun invoke(
        transactionSequenceNumber: Long,
        localTransactionDate: LocalDateTime,
        localTransactionTime: LocalDateTime,
        batchNumber:String,
        salesCounter: Int,
        salesTotal: Double,
        voidedCounter: Int,
        voidedTotal: Double
    ): Result<NativeRequest?> {
        try {
            val configuration = getConfigurationUseCase.getConfiguration()

            if (configuration.ationet.terminalId.isEmpty()) {
                throw TerminalIdNotConfiguredException()
            }

            val nativeInterface = nativeInterfaceFactory.getCurrentInterface()

            val nativeResponse = nativeInterface.requestBatchClose(
                terminalId = configuration.ationet.terminalId,
                systemModel = deviceInfo.model,
                systemVersion = BuildConfig.VERSION_NAME,
                transactionSequenceNumber = transactionSequenceNumber,
                localTransactionDate = localTransactionDate,
                localTransactionTime = localTransactionTime,
                salesCounter = salesCounter,
                salesTotal = salesTotal,
                voidedCounter = voidedCounter,
                voidedTotal = voidedTotal,
                batchNumber = batchNumber
            )
            return Result.success(nativeResponse)
        } catch (e: Exception) {
            logger.error("Error requesting batch close", e)
            return Result.failure(e)
        }

    }
}