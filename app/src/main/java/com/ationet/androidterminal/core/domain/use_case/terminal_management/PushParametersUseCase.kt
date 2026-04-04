package com.ationet.androidterminal.core.domain.use_case.terminal_management

import com.atio.terminal_management.TerminalManagement
import com.atio.terminal_management.domain.model.PushParameters
import com.ationet.androidterminal.BuildConfig
import com.ationet.androidterminal.core.domain.exception.HostUrlNotConfiguredException
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushParametersUseCase @Inject constructor(
    private val terminalManagementModule: TerminalManagement,
    private val deviceInfo: HALDeviceInfo
) {
    suspend operator fun invoke(
        host: String,
        terminalId: String?,
        parameters: Map<String, String>
    ): PushParameters? {
        if (host.isBlank()) {
            throw HostUrlNotConfiguredException()
        }

        if (terminalId.isNullOrBlank()) {
            throw TerminalIdNotConfiguredException()
        }
        return terminalManagementModule.executePushParameters(
            host = host,
            terminalId = terminalId,
            serialNumber = deviceInfo.serialNumber,
            systemVersion = BuildConfig.VERSION_NAME,
            systemModel = deviceInfo.model,
            parameters = parameters
        )
    }
}