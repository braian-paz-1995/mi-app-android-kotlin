package com.ationet.androidterminal.standalone.balance_enquiry.di

import android.content.Context
import com.ationet.androidterminal.core.domain.hal.printer.HALPrinter
import com.ationet.androidterminal.core.domain.receipt.ReceiptPrinter
import com.ationet.androidterminal.core.domain.repository.OperationStateRepository
import com.ationet.androidterminal.standalone.balance_enquiry.data.local.BalanceEnquiryOperationState
import com.ationet.androidterminal.standalone.balance_enquiry.data.local.BalanceEnquiryStateRepository
import com.ationet.androidterminal.standalone.balance_enquiry.domain.receipt.BalanceEnquiryPrinter
import com.ationet.androidterminal.standalone.balance_enquiry.domain.receipt.BalanceEnquiryReceiptPrinter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BalanceEnquiryModule {
    @Provides
    @Singleton
    fun provideBalanceEnquiryOperationRepository(): OperationStateRepository<BalanceEnquiryOperationState> {
        return BalanceEnquiryStateRepository()
    }

    @Provides
    @Singleton
    @BalanceEnquiryPrinter
    fun provideBalanceEnquiryPrinter(
        @ApplicationContext context: Context,
        printer: HALPrinter
    ): ReceiptPrinter {
        return BalanceEnquiryReceiptPrinter(
            context = context,
            printer = printer
        )
    }
}