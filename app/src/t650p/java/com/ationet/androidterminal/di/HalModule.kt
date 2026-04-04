package com.ationet.androidterminal.di

import android.content.Context
import android.util.Log
import com.ationet.androidterminal.core.domain.hal.*
import com.ationet.androidterminal.core.domain.hal.card_reader.HALCardReader
import com.ationet.androidterminal.core.domain.hal.led.HALLed
import com.ationet.androidterminal.core.domain.hal.printer.HALPrinter
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.hal.*
import com.ationet.androidterminal.hal.card_reader.HALCardReaderImpl
import com.ationet.androidterminal.hal.card_reader.config.*
import com.ationet.androidterminal.hal.led.HALLedImpl
import com.ationet.androidterminal.hal.printer.HALPrinterImpl
import com.newland.sdk.ModuleManage
import com.newland.sdk.module.buzzer.BuzzerModule
import com.newland.sdk.module.devicebasic.DeviceBasicModule
import com.newland.sdk.module.light.IndicatorLightModule
import com.newland.sdk.module.printer.PrinterModule
import com.verifone.payment_sdk.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.google.gson.Gson

@Module
@InstallIn(SingletonComponent::class)
abstract class HalModule {

    @Binds
    @Singleton
    abstract fun bindCardReaderImplementation(cardReader: HALCardReaderImpl): HALCardReader

    @Binds
    @Singleton
    abstract fun bindLedImplementation(led: HALLedImpl): HALLed

    @Binds
    @Singleton
    abstract fun bindPrinterImplementation(printer: HALPrinterImpl): HALPrinter

    @Binds
    @Singleton
    abstract fun bindInfoImplementation(info: HALDeviceInfoImpl): HALDeviceInfo

    @Binds
    @Singleton
    abstract fun bindBuzzerImplementation(buzzer: HALBuzzerImpl): HALBuzzer

    companion object {

        // ModuleManage initialization
        @Provides
        @Singleton
        fun providesModuleManage(@ApplicationContext context: Context): ModuleManage {
            return ModuleManage.getInstance().apply {
                init(context)
                setDebugMode(true)
            }
        }

        // Payment SDK configuration
        @Provides
        @Singleton
        fun providePaymentSdk(@ApplicationContext context: Context): PaymentSdk {
            return PaymentSdk.create(context).apply {
                val TAG = "PaymentSDK"

                val networkCallback = object : SdiDisconnectCallback() {
                    override fun disconnectCallback() {
                        Log.i(TAG, "Connection with SDI Server is lost")
                        tearDown()
                    }
                }

                val connectionListener = object : CommerceListener2() {
                    override fun handleCommerceEvent(event: CommerceEvent) {
                        Log.i(TAG, "Received event: ${event.type} - ${event.message}")
                    }

                    override fun handleStatus(status: Status) {
                        Log.i(TAG, "Status received: ${status.type} - ${status.message}")

                        when (status.type) {
                            Status.STATUS_INITIALIZED -> {
                                when (status.status) {
                                    StatusCode.SUCCESS -> {
                                        Log.i(TAG, "Initialization success")
                                        sdiManager.setDisconnectCallback(networkCallback)
                                    }

                                    StatusCode.CONFIGURATION_REQUIRED -> {
                                        Log.i(TAG, "Configuration required")
                                    }

                                    else -> {
                                        Log.i(TAG, "Initialization failed")
                                    }
                                }
                            }

                            Status.STATUS_TEARDOWN -> {
                                if (status.status == StatusCode.SUCCESS) {
                                    Log.i(TAG, "Teardown success")
                                } else {
                                    Log.i(TAG, "Teardown failed")
                                }
                            }

                            else -> {
                                Log.i(TAG, "Unhandled status: ${status.type}")
                            }
                        }
                    }
                }

                val config = hashMapOf(
                    TransactionManager.DEVICE_PROTOCOL_KEY to TransactionManager.DEVICE_PROTOCOL_SDI,
                    PsdkDeviceInformation.DEVICE_ADDRESS_KEY to "vfi-terminal",
                    PsdkDeviceInformation.DEVICE_CONNECTION_TYPE_KEY to "tcpip"
                )

                configureLogLevel(PsdkLogLevel.LOG_TRACE)
                initializeFromValues(connectionListener, config)
            }
        }

        // Hardware modules
        @Provides
        @Singleton
        fun providePrinterModule(moduleManage: ModuleManage): PrinterModule =
            moduleManage.printerModule

        @Provides
        @Singleton
        fun provideBasicInfoModule(moduleManage: ModuleManage): DeviceBasicModule =
            moduleManage.deviceBasicModule

        @Provides
        @Singleton
        fun provideIndicationLightModule(moduleManage: ModuleManage): IndicatorLightModule =
            moduleManage.indicatorLightModule

        @Provides
        @Singleton
        fun provideBuzzer(moduleManage: ModuleManage): BuzzerModule = moduleManage.buzzerModule

        // Configuration wrapper
        @Provides
        @Singleton
        fun provideConfiguration(): Configuration = Configuration()

        // EMV Configurations
        @Provides
        @Singleton
        fun provideEmvContactConfig(@ApplicationContext context: Context): EmvContactConfig {
            val json = Utils.getDataFromAssets(context, "config/emvct.json")
            return Gson().fromJson(json, EmvContactConfig::class.java)
        }

        @Provides
        @Singleton
        fun provideEmvCtlsConfig(@ApplicationContext context: Context): EmvCtlsConfig {
            val json = Utils.getDataFromAssets(context, "config/emvctls.json")
            return Gson().fromJson(json, EmvCtlsConfig::class.java)
        }

        @Provides
        @Singleton
        fun provideEmvContactConfigTlv(@ApplicationContext context: Context): EmvContactConfigTlv {
            val json = Utils.getDataFromAssets(context, "config/tlvemvct.json")
            return Gson().fromJson(json, EmvContactConfigTlv::class.java)
        }

        @Provides
        @Singleton
        fun provideEmvCtlsConfigTlv(@ApplicationContext context: Context): EmvCtlsConfigTlv {
            val json = Utils.getDataFromAssets(context, "config/tlvemvctls.json")
            return Gson().fromJson(json, EmvCtlsConfigTlv::class.java)
        }
    }
}
