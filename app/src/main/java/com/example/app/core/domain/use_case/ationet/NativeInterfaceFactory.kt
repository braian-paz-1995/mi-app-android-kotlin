package com.ationet.androidterminal.core.domain.use_case.ationet

import android.util.Log
import com.ationet.androidterminal.core.data.remote.ationet.AtionetNativeInterface
import com.ationet.androidterminal.core.data.remote.ationet.LocalAgentNativeInterface
import com.ationet.androidterminal.core.data.remote.ationet.NativeInterface
import com.ationet.androidterminal.core.domain.exception.HostUrlNotConfiguredException
import com.ationet.androidterminal.core.domain.exception.IpAddressNotConfigured
import com.ationet.androidterminal.core.domain.exception.PortNotConfiguredException
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NativeInterfaceFactory @Inject constructor(
    private val configurationUseCase: ConfigurationUseCase
) {
    fun getCurrentInterface(): NativeInterface {
        val configuration = configurationUseCase.getConfiguration()

        if (configuration.ationet.terminalId.isEmpty()) {
            throw TerminalIdNotConfiguredException()
        }

        return if (configuration.ationet.localAgent) {
            Log.d(TAG, "Native interface factory: Host='${configuration.ationet.localAgentIp}:${configuration.ationet.localAgentPort}'")
            if (configuration.ationet.localAgentIp.isEmpty()) {
                throw IpAddressNotConfigured()
            }

            val localAgentPort = configuration.ationet.localAgentPort.toIntOrNull()
                ?: throw PortNotConfiguredException()

            LocalAgentNativeInterface(
                ip = configuration.ationet.localAgentIp,
                port = localAgentPort
            )
        } else {
            if (configuration.ationet.nativeUrl.isEmpty()) {
                throw HostUrlNotConfiguredException()
            }

            AtionetNativeInterface(configuration.ationet.nativeUrl)
        }
    }


    private companion object {
        private const val TAG: String = "NativeInterfaceFactory"
    }
}