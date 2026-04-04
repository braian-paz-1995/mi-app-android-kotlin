package com.ationet.androidterminal.hal

import android.device.DeviceManager
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import javax.inject.Inject

class HALDeviceInfoImpl @Inject constructor(
    private val deviceManager: DeviceManager,
) : HALDeviceInfo {
    override val vendor: String = "Urovo"

    override val model: String
        get() = "i9100s"
    override val serialNumber: String
        get() = deviceManager.deviceId
    override val sdkVersion: String
        get() = "UNKNOWN"

    override fun isDeviceSupported(): Boolean {
        return try {
            deviceManager.deviceId.isNotEmpty()
        } catch (e: Throwable) {
            false
        }
    }
}