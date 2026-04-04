package com.ationet.androidterminal.core.domain.use_case.ationet

import com.atio.log.Logger
import com.atio.log.util.error
import com.ationet.androidterminal.BuildConfig
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.data.remote.ationet.model.OriginalData
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestVoidTransaction @Inject constructor(
    private val nativeInterfaceFactory: NativeInterfaceFactory,
    private val getConfigurationUseCase: ConfigurationUseCase,
    private val deviceInfo: HALDeviceInfo
) {
    companion object {
        val logger = Logger("RequestVoidTransaction")
    }

    suspend operator fun invoke(
        transactionSequenceNumber: Long,
        localTransactionDate: LocalDateTime,
        localTransactionTime: LocalDateTime,
        primaryTrack: String,
        originalData: OriginalData?
    ): Result<NativeRequest?> {
        try {
            val configuration = getConfigurationUseCase.getConfiguration()

            if (configuration.ationet.terminalId.isEmpty()) {
                throw TerminalIdNotConfiguredException()
            }

            val nativeInterface = nativeInterfaceFactory.getCurrentInterface()

            val nativeResponse = nativeInterface.requestVoidTransaction(
                terminalId = configuration.ationet.terminalId,
                systemModel = deviceInfo.model,
                systemVersion = BuildConfig.VERSION_NAME,
                transactionSequenceNumber = transactionSequenceNumber,
                localTransactionDate = localTransactionDate,
                localTransactionTime = localTransactionTime,
                primaryTrack = primaryTrack,
                originalData = originalData,
            )
            return Result.success(nativeResponse)
        } catch (e: Exception) {
            logger.error("Error requesting void transaction", e)
            return Result.failure(e)
        }

    }
}