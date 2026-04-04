package com.ationet.androidterminal.core.domain.use_case.ationet

import com.atio.log.Logger
import com.atio.log.util.error
import com.ationet.androidterminal.BuildConfig
import com.ationet.androidterminal.core.data.remote.ationet.model.DealerData
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestPreAuthorization @Inject constructor(
    private val configurationUseCase: ConfigurationUseCase,
    private val nativeInterfaceFactory: NativeInterfaceFactory,
    private val getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
    private val getInvoice: GetInvoice,
    private val deviceInfo: HALDeviceInfo
) {
    companion object {
        val logger = Logger("RequestPreAuthorization")
    }

    suspend operator fun invoke(
        authorizationCode: String? = null,
        pumpNumber: Int,
        productCode: String,
        unitPrice: Double,
        productAmount: Double? = null,
        productQuantity: Double? = null,
        primaryTrack: String,
        primaryPin: String? = null,
        secondaryTrack: String? = null,
        secondaryPin: String? = null,
        manualEntry: Boolean,
        customerData: Map<String, String?>? = null,
        localDateTime: LocalDateTime,
        unitCode: String,
        currencyCode: String,
        dealerData: DealerData? = null
    ): Result<NativeRequest?> {
        try {
            val configuration = configurationUseCase.getConfiguration()

            if (configuration.ationet.terminalId.isEmpty()) {
                throw TerminalIdNotConfiguredException()
            }

            val nativeInterface = nativeInterfaceFactory.getCurrentInterface()

            val nativeResponse = nativeInterface.requestPreAuthorization(
                terminalId = configuration.ationet.terminalId,
                systemModel = deviceInfo.model,
                systemVersion = BuildConfig.VERSION_NAME,
                authorizationCode = authorizationCode,
                pumpNumber = pumpNumber,
                transactionSequenceNumber = getNextTransactionSequenceNumber(),
                productCode = productCode,
                unitPrice = unitPrice,
                productAmount = productAmount,
                productQuantity = productQuantity,
                primaryTrack = primaryTrack,
                primaryPin = primaryPin,
                secondaryTrack = secondaryTrack,
                secondaryPin = secondaryPin,
                invoice = getInvoice(),
                manualEntry = manualEntry,
                customerData = customerData,
                localDateTime = localDateTime,
                unitCode = unitCode,
                currencyCode = currencyCode,
                dealerData = dealerData
            )

            return Result.success(nativeResponse)
        } catch (e: Throwable) {
            logger.error("Error requesting pre-authorization", e)
            return Result.failure(e)
        }
    }

    suspend operator fun invoke(
        authorizationCode: String? = null,
        productCode: String,
        unitPrice: Double,
        productAmount: Double? = null,
        productQuantity: Double? = null,
        primaryTrack: String,
        primaryPin: String? = null,
        secondaryTrack: String? = null,
        secondaryPin: String? = null,
        manualEntry: Boolean,
        customerData: Map<String, String?>? = null,
        localDateTime: LocalDateTime,
        unitCode: String,
        currencyCode: String,
        dealerData: DealerData? = null,
    ): Result<NativeRequest?> {
        try {
            val configuration = configurationUseCase.getConfiguration()

            if (configuration.ationet.terminalId.isEmpty()) {
                throw TerminalIdNotConfiguredException()
            }

            val nativeInterface = nativeInterfaceFactory.getCurrentInterface()

            val nativeResponse = nativeInterface.requestPreAuthorization(
                terminalId = configuration.ationet.terminalId,
                systemModel = deviceInfo.model,
                systemVersion = BuildConfig.VERSION_NAME,
                authorizationCode = authorizationCode,
                pumpNumber = 0,
                transactionSequenceNumber = getNextTransactionSequenceNumber(),
                productCode = productCode,
                unitPrice = unitPrice,
                productAmount = productAmount,
                productQuantity = productQuantity,
                primaryTrack = primaryTrack,
                primaryPin = primaryPin,
                secondaryTrack = secondaryTrack,
                secondaryPin = secondaryPin,
                invoice = getInvoice(),
                manualEntry = manualEntry,
                customerData = customerData,
                localDateTime = localDateTime,
                unitCode = unitCode,
                currencyCode = currencyCode,
                dealerData = dealerData
            )

            return Result.success(nativeResponse)
        } catch (e: Exception) {
            logger.error("Error requesting pre-authorization", e)
            return Result.failure(e)
        }
    }
}