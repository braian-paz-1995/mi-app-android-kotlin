package com.ationet.androidterminal.core.domain.use_case.ationet

import com.atio.log.Logger
import com.atio.log.util.error
import com.ationet.androidterminal.BuildConfig
import com.ationet.androidterminal.core.data.remote.ationet.MessageVersion
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import javax.inject.Inject

class RequestModules @Inject constructor(
    private val nativeInterfaceFactory: NativeInterfaceFactory,
    private val getConfigurationUseCase: ConfigurationUseCase,
    private val deviceInfo: HALDeviceInfo,
) {
    companion object {
        val logger = Logger("RequestModules")
    }
    data class ResultDto(
        val response: NativeRequest?,
        val rawRequestJson: String,
        val rawResponseJson: String?,
    )

    suspend operator fun invoke(): Result<ResultDto> {
        try {
            val configuration = getConfigurationUseCase.getConfiguration()

            if (configuration.ationet.terminalId.isEmpty()) {
                throw TerminalIdNotConfiguredException()
            }

            val requestPayload = mapOf(
                "MessageFormatVersion" to MessageVersion,
                "SubscriberCode" to configuration.ationet.terminalId,
                "TransactionCode" to "516",
                "systemModel" to deviceInfo.model,
                "systemVersion" to BuildConfig.VERSION_NAME,
                "terminalId" to configuration.ationet.terminalId,
                "lastVersion" to 0,

            )

            val nativeInterface = nativeInterfaceFactory.getCurrentInterface()
            val nativeResponse: NativeRequest? = nativeInterface.requestModules(
                subscriberCode = configuration.ationet.terminalId,
                systemModel = deviceInfo.model,
                systemVersion = BuildConfig.VERSION_NAME,
                terminalId = configuration.ationet.terminalId,
                messageFormatVersion = MessageVersion,
                lastVersion = 0,
            )

            val rawRequestJson = requestPayload.toString()
            val rawResponseJson = nativeResponse?.toString()

            return Result.success(ResultDto(
                response = nativeResponse,
                rawRequestJson = rawRequestJson,
                rawResponseJson = rawResponseJson

            ))
        } catch (e: Exception) {
            logger.error("Error requesting modules", e)
            return Result.failure(e)
        }
    }
}
