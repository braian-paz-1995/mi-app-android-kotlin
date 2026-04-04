package com.ationet.androidterminal.di

import android.content.Context
import com.ationet.androidterminal.core.domain.hal.HALBuzzer
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.ationet.androidterminal.core.domain.hal.card_reader.HALCardReader
import com.ationet.androidterminal.core.domain.hal.led.HALLed
import com.ationet.androidterminal.core.domain.hal.printer.HALPrinter
import com.ationet.androidterminal.hal.HALBuzzerImpl
import com.ationet.androidterminal.hal.HALDeviceInfoImpl
import com.ationet.androidterminal.hal.printer.HALPrinterImpl
import com.ationet.androidterminal.hal.card_reader.HALCardReaderImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HalModule {
    @Binds
    @Singleton
    abstract fun bindCardReaderImplementation(
        cardReader: HALCardReaderImpl
    ): HALCardReader


    @Binds
    @Singleton
    abstract fun bindPrinterImplementation(
        printer: HALPrinterImpl
    ): HALPrinter

    @Binds
    @Singleton
    abstract fun bindInfoImplementation(
        info: HALDeviceInfoImpl
    ): HALDeviceInfo

    @Binds
    @Singleton
    abstract fun bindBuzzerImplementation(
        buzzer: HALBuzzerImpl
    ): HALBuzzer
    companion object {

    }
}
