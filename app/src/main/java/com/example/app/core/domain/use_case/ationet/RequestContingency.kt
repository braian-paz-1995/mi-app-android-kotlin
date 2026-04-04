package com.ationet.androidterminal.core.domain.use_case.ationet

import com.atio.log.Logger
import com.atio.log.util.info
import com.atio.log.util.warn
import com.ationet.androidterminal.BuildConfig
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.data.remote.ationet.model.ResponseCodes
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class RequestContingency(
    private val getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
    private val configurationUseCase: ConfigurationUseCase,
    private val nativeInterfaceFactory: NativeInterfaceFactory,
    private val deviceInfo: HALDeviceInfo
) {
    companion object {
        val logger = Logger("RequestContingencyUseCase")
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
        customerData: Map<String, String?>? = null,
    ): Result<NativeRequest?> {
        val configuration = configurationUseCase.getConfiguration()

        if (configuration.ationet.terminalId.isEmpty()) {
            throw TerminalIdNotConfiguredException()
        }

        val nativeInterface = nativeInterfaceFactory.getCurrentInterface()

        val tsn = getNextTransactionSequenceNumber.invoke()
        val terminalId = configuration.ationet.terminalId

        try {
            logger.info("Requesting contingency of authorization #$authorizationCode")
            val nativeResponse = nativeInterface.requestContingency(
                terminalId = terminalId,
                systemModel = deviceInfo.model,
                systemVersion = BuildConfig.VERSION_NAME,
                authorizationCode = authorizationCode,
                transactionSequenceNumber = tsn,
                pumpNumber = fuelPosition,
                productCode = productCode,
                productAmount = productAmount,
                productQuantity = productQuantity,
                unitPrice = unitPrice,
                transactionAmount = transactionAmount,
                invoice = invoice,
                primaryTrack = primaryTrack,
                manualEntry = false,
                customerData = customerData,
                localDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )

            if (nativeResponse?.responseCode == ResponseCodes.Authorized) {
                logger.info("Contingency of authorization #$authorizationCode approved")
            } else {
                logger.warn("Contingency of authorization #$authorizationCode rejected")
            }

            return Result.success(nativeResponse)
        } catch (e: Throwable) {
            logger.error(e) { append("Failed to request contingency") }
            return Result.failure(e)
        }
    }

    suspend operator fun invoke(
        authorizationCode: String,
        primaryTrack: String,
        invoice: String,
        fuelPosition: Int,
        productCode: String,
        customerData: Map<String, String?>? = null,
    ): Result<NativeRequest?> {
        return invoke(
            authorizationCode = authorizationCode,
            primaryTrack = primaryTrack,
            invoice = invoice,
            fuelPosition = fuelPosition,
            productCode = productCode,
            productAmount = 0.0,
            productQuantity = 0.0,
            unitPrice = 0.0,
            transactionAmount = 0.0,

            customerData = customerData
        )
    }
}