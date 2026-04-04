package com.ationet.androidterminal.core.domain.use_case.terminal_management

import com.atio.terminal_management.TerminalManagement
import com.atio.terminal_management.domain.model.GetNews
import com.ationet.androidterminal.core.domain.exception.HostUrlNotConfiguredException
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetNewsUseCase @Inject constructor(
    private val terminalManagementModule: TerminalManagement,
    private val deviceInfo: HALDeviceInfo
) {
    suspend operator fun invoke(
        host: String,
        terminalId: String?,
        scheduleId: String
    ): GetNews? {
        if (host.isBlank()) {
            throw HostUrlNotConfiguredException()
        }

        if (terminalId.isNullOrBlank()) {
            throw TerminalIdNotConfiguredException()
        }

        return terminalManagementModule.executeGetNews(
            host = host,
            terminalId = terminalId,
            serialNumber = deviceInfo.serialNumber,
            scheduleId = scheduleId
        )

    }
}