package com.ationet.androidterminal.core.domain.hal

interface HALDeviceInfo {
    val vendor : String
    val model: String
    val serialNumber: String
    val sdkVersion: String

    /**
     * Determines if the current device is supported for NAT.
     * */
    fun isDeviceSupported() : Boolean
}