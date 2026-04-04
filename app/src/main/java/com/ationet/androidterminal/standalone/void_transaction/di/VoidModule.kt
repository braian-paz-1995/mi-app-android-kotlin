package com.ationet.androidterminal.standalone.void_transaction.di

import android.content.Context
import com.ationet.androidterminal.core.domain.hal.printer.HALPrinter
import com.ationet.androidterminal.core.domain.receipt.ReceiptPrinter
import com.ationet.androidterminal.core.domain.repository.OperationStateRepository
import com.ationet.androidterminal.standalone.void_transaction.data.local.VoidTransactionOperationState
import com.ationet.androidterminal.standalone.void_transaction.data.local.VoidTransactionStateRepository
import com.ationet.androidterminal.standalone.void_transaction.domain.receipt.VoidPrinter
import com.ationet.androidterminal.standalone.void_transaction.domain.receipt.VoidReceiptPrinter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VoidModule {
    @Provides
    @Singleton
    fun provideVoidTransactionOperationRepository(): OperationStateRepository<VoidTransactionOperationState> {
        return VoidTransactionStateRepository()
    }

    @Provides
    @Singleton
    @VoidPrinter
    fun provideVoidPrinter(
        @ApplicationContext context: Context,
        printer: HALPrinter
    ): ReceiptPrinter {
        return VoidReceiptPrinter(
            context = context,
            printer = printer
        )
    }
}