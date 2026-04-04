package com.ationet.androidterminal.core.domain.use_case.ationet

import com.ationet.androidterminal.BuildConfig
import com.ationet.androidterminal.core.data.remote.ationet.model.DealerData
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestSale @Inject constructor(
    private val configurationUseCase: ConfigurationUseCase,
    private val getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
    private val getInvoice: GetInvoice,
    private val nativeInterfaceFactory: NativeInterfaceFactory,
    private val deviceInfo: HALDeviceInfo
) {
    suspend operator fun invoke(
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
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
        dealerData: DealerData?,
        operationType: Boolean,
    ): Result<NativeRequest?> {
        try {
            val configuration = configurationUseCase.getConfiguration()

            if (configuration.ationet.terminalId.isEmpty()) {
                throw TerminalIdNotConfiguredException()
            }

            val nativeInterface = nativeInterfaceFactory.getCurrentInterface()

            val nativeResponse = nativeInterface.requestSale(
                terminalId = configuration.ationet.terminalId,
                systemModel = deviceInfo.model,
                systemVersion = BuildConfig.VERSION_NAME,
                transactionSequenceNumber = getNextTransactionSequenceNumber.invoke(),
                manualEntry = manualEntry,
                localDateTime = localDateTime,
                productCode = productCode,
                productAmount = productAmount,
                productQuantity = productQuantity,
                transactionAmount = transactionAmount,
                unitPrice = unitPrice,
                primaryTrack = primaryTrack,
                primaryPin = primaryPin.orEmpty(),
                secondaryPin = secondaryPin,
                secondaryTrack = secondaryTrack,
                invoice = getInvoice.invoke(),
                customerData = customerData,
                currencyCode = configuration.currencyCode,
                dealerData = dealerData,
                operationType = operationType
            )
            return Result.success(nativeResponse)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()

            return Result.failure(e)
        }
    }
}