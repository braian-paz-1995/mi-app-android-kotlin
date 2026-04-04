package com.ationet.androidterminal.core.domain.use_case.ationet

import com.atio.log.Logger
import com.atio.log.util.error
import com.ationet.androidterminal.BuildConfig
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class RequestChangePin(
    private val configurationUseCase: ConfigurationUseCase,
    private val getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
    private val nativeInterfaceFactory: NativeInterfaceFactory,
    private val deviceInfo: HALDeviceInfo
) {
    companion object {
        val logger = Logger("RequestChangePinUseCase")
    }

    suspend operator fun invoke(
        authorizationCode: String? = null,
        transactionSequenceNumber: Long,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        track: String,
        primaryPin: String,
        oldPin: String,
        newPin: String,
        confirmationPin: String
    ): Result<NativeRequest?> {
        try {
            val configuration = configurationUseCase.getConfiguration()

            if (configuration.ationet.terminalId.isEmpty()) {
                throw TerminalIdNotConfiguredException()
            }

            val nativeInterface = nativeInterfaceFactory.getCurrentInterface()

            val nativeResponse = nativeInterface.requestChangePin(
                terminalId = configuration.ationet.terminalId,
                systemModel = deviceInfo.model,
                systemVersion = BuildConfig.VERSION_NAME,
                authorizationCode = authorizationCode,
                transactionSequenceNumber = transactionSequenceNumber,
                manualEntry = manualEntry,
                localDateTime = localDateTime,
                track = track,
                primaryPin = primaryPin,
                oldPin = oldPin,
                newPin = newPin,
                confirmationPin = confirmationPin,
            )
            return Result.success(nativeResponse)
        } catch (e: Exception) {
            logger.error("Error requesting pin change for track $track", e)
            return Result.failure(e)
        }
    }

    suspend operator fun invoke(
        track: String,
        primaryPin: String,
        newPin: String,
        confirmationPin: String
    ): Result<NativeRequest?> {
        val tsn = getNextTransactionSequenceNumber.invoke()
        return invoke(
            authorizationCode = null,
            transactionSequenceNumber = tsn,
            manualEntry = false,
            localDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            track = track,
            primaryPin = primaryPin,
            oldPin = primaryPin,
            newPin = newPin,
            confirmationPin = confirmationPin,
        )
    }
}