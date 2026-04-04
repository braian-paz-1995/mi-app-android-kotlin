package com.ationet.androidterminal.hal

import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.newland.sdk.module.devicebasic.DeviceBasicModule
import javax.inject.Inject

class HALDeviceInfoImpl @Inject constructor(
    private val deviceBasicModule: DeviceBasicModule,
) : HALDeviceInfo {
    override val vendor: String = "Newland"

    override val model: String
        get() = deviceBasicModule.deviceInfo.model
    override val serialNumber: String
        get() = deviceBasicModule.deviceInfo.sn
    override val sdkVersion: String
        get() = deviceBasicModule.sdkVersion

    override fun isDeviceSupported(): Boolean {
        return try {
            deviceBasicModule
                .deviceInfo
                .firmwareVer
                .isNotEmpty()
        } catch (e: Throwable) {
            false
        }
    }
}