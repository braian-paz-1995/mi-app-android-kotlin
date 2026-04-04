package com.ationet.androidterminal.core.change_pin.di

import android.content.Context
import com.ationet.androidterminal.core.change_pin.domain.receipt.ChangePinPrinter
import com.ationet.androidterminal.core.change_pin.domain.receipt.ChangePinReceiptPrinter
import com.ationet.androidterminal.core.domain.hal.printer.HALPrinter
import com.ationet.androidterminal.core.domain.receipt.ReceiptPrinter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChangePinModule {
    @Provides
    @Singleton
    @ChangePinPrinter
    fun provideChangePinPrinter(
        @ApplicationContext context: Context,
        printer: HALPrinter
    ): ReceiptPrinter {
        return ChangePinReceiptPrinter(
            context = context,
            printer = printer
        )
    }
}