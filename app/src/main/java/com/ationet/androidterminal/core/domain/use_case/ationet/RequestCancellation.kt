package com.ationet.androidterminal.core.domain.use_case.ationet

import com.atio.log.Logger
import com.atio.log.util.debug
import com.atio.log.util.error
import com.ationet.androidterminal.BuildConfig
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import kotlinx.datetime.LocalDateTime

class RequestCancellation(
    private val configurationUseCase: ConfigurationUseCase,
    private val nativeInterfaceFactory: NativeInterfaceFactory,
    private val deviceInfo: HALDeviceInfo
) {
    companion object {
        val logger = Logger("RequestCancellationUseCase")
    }

    suspend operator fun invoke(
        authorizationCode: String? = null,
        transactionSequenceNumber: Long,
        localDateTime: LocalDateTime,
        manualEntry: Boolean = false,
        originalAuthorizationCode: String,
        originalTransactionSequenceNumber: Long,
        originalLocalDateTime: LocalDateTime,
    ): NativeRequest? {
        try {
            val configuration = configurationUseCase.getConfiguration()
            logger.debug("Requesting cancellation of authorization #$authorizationCode")

            if (configuration.ationet.terminalId.isEmpty()) {
                throw TerminalIdNotConfiguredException()
            }

            val nativeInterface = nativeInterfaceFactory.getCurrentInterface()

            val nativeResponse = nativeInterface.requestCancellation(
                terminalId = configuration.ationet.terminalId,
                systemModel = deviceInfo.model,
                systemVersion = BuildConfig.VERSION_NAME,
                authorizationCode = authorizationCode,
                transactionSequenceNumber = transactionSequenceNumber,
                localDateTime = localDateTime,
                manualEntry = manualEntry,
                originalAuthorizationCode = originalAuthorizationCode,
                originalTransactionSequenceNumber = originalTransactionSequenceNumber,
                originalLocalDateTime = originalLocalDateTime,
                currencyCode = configuration.currencyCode
            )
            return nativeResponse
        } catch (e: Exception) {
            logger.error("Error requesting cancellation of authorization #$authorizationCode", e)
            return null
        }
    }
}