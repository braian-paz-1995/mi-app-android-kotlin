package com.ationet.androidterminal.hal

import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo

import javax.inject.Inject

class HALDeviceInfoImpl @Inject constructor(

) : HALDeviceInfo {
    override val vendor: String = "Newland"

    override val model: String
        get() = "rest"
    override val serialNumber: String
        get() ="rest"
    override val sdkVersion: String
        get() = "rest"

    override fun isDeviceSupported(): Boolean {
        return try {
            true
        } catch (e: Throwable) {
            false
        }
    }
}