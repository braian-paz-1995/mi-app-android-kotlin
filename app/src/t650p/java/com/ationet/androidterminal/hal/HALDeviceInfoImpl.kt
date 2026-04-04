package com.ationet.androidterminal.hal

import android.app.Application
import com.ationet.androidterminal.MyApp
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.newland.sdk.module.devicebasic.DeviceBasicModule
import com.verifone.payment_sdk.SdiSysPropertyString
import javax.inject.Inject
import com.verifone.payment_sdk.*

class HALDeviceInfoImpl @Inject constructor(
    private val deviceBasicModule: DeviceBasicModule, private val paymentSdk: PaymentSdk
) : HALDeviceInfo {
    val sdiManager = paymentSdk.sdiManager

    override val vendor: String = "T650P"

    override val model: String get() = sdiManager.system.getPropertyString(SdiSysPropertyString.HW_MODEL_NAME, 0x01).response

//        get() = deviceBasicModule.deviceInfo.model

    override val serialNumber: String
        get() {

            val fallback = sdiManager.system.serialNumber.response
            val propertyResponse = sdiManager.system.getPropertyString(SdiSysPropertyString.HW_SERIALNO, 0x01)
            return if (propertyResponse.response.isNotEmpty()) propertyResponse.response else fallback
        }

    override val sdkVersion: String get() = "3.66.0"
//    get() = deviceBasicModule.sdkVersion

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