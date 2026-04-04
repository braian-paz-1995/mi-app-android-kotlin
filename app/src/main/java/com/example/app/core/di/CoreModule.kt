package com.ationet.androidterminal.core.di

import android.content.Context
import androidx.room.Room
import com.atio.log.domain.service.FileHandler
import com.atio.terminal_management.TerminalManagement
import com.atio.terminal_management.data.TerminalManagementModule
import com.ationet.androidterminal.core.data.local.ConfigurationRepositoryImpl
import com.ationet.androidterminal.core.data.local.ProductRepositoryImpl
import com.ationet.androidterminal.core.data.local.preauthorization.PreAuthorizationFusionRepository
import com.ationet.androidterminal.core.data.local.preauthorization.PreAuthorizationStandAloneRepository
import com.ationet.androidterminal.core.data.local.room.AATDatabase
import com.ationet.androidterminal.core.data.local.room.FusionPumpLockDao
import com.ationet.androidterminal.core.data.local.room.ReceiptDao
import com.ationet.androidterminal.core.data.local.room.TransactionDao
import com.ationet.androidterminal.core.data.local.room.fleet.CompletionDao
import com.ationet.androidterminal.core.data.local.room.fleet.PreAuthorizationDao
import com.ationet.androidterminal.core.data.local.room.fleet.SaleDao
import com.ationet.androidterminal.core.data.local.room.fleet.VoidDao
import com.ationet.androidterminal.core.data.local.room.loyalty.LoyaltyAccumulationDao
import com.ationet.androidterminal.core.data.local.room.loyalty.LoyaltyBalanceEnquiryDao
import com.ationet.androidterminal.core.data.local.room.loyalty.LoyaltyBatchDao
import com.ationet.androidterminal.core.data.local.room.loyalty.LoyaltyPointsRedemptionDao
import com.ationet.androidterminal.core.data.local.room.loyalty.LoyaltyRewardsRedemptionDao
import com.ationet.androidterminal.core.data.local.room.loyalty.LoyaltyVoidDao
import com.ationet.androidterminal.core.data.local.room.migration.MIGRATION_1_2
import com.ationet.androidterminal.core.data.local.room.task.BatchDao
import com.ationet.androidterminal.core.data.local.room.task.RechargeCCDao
import com.ationet.androidterminal.core.data.local.room.task.ReverseCCDao
import com.ationet.androidterminal.core.data.local.room.tm.ModulesCapabilityDao
import com.ationet.androidterminal.core.data.local.room.tm.PaymentMethodDao
import com.ationet.androidterminal.core.data.repository.BatchRepositoryImpl
import com.ationet.androidterminal.core.data.repository.FusionPumpLockRepositoryImpl
import com.ationet.androidterminal.core.data.repository.LoyaltyBatchRepositoryImpl
import com.ationet.androidterminal.core.data.repository.LoyaltyVoidRepositoryImpl
import com.ationet.androidterminal.core.data.repository.ModulesCapabilityRepositoryImpl
import com.ationet.androidterminal.core.data.repository.PaymentMethodRepositoryImpl
import com.ationet.androidterminal.core.data.repository.ReceiptRepositoryImpl
import com.ationet.androidterminal.core.data.repository.TransactionRepositoryImpl
import com.ationet.androidterminal.core.data.repository.VoidRepositoryImpl
import com.ationet.androidterminal.core.domain.model.preauthorization.PreAuthorizationFusion
import com.ationet.androidterminal.core.domain.model.preauthorization.PreAuthorizationStandalone
import com.ationet.androidterminal.core.domain.repository.BatchRepository
import com.ationet.androidterminal.core.domain.repository.CompletionRepository
import com.ationet.androidterminal.core.domain.repository.ConfigurationRepository
import com.ationet.androidterminal.core.domain.repository.FusionPumpLockRepository
import com.ationet.androidterminal.core.domain.repository.LoyaltyAccumulationRepository
import com.ationet.androidterminal.core.domain.repository.LoyaltyBalanceEnquiryRepository
import com.ationet.androidterminal.core.domain.repository.LoyaltyBatchRepository
import com.ationet.androidterminal.core.domain.repository.LoyaltyPointsRedemptionRepository
import com.ationet.androidterminal.core.domain.repository.LoyaltyRewardsRedemptionRepository
import com.ationet.androidterminal.core.domain.repository.LoyaltyVoidRepository
import com.ationet.androidterminal.core.domain.repository.ModulesCapabilityRepository
import com.ationet.androidterminal.core.domain.repository.PaymentMethodRepository
import com.ationet.androidterminal.core.domain.repository.PreAuthorizationRepository
import com.ationet.androidterminal.core.domain.repository.ProductRepository
import com.ationet.androidterminal.core.domain.repository.ReceiptRepository
import com.ationet.androidterminal.core.domain.repository.RechargeCCRepository
import com.ationet.androidterminal.core.domain.repository.ReverseCCRepository
import com.ationet.androidterminal.core.domain.repository.SaleRepository
import com.ationet.androidterminal.core.domain.repository.TransactionRepository
import com.ationet.androidterminal.core.domain.repository.VoidRepository
import com.ationet.androidterminal.core.domain.use_case.terminal_management.CreateFileHandlerUseCase
import com.ationet.androidterminal.loyalty.loyalty_accumulation.data.local.LoyaltyAccumulationRepositoryImpl
import com.ationet.androidterminal.loyalty.loyalty_balance_enquiry.data.local.LoyaltyBalanceEnquiryRepositoryImpl
import com.ationet.androidterminal.loyalty.loyalty_points_redemption.data.local.LoyaltyPointsRedemptionRepositoryImpl
import com.ationet.androidterminal.loyalty.loyalty_rewards_redemption.data.local.LoyaltyRewardsRedemptionRepositoryImpl
import com.ationet.androidterminal.standalone.completion.data.local.CompletionRepositoryImpl
import com.ationet.androidterminal.standalone.sale.data.repository.SaleRepositoryImpl
import com.ationet.androidterminal.task.rechargeCC.data.local.RechargeCCRepositoryImpl
import com.ationet.androidterminal.task.reverseCC.local.ReverseCCRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModule {

    @Binds
    @Singleton
    abstract fun bindConfigurationRepository(
        repository: ConfigurationRepositoryImpl
    ): ConfigurationRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        repository: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindPreAuthorizationFusionRepository(
        repository: PreAuthorizationFusionRepository
    ): PreAuthorizationRepository<PreAuthorizationFusion>

    @Binds
    @Singleton
    abstract fun bindPreAuthorizationStandAloneRepository(
        repository: PreAuthorizationStandAloneRepository
    ): PreAuthorizationRepository<PreAuthorizationStandalone>

    @Binds
    @Singleton
    abstract fun bindCompletionRepository(
        repository: CompletionRepositoryImpl
    ): CompletionRepository

    @Binds
    @Singleton
    abstract fun bindRechargeCCRepository(
        repository: RechargeCCRepositoryImpl
    ): RechargeCCRepository

    @Binds
    @Singleton
    abstract fun bindReverseCCRepository(
        repository: ReverseCCRepositoryImpl
    ): ReverseCCRepository

    @Binds
    @Singleton
    abstract fun bindSaleRepository(
        repository: SaleRepositoryImpl
    ): SaleRepository

    @Binds
    @Singleton
    abstract fun bindPaymentMethodRepository(
        repository: PaymentMethodRepositoryImpl
    ): PaymentMethodRepository

    @Binds
    @Singleton
    abstract fun bindModulesCapabilityRepository(
        repository: ModulesCapabilityRepositoryImpl
    ): ModulesCapabilityRepository

    @Binds
    @Singleton
    abstract fun bindVoidRepository(
        repository: VoidRepositoryImpl
    ): VoidRepository

    @Binds
    @Singleton
    abstract fun bindReceiptRepository(
        repository: ReceiptRepositoryImpl
    ): ReceiptRepository

    @Binds
    @Singleton
    abstract fun bindBatchRepository(
        repository: BatchRepositoryImpl
    ): BatchRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        repositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository

    /*Loyalty*/
    @Binds
    @Singleton
    abstract fun bindLoyaltyAccumulationRepository(
        repository: LoyaltyAccumulationRepositoryImpl
    ): LoyaltyAccumulationRepository

    @Binds
    @Singleton
    abstract fun bindLoyaltyBalanceEnquiryRepository(
        repository: LoyaltyBalanceEnquiryRepositoryImpl
    ): LoyaltyBalanceEnquiryRepository

    @Binds
    @Singleton
    abstract fun bindLoyaltyPointsRedemptionRepository(
        repository: LoyaltyPointsRedemptionRepositoryImpl
    ): LoyaltyPointsRedemptionRepository

    @Binds
    @Singleton
    abstract fun bindLoyaltyRewardsRedemptionRepository(
        repository: LoyaltyRewardsRedemptionRepositoryImpl
    ): LoyaltyRewardsRedemptionRepository

    @Binds
    @Singleton
    abstract fun bindLoyaltyVoidRepository(
        repository: LoyaltyVoidRepositoryImpl
    ): LoyaltyVoidRepository

    @Binds
    @Singleton
    abstract fun bindLoyaltyBatchRepository(
        repository: LoyaltyBatchRepositoryImpl
    ): LoyaltyBatchRepository

    @Binds
    @Singleton
    abstract fun bindFusionPumpLockRepository(
        repository: FusionPumpLockRepositoryImpl
    ): FusionPumpLockRepository

    companion object {
        @Provides
        @Singleton
        @IoDispatcher
        fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

        @Provides
        @Singleton
        @DefaultDispatcher
        fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Default

        @Provides
        @Singleton
        fun provideLogFileHandler(
            createFileHandlerUseCase: CreateFileHandlerUseCase
        ): FileHandler {
            return createFileHandlerUseCase.invoke()
        }

        @Provides
        @Singleton
        fun provideTerminalManagementModule(): TerminalManagement =
            TerminalManagementModule.instance

        @Provides
        @Singleton
        @ApplicationScope
        fun provideApplicationCoroutineScope(): CoroutineScope {
            return CoroutineScope(SupervisorJob() + Dispatchers.Default)
        }

        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context
        ): AATDatabase = Room.databaseBuilder(
            context = context,
            klass = AATDatabase::class.java,
            name = "aat_database.db"
        )
            .addMigrations(MIGRATION_1_2)
            .build()

        @Provides
        @Singleton
        fun providePreAuthorizationDao(
            database: AATDatabase
        ): PreAuthorizationDao = database.preAuthorizationDao

        @Provides
        @Singleton
        fun provideCompletionDao(
            database: AATDatabase,
        ): CompletionDao {
            return database.completionDao
        }

        @Provides
        @Singleton
        fun provideReceiptDao(
            database: AATDatabase
        ): ReceiptDao {
            return database.receiptDao
        }

        @Provides
        @Singleton
        fun provideSaleDao(
            database: AATDatabase
        ): SaleDao {
            return database.saleDao
        }

        /*Task*/
        @Provides
        @Singleton
        fun provideRechargeCCDao(
            database: AATDatabase
        ): RechargeCCDao {
            return database.rechargeCCDao
        }

        @Provides
        @Singleton
        fun provideReverseCCDao(
            database: AATDatabase
        ): ReverseCCDao {
            return database.reverseCCDao
        }

        @Provides
        @Singleton
        fun providePaymentMethodDao(
            database: AATDatabase
        ): PaymentMethodDao {
            return database.paymentMethodDao
        }

        @Provides
        @Singleton
        fun provideVoidDao(
            database: AATDatabase
        ): VoidDao {
            return database.voidDao
        }

        @Provides
        @Singleton
        fun provideBatchDao(
            database: AATDatabase
        ): BatchDao {
            return database.batchDao
        }

        @Provides
        @Singleton
        fun provideLoyaltyBatchDao(
            database: AATDatabase
        ): LoyaltyBatchDao {
            return database.loyaltyBatchDao
        }

        @Provides
        @Singleton
        fun provideModulesCapabilityDao(
            database: AATDatabase
        ): ModulesCapabilityDao {
            return database.modulesCapabilityDao
        }

        @Provides
        @Singleton
        fun provideTransactionDao(
            database: AATDatabase
        ): TransactionDao {
            return database.transactionDao
        }

        /*Loyalty*/
        @Provides
        @Singleton
        fun provideLoyaltyAccumulationDao(
            database: AATDatabase
        ): LoyaltyAccumulationDao {
            return database.loyaltyAccumulationDao
        }

        @Provides
        @Singleton
        fun provideLoyaltyBalanceEnquiryDao(
            database: AATDatabase
        ): LoyaltyBalanceEnquiryDao {
            return database.loyaltyBalanceEnquiryDao
        }

        @Provides
        @Singleton
        fun provideLoyaltyPointsRedemptionDao(
            database: AATDatabase
        ): LoyaltyPointsRedemptionDao {
            return database.loyaltyPointsRedemptionDao
        }

        @Provides
        @Singleton
        fun provideLoyaltyRewardsRedemptionDao(
            database: AATDatabase
        ): LoyaltyRewardsRedemptionDao {
            return database.loyaltyRewardsRedemptionDao
        }

        @Provides
        @Singleton
        fun provideLoyaltyVoidDao(
            database: AATDatabase
        ): LoyaltyVoidDao {
            return database.loyaltyVoidDao
        }

        @Provides
        @Singleton
        fun provideFusionPumpLockDao(
            database: AATDatabase
        ): FusionPumpLockDao {
            return database.fusionPumpLockDao
        }

    }
}
