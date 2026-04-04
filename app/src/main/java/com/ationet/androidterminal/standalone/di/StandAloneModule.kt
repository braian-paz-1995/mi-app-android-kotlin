package com.ationet.androidterminal.standalone.di

//import com.ationet.androidterminal.standalone.change_pin.domain.receipt.ChangePinPrinter
import com.ationet.androidterminal.core.domain.receipt.ReceiptPrinter
import com.ationet.androidterminal.standalone.clear_pending.domain.receipt.ClearPendingPrinter
import com.ationet.androidterminal.standalone.clear_pending.domain.receipt.ClearPendingReceiptPrinter
import com.ationet.androidterminal.standalone.completion.domain.receipt.CompletionPrinter
import com.ationet.androidterminal.standalone.completion.domain.receipt.CompletionReceiptPrinter
import com.ationet.androidterminal.standalone.preauthorization.domain.receipt.PreAuthorizationPrinter
import com.ationet.androidterminal.standalone.preauthorization.domain.receipt.PreAuthorizationReceiptPrinter
import com.ationet.androidterminal.standalone.receipts.domain.receipts.StandAloneReceiptPrinter
import com.ationet.androidterminal.standalone.sale.domain.SaleReceiptPrinter
import com.ationet.androidterminal.standalone.sale.domain.use_case.SalePrinter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StandAloneModule {
    @Binds
    @Singleton
    @PreAuthorizationPrinter
    abstract fun bindPreAuthorizationPrinter(
        printer: PreAuthorizationReceiptPrinter
    ): ReceiptPrinter

    @Binds
    @Singleton
    @CompletionPrinter
    abstract fun bindCompletionPrinter(
        printer: CompletionReceiptPrinter
    ): ReceiptPrinter

    @Binds
    @Singleton
    @SalePrinter
    abstract fun bindSalePrinter(
        printer: SaleReceiptPrinter
    ): ReceiptPrinter

    @Binds
    @Singleton
    @ClearPendingPrinter
    abstract fun bindClearPendingPrinter(
        printer: ClearPendingReceiptPrinter
    ): ReceiptPrinter

    companion object {
        @Provides
        @ElementsIntoSet
        @StandAloneReceiptPrinter
        fun provideStandAlonePrinters(
            @PreAuthorizationPrinter preAuthPrinter: ReceiptPrinter,
            @CompletionPrinter completionPrinter: ReceiptPrinter,
            @SalePrinter salePrinter: ReceiptPrinter,
            @ClearPendingPrinter clearPendingPrinter: ReceiptPrinter
        ): Set<ReceiptPrinter> {
            return setOf(
                preAuthPrinter,
                completionPrinter,
                salePrinter,
                clearPendingPrinter
            )
        }
    }

//    @Binds
//    @Singleton
//    @ChangePinPrinter
//    abstract fun bindChangePinPrinter(
//        printer: ChangePinReceiptPrinter
//    ): ReceiptPrinter
}