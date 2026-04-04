package com.ationet.androidterminal.standalone.batch_close.di

import android.content.Context
import com.ationet.androidterminal.core.domain.hal.printer.HALPrinter
import com.ationet.androidterminal.core.domain.model.preauthorization.PreAuthorizationStandalone
import com.ationet.androidterminal.core.domain.receipt.ReceiptPrinter
import com.ationet.androidterminal.core.domain.repository.OperationStateRepository
import com.ationet.androidterminal.core.domain.repository.PreAuthorizationRepository
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.standalone.batch_close.data.local.BatchCloseOperationState
import com.ationet.androidterminal.standalone.batch_close.data.local.BatchCloseStateRepository
import com.ationet.androidterminal.standalone.batch_close.domain.receipt.BatchPrinter
import com.ationet.androidterminal.standalone.batch_close.domain.receipt.BatchReceiptPrinter
import com.ationet.androidterminal.standalone.batch_close.domain.use_case.GetAllPreAuthorizationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BatchModule {
    @Provides
    @Singleton
    fun provideBatchCloseStateRepository(): OperationStateRepository<BatchCloseOperationState> {
        return BatchCloseStateRepository()
    }

    @Provides
    @Singleton
    @BatchPrinter
    fun provideVoidPrinter(
        @ApplicationContext context: Context,
        printer: HALPrinter,
        getConfiguration: GetConfiguration
    ): ReceiptPrinter {
        return BatchReceiptPrinter(
            context = context,
            printer = printer,
            getConfiguration = getConfiguration,
        )
    }

    @Provides
    @Singleton
    fun provideGetAllPreAuthorizationUseCase(
        preAuthorizationRepository: PreAuthorizationRepository<PreAuthorizationStandalone>
    ): GetAllPreAuthorizationUseCase {
        return GetAllPreAuthorizationUseCase(preAuthorizationRepository)
    }
}