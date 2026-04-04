package com.ationet.androidterminal.core.domain.use_case.ationet

import com.atio.log.Logger
import com.atio.log.util.info
import com.atio.log.util.warn
import com.ationet.androidterminal.BuildConfig
import com.ationet.androidterminal.core.data.remote.ationet.model.DealerData
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.data.remote.ationet.model.OriginalData
import com.ationet.androidterminal.core.data.remote.ationet.model.ResponseCodes
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.datetime.LocalDateTime

class RequestCompletion(
    private val configurationUseCase: ConfigurationUseCase,
    private val getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
    private val nativeInterfaceFactory: NativeInterfaceFactory,
    private val deviceInfo: HALDeviceInfo
) {
    companion object {
        val logger = Logger("RequestCompletionUseCase")
    }

    suspend operator fun invoke(
        authorizationCode: String,
        primaryTrack: String,
        invoice: String,
        fuelPosition: Int,
        productCode: String,
        productAmount: Double,
        productQuantity: Double,
        unitPrice: Double,
        transactionAmount: Double,
        transactionDateTime: LocalDateTime,
        primaryPin: String? = null,
        secondaryTrack: String? = null,
        secondaryPin: String? = null,
        customerData: Map<String, String?>? = null,
        originalData: OriginalData? = null,
        dealerData: DealerData? = null
    ): Result<NativeRequest?> {
        try {
            val configuration = configurationUseCase.getConfiguration()

            if (configuration.ationet.terminalId.isEmpty()) {
                throw TerminalIdNotConfiguredException()
            }

            val nativeInterface = nativeInterfaceFactory.getCurrentInterface()

            logger.info("Requesting completion of authorization #$authorizationCode")
            val nativeResponse = nativeInterface.requestCompletion(
                terminalId = configuration.ationet.terminalId,
                systemModel = deviceInfo.model,
                systemVersion = BuildConfig.VERSION_NAME,
                authorizationCode = authorizationCode,
                transactionSequenceNumber = getNextTransactionSequenceNumber.invoke(),
                pumpNumber = fuelPosition,
                productCode = productCode,
                productAmount = productAmount,
                productQuantity = productQuantity,
                unitPrice = unitPrice,
                transactionAmount = transactionAmount,
                invoice = invoice,
                primaryTrack = primaryTrack,
                primaryPin = primaryPin,
                secondaryTrack = secondaryTrack,
                secondaryPin = secondaryPin,
                manualEntry = false,
                customerData = customerData,
                localDateTime = transactionDateTime,
                originalData = originalData,
                currencyCode = configuration.currencyCode,
                dealerData = dealerData
            )

            if (nativeResponse?.responseCode == ResponseCodes.Authorized) {
                logger.info("Authorization #$authorizationCode completed")
            } else {
                logger.warn("Authorization #$authorizationCode rejected")
            }

            return Result.success(nativeResponse)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            return Result.failure(e)
        }
    }
}