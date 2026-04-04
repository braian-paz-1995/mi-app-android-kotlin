package com.ationet.androidterminal.core.domain.use_case.ationet

import com.atio.log.Logger
import com.ationet.androidterminal.BuildConfig
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import io.ktor.serialization.JsonConvertException
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerializationException

class RequestActiveGC(
    private val configurationUseCase: ConfigurationUseCase,
    private val getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
    private val nativeInterfaceFactory: NativeInterfaceFactory,
    private val deviceInfo: HALDeviceInfo
) {
    companion object {
        val logger = Logger("RequestRechargeCCUseCase")
    }

    suspend operator fun invoke(
        authorizationCode: String? = null,
        transactionSequenceNumber: Long,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        track: String,
        amount: Double,
        username: String? = null,
        password: String? = null

    ): Result<NativeRequest?> {
        try {
            val configuration = configurationUseCase.getConfiguration()

            if (configuration.ationet.terminalId.isEmpty()) {
                throw TerminalIdNotConfiguredException()
            }

            val nativeInterface = nativeInterfaceFactory.getCurrentInterface()

            val nativeResponse = nativeInterface.requestActiveGC(
                terminalId = configuration.ationet.terminalId,
                systemModel = deviceInfo.model,
                systemVersion = BuildConfig.VERSION_NAME,
                authorizationCode = authorizationCode,
                //transactionSequenceNumber = transactionSequenceNumber,
                manualEntry = manualEntry,
                localDateTime = localDateTime,
                track = track,
                amount = amount,
                username = username,
                password = password,
                currencyCode = configuration.currencyCode
            )


            return Result.success(nativeResponse)
        } catch (e: Exception) {
            return if (
                e is SerializationException ||
                e is JsonConvertException
            ) {
                if (e.message!!.contains("Operation Succeeded")) {
                    val fallback = NativeRequest(
                        responseCode = "00000",
                        responseMessage = "Operation Succeeded",
                        responseError = null
                    )
                    Result.success(fallback)
                } else {
                    Result.failure(e)
                }
            } else {
                Result.failure(e)
            }
        }

    }

    suspend operator fun invoke(
        track: String,
        amount: Double,
        username: String? = null,
        password: String? = null

    ): Result<NativeRequest?> {
        val tsn = getNextTransactionSequenceNumber.invoke()
        return invoke(
            authorizationCode = null,
            transactionSequenceNumber = tsn,
            manualEntry = false,
            localDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            track = track,
            amount = amount,
            username = username,
            password = password
        )
    }
}