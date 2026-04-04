package com.ationet.androidterminal.core.domain.use_case.ationet

import com.atio.log.Logger
import com.atio.log.util.error
import com.ationet.androidterminal.BuildConfig
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import kotlinx.datetime.LocalDateTime

class RequestPostpaidSale(
    private val configurationUseCase: ConfigurationUseCase,
    private val getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
    private val nativeInterfaceFactory: NativeInterfaceFactory,
    private val deviceInfo: HALDeviceInfo,
    private val getInvoice: GetInvoice
) {
    companion object {
        val logger = Logger("GeneratePostpaidSaleUseCase")
    }

    suspend operator fun invoke(
        authorizationCode: String? = null,
        manualEntry: Boolean = false,
        localDateTime: LocalDateTime,
        pumpNumber: Int,
        productCode: String,
        productAmount: Double,
        productQuantity: Double,
        unitPrice: Double,
        transactionAmount: Double,
        primaryTrack: String,
        primaryPin: String?,
        secondaryPin: String?,
        secondaryTrack: String?,
        customerData: Map<String, String?>?,
        operationType: Boolean
    ): Result<NativeRequest?> {
        try {
            val configuration = configurationUseCase.getConfiguration()

            if (configuration.ationet.terminalId.isEmpty()) {
                throw TerminalIdNotConfiguredException()
            }

            val nativeInterface = nativeInterfaceFactory.getCurrentInterface()

            val nativeResponse = nativeInterface.requestPostpaidSale(
                terminalId = configuration.ationet.terminalId,
                systemModel = deviceInfo.model,
                systemVersion = BuildConfig.VERSION_NAME,
                authorizationCode = authorizationCode,
                transactionSequenceNumber = getNextTransactionSequenceNumber(),
                manualEntry = manualEntry,
                localDateTime = localDateTime,
                pumpNumber = pumpNumber,
                productCode = productCode,
                productAmount = productAmount,
                productQuantity = productQuantity,
                unitPrice = unitPrice,
                transactionAmount = transactionAmount,
                primaryTrack = primaryTrack,
                primaryPin = primaryPin,
                secondaryPin = secondaryPin,
                secondaryTrack = secondaryTrack,
                invoice = getInvoice(),
                customerData = customerData,
                currencyCode = configuration.currencyCode,
                operationType = operationType,
            )
            return Result.success(nativeResponse)
        } catch (e: Exception) {
            logger.error("Error requesting postpaid sale", e)
            return Result.failure(e)
        }
    }
}