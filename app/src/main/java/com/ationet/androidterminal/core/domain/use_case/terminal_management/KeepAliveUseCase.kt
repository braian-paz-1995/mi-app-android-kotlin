package com.ationet.androidterminal.core.domain.use_case.terminal_management

import android.content.Context
import com.atio.terminal_management.TerminalManagement
import com.atio.terminal_management.domain.model.KeepAlive
import com.ationet.androidterminal.BuildConfig
import com.ationet.androidterminal.core.domain.exception.HostUrlNotConfiguredException
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.ationet.androidterminal.core.domain.util.NetworkUtils
import com.ationet.androidterminal.core.domain.util.OSUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeepAliveUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val terminalManagementModule: TerminalManagement,
    private val deviceInfo: HALDeviceInfo
) {
    suspend operator fun invoke(
        host: String,
        terminalId: String?,
        scheduleId: String?
    ): KeepAlive? {
        if (host.isBlank()) {
            throw HostUrlNotConfiguredException()
        }

        if (terminalId.isNullOrBlank()) {
            throw TerminalIdNotConfiguredException()
        }

        val addressIp = NetworkUtils.getIpAddress(context = context)
        val operatingSystem = OSUtils.operatingSystem

        return terminalManagementModule.executeKeepAlive(
            host = host,
            terminalId = terminalId,
            serialNumber = deviceInfo.serialNumber,
            addressIp = addressIp.orEmpty(),
            operatingSystem = operatingSystem,
            systemModel = deviceInfo.model,
            systemVersion = BuildConfig.VERSION_NAME,
            lastScheduleId = scheduleId
        )

    }
}


