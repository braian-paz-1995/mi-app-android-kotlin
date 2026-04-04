package com.ationet.androidterminal.core.domain.use_case.ationet

import com.atio.log.Logger
import com.atio.log.util.error
import com.ationet.androidterminal.BuildConfig
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import kotlinx.datetime.LocalDateTime

class RequestBalanceInquiry(
    private val configurationUseCase: ConfigurationUseCase,
    private val getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
    private val nativeInterfaceFactory: NativeInterfaceFactory,
    private val deviceInfo: HALDeviceInfo
) {
    companion object {
        val logger = Logger("RequestBalanceInquiry")
    }

    suspend operator fun invoke(
        localDateTime: LocalDateTime,
        manualEntry: Boolean = false,
        primaryTrack: String,
        pumpNumber: Int,
        productCode: String,
        unitPrice: Double
    ): Result<NativeRequest?> {
        try {
            val configuration = configurationUseCase.getConfiguration()

            if (configuration.ationet.terminalId.isEmpty()) {
                throw TerminalIdNotConfiguredException()
            }

            val nativeInterface = nativeInterfaceFactory.getCurrentInterface()

            val nativeResponse = nativeInterface.requestBalanceInquiry(
                terminalId = configuration.ationet.terminalId,
                systemModel = deviceInfo.model,
                systemVersion = BuildConfig.VERSION_NAME,
                transactionSequenceNumber = getNextTransactionSequenceNumber.invoke(),
                localDateTime = localDateTime,
                manualEntry = manualEntry,
                primaryTrack = primaryTrack,
                pumpNumber = pumpNumber,
                productCode = productCode,
                unitPrice = unitPrice,
            )
            return Result.success(nativeResponse)
        } catch (e: Exception) {
            logger.error("Error requesting balance inquiry", e)
            return Result.failure(e)
        }
    }
}