package com.ationet.androidterminal.core.di

import android.content.Context
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.ationet.androidterminal.core.domain.repository.BatchRepository
import com.ationet.androidterminal.core.domain.repository.ConfigurationRepository
import com.ationet.androidterminal.core.domain.repository.LoyaltyBatchRepository
import com.ationet.androidterminal.core.domain.repository.ProductRepository
import com.ationet.androidterminal.core.domain.use_case.ationet.GetInvoice
import com.ationet.androidterminal.core.domain.use_case.ationet.GetNextTransactionSequenceNumber
import com.ationet.androidterminal.core.domain.use_case.ationet.NativeInterfaceFactory
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestActiveGC
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestBalanceInquiry
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestBatchClose
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestCancellation
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestChangePin
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestCompletion
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestContingency
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestGncSale
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestLoyaltyAccumulation
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestLoyaltyBalanceEnquiry
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestLoyaltyBatchClose
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestLoyaltyPointsRedemption
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestLoyaltyRewardsRedemption
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestLubeSale
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestModules
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestPostpaidSale
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestPreAuthorization
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestRechargeCC
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestReverseCC
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestShopSale
import com.ationet.androidterminal.core.domain.use_case.batch.CloseBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.batch.GetLastOpenBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.batch.GetTransactionsRechargeCCWithBatchIdUseCase
import com.ationet.androidterminal.core.domain.use_case.batch.GetTransactionsReverseCCWithBatchIdUseCase
import com.ationet.androidterminal.core.domain.use_case.batch.GetTransactionsWithBatchIdUseCase
import com.ationet.androidterminal.core.domain.use_case.batch.GetVoidTransactionsWithBatchIdUseCase
import com.ationet.androidterminal.core.domain.use_case.batch.OpenBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.core.domain.use_case.configuration.UpdateConfiguration
import com.ationet.androidterminal.core.domain.use_case.loyalty_batch.CloseLoyaltyBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.loyalty_batch.GetLastOpenLoyaltyBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.loyalty_batch.GetTransactionsRedemptionPointsWithLoyaltyBatchIdUseCase
import com.ationet.androidterminal.core.domain.use_case.loyalty_batch.GetTransactionsRedemptionRewardsWithLoyaltyBatchIdUseCase
import com.ationet.androidterminal.core.domain.use_case.loyalty_batch.GetTransactionsWithLoyaltyBatchIdUseCase
import com.ationet.androidterminal.core.domain.use_case.loyalty_batch.GetVoidTransactionsWithLoyaltyBatchIdUseCase
import com.ationet.androidterminal.core.domain.use_case.loyalty_batch.OpenLoyaltyBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.product.CreateProduct
import com.ationet.androidterminal.core.domain.use_case.product.DeleteProduct
import com.ationet.androidterminal.core.domain.use_case.product.GetAllProducts
import com.ationet.androidterminal.core.domain.use_case.product.GetProduct
import com.ationet.androidterminal.core.domain.use_case.product.ProductUseCase
import com.ationet.androidterminal.core.domain.use_case.product.UpdateOrderProducts
import com.ationet.androidterminal.core.domain.use_case.product.UpdateProduct
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {
    companion object {
        @Provides
        @Singleton
        fun provideGetInvoiceUseCase(
            @ApplicationContext context: Context
        ): GetInvoice {
            return GetInvoice(context)
        }

        @Provides
        @Singleton
        fun provideGetNextTransactionSequenceNumberUseCase(
            @ApplicationContext context: Context
        ): GetNextTransactionSequenceNumber {
            return GetNextTransactionSequenceNumber(context)
        }

        @Provides
        @Singleton
        fun provideRequestBalanceInquiryUseCase(
            getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
            configurationUseCase: ConfigurationUseCase,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo
        ): RequestBalanceInquiry {
            return RequestBalanceInquiry(
                configurationUseCase = configurationUseCase,
                getNextTransactionSequenceNumber = getNextTransactionSequenceNumber,
                nativeInterfaceFactory = nativeInterfaceFactory,
                deviceInfo = deviceInfo
            )
        }

        @Provides
        @Singleton
        fun provideRequestCancellationUseCase(
            configurationUseCase: ConfigurationUseCase,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo
        ): RequestCancellation {
            return RequestCancellation(
                configurationUseCase = configurationUseCase,
                nativeInterfaceFactory = nativeInterfaceFactory,
                deviceInfo = deviceInfo
            )
        }

        @Provides
        @Singleton
        fun provideRequestBatchCloseUseCase(
            configurationUseCase: ConfigurationUseCase,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo
        ): RequestBatchClose {
            return RequestBatchClose(
                nativeInterfaceFactory = nativeInterfaceFactory,
                getConfigurationUseCase = configurationUseCase,
                deviceInfo = deviceInfo
            )
        }

        @Provides
        @Singleton
        fun provideRequestModulesUseCase(
            configurationUseCase: ConfigurationUseCase,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo
        ): RequestModules {
            return RequestModules(
                nativeInterfaceFactory = nativeInterfaceFactory,
                getConfigurationUseCase = configurationUseCase,
                deviceInfo = deviceInfo
            )
        }


        @Provides
        @Singleton
        fun provideRequestChangePinUseCase(
            configurationUseCase: ConfigurationUseCase,
            getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo
        ): RequestChangePin {
            return RequestChangePin(
                configurationUseCase = configurationUseCase,
                getNextTransactionSequenceNumber = getNextTransactionSequenceNumber,
                nativeInterfaceFactory = nativeInterfaceFactory,
                deviceInfo = deviceInfo
            )
        }

        @Provides
        @Singleton
        fun provideRequestActiveGCUseCase(
            configurationUseCase: ConfigurationUseCase,
            getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo
        ): RequestActiveGC {
            return RequestActiveGC(
                configurationUseCase = configurationUseCase,
                getNextTransactionSequenceNumber = getNextTransactionSequenceNumber,
                nativeInterfaceFactory = nativeInterfaceFactory,
                deviceInfo = deviceInfo
            )
        }

        @Provides
        @Singleton
        fun provideRequestRechargeCCUseCase(
            configurationUseCase: ConfigurationUseCase,
            getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo
        ): RequestRechargeCC {
            return RequestRechargeCC(
                configurationUseCase = configurationUseCase,
                getNextTransactionSequenceNumber = getNextTransactionSequenceNumber,
                nativeInterfaceFactory = nativeInterfaceFactory,
                deviceInfo = deviceInfo
            )
        }

        @Provides
        @Singleton
        fun provideRequestReverseCCUseCase(
            configurationUseCase: ConfigurationUseCase,
            getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo
        ): RequestReverseCC {
            return RequestReverseCC(
                configurationUseCase = configurationUseCase,
                getNextTransactionSequenceNumber = getNextTransactionSequenceNumber,
                nativeInterfaceFactory = nativeInterfaceFactory,
                deviceInfo = deviceInfo
            )
        }

        @Provides
        @Singleton
        fun provideRequestCompletionUseCase(
            configurationUseCase: ConfigurationUseCase,
            getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo
        ): RequestCompletion {
            return RequestCompletion(
                configurationUseCase = configurationUseCase,
                getNextTransactionSequenceNumber = getNextTransactionSequenceNumber,
                nativeInterfaceFactory = nativeInterfaceFactory,
                deviceInfo = deviceInfo
            )
        }

        @Provides
        @Singleton
        fun provideRequestContingencyUseCase(
            configurationUseCase: ConfigurationUseCase,
            getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo
        ): RequestContingency {
            return RequestContingency(
                configurationUseCase = configurationUseCase,
                getNextTransactionSequenceNumber = getNextTransactionSequenceNumber,
                nativeInterfaceFactory = nativeInterfaceFactory,
                deviceInfo = deviceInfo
            )
        }

        @Provides
        @Singleton
        fun provideRequestGncSalesUseCase(
            configurationUseCase: ConfigurationUseCase,
            getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
            getInvoiceUseCase: GetInvoice,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo
        ): RequestGncSale {
            return RequestGncSale(
                configurationUseCase = configurationUseCase,
                getNextTransactionSequenceNumber = getNextTransactionSequenceNumber,
                getInvoice = getInvoiceUseCase,
                nativeInterfaceFactory = nativeInterfaceFactory,
                deviceInfo = deviceInfo
            )
        }

        @Provides
        @Singleton
        fun provideRequestLubeSaleUseCase(
            configurationUseCase: ConfigurationUseCase,
            getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
            getInvoiceUseCase: GetInvoice,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo
        ): RequestLubeSale {
            return RequestLubeSale(
                configurationUseCase = configurationUseCase,
                getNextTransactionSequenceNumber = getNextTransactionSequenceNumber,
                getInvoice = getInvoiceUseCase,
                nativeInterfaceFactory = nativeInterfaceFactory,
                deviceInfo = deviceInfo
            )
        }

        @Provides
        @Singleton
        fun provideRequestPostpaidSaleUseCase(
            configurationUseCase: ConfigurationUseCase,
            getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo,
            getInvoiceUseCase: GetInvoice
        ): RequestPostpaidSale {
            return RequestPostpaidSale(
                configurationUseCase = configurationUseCase,
                getNextTransactionSequenceNumber = getNextTransactionSequenceNumber,
                nativeInterfaceFactory = nativeInterfaceFactory,
                getInvoice = getInvoiceUseCase,
                deviceInfo = deviceInfo
            )
        }

        @Provides
        @Singleton
        fun provideRequestPreAuthorizationUseCase(
            configurationUseCase: ConfigurationUseCase,
            getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
            getInvoiceUseCase: GetInvoice,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo
        ): RequestPreAuthorization {
            return RequestPreAuthorization(
                configurationUseCase = configurationUseCase,
                getNextTransactionSequenceNumber = getNextTransactionSequenceNumber,
                getInvoice = getInvoiceUseCase,
                nativeInterfaceFactory = nativeInterfaceFactory,
                deviceInfo = deviceInfo
            )
        }

        @Provides
        @Singleton
        fun provideRequestShopSaleUseCase(
            configurationUseCase: ConfigurationUseCase,
            getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
            getInvoiceUseCase: GetInvoice,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo
        ): RequestShopSale {
            return RequestShopSale(
                configurationUseCase = configurationUseCase,
                getNextTransactionSequenceNumber = getNextTransactionSequenceNumber,
                getInvoice = getInvoiceUseCase,
                nativeInterfaceFactory = nativeInterfaceFactory,
                deviceInfo = deviceInfo
            )
        }

        /*loyalty*/
        @Provides
        @Singleton
        fun provideRequestLoyaltyAccumulationUseCase(
            configurationUseCase: ConfigurationUseCase,
            getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo
        ): RequestLoyaltyAccumulation {
            return RequestLoyaltyAccumulation(
                configurationUseCase = configurationUseCase,
                getNextTransactionSequenceNumber = getNextTransactionSequenceNumber,
                nativeInterfaceFactory = nativeInterfaceFactory,
                deviceInfo = deviceInfo
            )
        }

        @Provides
        @Singleton
        fun provideRequestLoyaltyBalanceEnquiryUseCase(
            configurationUseCase: ConfigurationUseCase,
            getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo
        ): RequestLoyaltyBalanceEnquiry {
            return RequestLoyaltyBalanceEnquiry(
                configurationUseCase = configurationUseCase,
                getNextTransactionSequenceNumber = getNextTransactionSequenceNumber,
                nativeInterfaceFactory = nativeInterfaceFactory,
                deviceInfo = deviceInfo
            )
        }

        @Provides
        @Singleton
        fun provideRequestLoyaltyPointsRedemptionUseCase(
            configurationUseCase: ConfigurationUseCase,
            getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo
        ): RequestLoyaltyPointsRedemption {
            return RequestLoyaltyPointsRedemption(
                configurationUseCase = configurationUseCase,
                getNextTransactionSequenceNumber = getNextTransactionSequenceNumber,
                nativeInterfaceFactory = nativeInterfaceFactory,
                deviceInfo = deviceInfo
            )
        }

        @Provides
        @Singleton
        fun provideRequestLoyaltyRewardsRedemptionUseCase(
            configurationUseCase: ConfigurationUseCase,
            getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo
        ): RequestLoyaltyRewardsRedemption {
            return RequestLoyaltyRewardsRedemption(
                configurationUseCase = configurationUseCase,
                getNextTransactionSequenceNumber = getNextTransactionSequenceNumber,
                nativeInterfaceFactory = nativeInterfaceFactory,
                deviceInfo = deviceInfo
            )
        }

        @Provides
        @Singleton
        fun provideRequestLoyaltyBatchCloseUseCase(
            configurationUseCase: ConfigurationUseCase,
            nativeInterfaceFactory: NativeInterfaceFactory,
            deviceInfo: HALDeviceInfo
        ): RequestLoyaltyBatchClose {
            return RequestLoyaltyBatchClose(
                nativeInterfaceFactory = nativeInterfaceFactory,
                getConfigurationUseCase = configurationUseCase,
                deviceInfo = deviceInfo
            )
        }

        @Provides
        @Singleton
        fun provideOpenLoyaltyBatchUseCase(
            loyaltyBatchRepository: LoyaltyBatchRepository
        ): OpenLoyaltyBatchUseCase {
            return OpenLoyaltyBatchUseCase(loyaltyBatchRepository)
        }

        @Provides
        @Singleton
        fun provideCloseLoyaltyBatchUseCase(
            loyaltyBatchRepository: LoyaltyBatchRepository
        ): CloseLoyaltyBatchUseCase {
            return CloseLoyaltyBatchUseCase(loyaltyBatchRepository)
        }

        @Provides
        @Singleton
        fun provideGetLastOpenLoyaltyBatchUseCase(
            loyaltyBatchRepository: LoyaltyBatchRepository
        ): GetLastOpenLoyaltyBatchUseCase {
            return GetLastOpenLoyaltyBatchUseCase(loyaltyBatchRepository)
        }

        @Provides
        @Singleton
        fun provideGetTransactionsWithLoyaltyBatchIdUseCase(
            loyaltyBatchRepository: LoyaltyBatchRepository
        ): GetTransactionsWithLoyaltyBatchIdUseCase {
            return GetTransactionsWithLoyaltyBatchIdUseCase(loyaltyBatchRepository)
        }

        @Provides
        @Singleton
        fun provideGetTransactionsRechargeCCWithLoyaltyBatchIdUseCase(
            loyaltyBatchRepository: LoyaltyBatchRepository
        ): GetTransactionsRedemptionPointsWithLoyaltyBatchIdUseCase {
            return GetTransactionsRedemptionPointsWithLoyaltyBatchIdUseCase(loyaltyBatchRepository)
        }

        @Provides
        @Singleton
        fun provideGetTransactionsReverseCCWithLoyaltyBatchIdUseCase(
            loyaltyBatchRepository: LoyaltyBatchRepository
        ): GetTransactionsRedemptionRewardsWithLoyaltyBatchIdUseCase {
            return GetTransactionsRedemptionRewardsWithLoyaltyBatchIdUseCase(loyaltyBatchRepository)
        }

        @Provides
        @Singleton
        fun provideGetVoidTransactionsWithLoyaltyBatchIdUseCase(
            loyaltyBatchRepository: LoyaltyBatchRepository
        ): GetVoidTransactionsWithLoyaltyBatchIdUseCase {
            return GetVoidTransactionsWithLoyaltyBatchIdUseCase(loyaltyBatchRepository)
        }

        /*Fin Loyalty*/
        @Provides
        @Singleton
        fun provideGetConfigurationUseCase(
            configurationRepository: ConfigurationRepository
        ): GetConfiguration {
            return GetConfiguration(configurationRepository)
        }

        @Provides
        @Singleton
        fun provideUpdateConfigurationUseCase(
            @ApplicationContext context: Context,
            configurationRepository: ConfigurationRepository
        ): UpdateConfiguration {
            return UpdateConfiguration(context, configurationRepository)
        }

        @Provides
        @Singleton
        fun provideConfigurationUseCase(
            getConfiguration: GetConfiguration,
            updateConfiguration: UpdateConfiguration
        ): ConfigurationUseCase {
            return ConfigurationUseCase(getConfiguration, updateConfiguration)
        }

        @Provides
        @Singleton
        fun provideGetProductUseCase(
            productRepository: ProductRepository
        ): GetProduct {
            return GetProduct(productRepository)
        }

        @Provides
        @Singleton
        fun provideUpdateProductUseCase(
            productRepository: ProductRepository
        ): UpdateProduct {
            return UpdateProduct(productRepository)
        }

        @Provides
        @Singleton
        fun provideCreateProductUseCase(
            productRepository: ProductRepository
        ): CreateProduct {
            return CreateProduct(productRepository)
        }

        @Provides
        @Singleton
        fun provideDeleteProductUseCase(
            productRepository: ProductRepository
        ): DeleteProduct {
            return DeleteProduct(productRepository)
        }

        @Provides
        @Singleton
        fun provideGetAllProductsUseCase(
            productRepository: ProductRepository
        ): GetAllProducts {
            return GetAllProducts(productRepository)
        }

        @Provides
        @Singleton
        fun provideUpdateOrderProductsUseCase(
            productRepository: ProductRepository
        ): UpdateOrderProducts {
            return UpdateOrderProducts(productRepository)
        }

        @Provides
        @Singleton
        fun provideProductUseCase(
            getProduct: GetProduct,
            updateProduct: UpdateProduct,
            createProduct: CreateProduct,
            deleteProduct: DeleteProduct,
            getAllProducts: GetAllProducts,
            updateOrderProducts: UpdateOrderProducts
        ): ProductUseCase {
            return ProductUseCase(
                createProduct,
                deleteProduct,
                updateProduct,
                getProduct,
                getAllProducts,
                updateOrderProducts
            )
        }

        @Provides
        @Singleton
        fun provideOpenBatchUseCase(
            batchRepository: BatchRepository
        ): OpenBatchUseCase {
            return OpenBatchUseCase(batchRepository)
        }

        @Provides
        @Singleton
        fun provideCloseBatchUseCase(
            batchRepository: BatchRepository
        ): CloseBatchUseCase {
            return CloseBatchUseCase(batchRepository)
        }

        @Provides
        @Singleton
        fun provideGetLastOpenBatchUseCase(
            batchRepository: BatchRepository
        ): GetLastOpenBatchUseCase {
            return GetLastOpenBatchUseCase(batchRepository)
        }

        @Provides
        @Singleton
        fun provideGetTransactionsWithBatchIdUseCase(
            batchRepository: BatchRepository
        ): GetTransactionsWithBatchIdUseCase {
            return GetTransactionsWithBatchIdUseCase(batchRepository)
        }

        @Provides
        @Singleton
        fun provideGetTransactionsRechargeCCWithBatchIdUseCase(
            batchRepository: BatchRepository
        ): GetTransactionsRechargeCCWithBatchIdUseCase {
            return GetTransactionsRechargeCCWithBatchIdUseCase(batchRepository)
        }

        @Provides
        @Singleton
        fun provideGetTransactionsReverseCCWithBatchIdUseCase(
            batchRepository: BatchRepository
        ): GetTransactionsReverseCCWithBatchIdUseCase {
            return GetTransactionsReverseCCWithBatchIdUseCase(batchRepository)
        }

        @Provides
        @Singleton
        fun provideGetVoidTransactionsWithBatchIdUseCase(
            batchRepository: BatchRepository
        ): GetVoidTransactionsWithBatchIdUseCase {
            return GetVoidTransactionsWithBatchIdUseCase(batchRepository)
        }

    }
}