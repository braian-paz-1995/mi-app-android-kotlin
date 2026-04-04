package com.ationet.androidterminal.di

import android.device.DeviceManager
import android.device.IccManager
import android.device.MagManager
import android.device.PiccManager
import android.device.PrinterManager
import android.media.AudioManager
import android.media.ToneGenerator
import com.ationet.androidterminal.core.domain.hal.HALBuzzer
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.ationet.androidterminal.core.domain.hal.card_reader.HALCardReader
import com.ationet.androidterminal.core.domain.hal.led.HALLed
import com.ationet.androidterminal.core.domain.hal.printer.HALPrinter
import com.ationet.androidterminal.hal.HALBuzzerImpl
import com.ationet.androidterminal.hal.HALDeviceInfoImpl
import com.ationet.androidterminal.hal.HALLedImpl
import com.ationet.androidterminal.hal.printer.HALPrinterImpl
import com.ationet.androidterminal.hal.card_reader.HALCardReaderImpl
import com.ationet.androidterminal.hal.card_reader.nfc.NfcCommandReceiver
import com.ationet.androidterminal.hal.card_reader.nfc.NfcEventReceiver
import com.ationet.androidterminal.hal.card_reader.nfc.NfcMediator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    abstract fun bindLedImplementation(
        led: HALLedImpl
    ): HALLed

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
    abstract fun bindBuzzerImpl(
        buzzer: HALBuzzerImpl
    ): HALBuzzer

    companion object {
        @Provides
        @Singleton
        fun provideNfcPublisher(
            nfcMediator: NfcMediator
        ) : NfcCommandReceiver = nfcMediator

        @Provides
        @Singleton
        fun provideNfcObserver(
            nfcMediator: NfcMediator
        ): NfcEventReceiver = nfcMediator

        @Provides
        @Singleton
        fun provideDeviceManager(): DeviceManager = DeviceManager()

        @Provides
        @Singleton
        fun providePrinterManager(): PrinterManager = PrinterManager()

        @Provides
        @Singleton
        fun provideMagManager(): MagManager = MagManager()

        @Provides
        @Singleton
        fun provideIccManager(): IccManager = IccManager()

        @Provides
        @Singleton
        fun providePiccManager(): PiccManager = PiccManager()

        @Provides
        @Singleton
        fun providesToneGenerator(): ToneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    }
}