package com.ationet.androidterminal.core.domain.use_case.ationet

import com.atio.log.Logger
import com.atio.log.util.error
import com.ationet.androidterminal.BuildConfig
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import kotlinx.datetime.LocalDateTime

class RequestLoyaltyPointsRedemption(
    private val configurationUseCase: ConfigurationUseCase,
    private val getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
    private val nativeInterfaceFactory: NativeInterfaceFactory,
    private val deviceInfo: HALDeviceInfo
) {
    companion object {
        val logger = Logger("LoyaltyBalanceEnquiryUseCase")
    }

    suspend operator fun invoke(
        authorizationCode: String? = null,
        transactionSequenceNumber: Long,
        productCode: String,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        primaryTrack: String,
        unitCode: String?,
        currencyCode: String,
        productUnitPrice: Double,
        productAmount: Double? = null,
        productQuantity: Double? = null,
        username: String? = null,
        password: String? = null,
    ): Result<NativeRequest?> {
        try {
            val configuration = configurationUseCase.getConfiguration()
            if (configuration.ationet.terminalId.isEmpty()) {
                throw TerminalIdNotConfiguredException()
            }
            val tsn = getNextTransactionSequenceNumber.invoke()
            val nativeInterface = nativeInterfaceFactory.getCurrentInterface()

            val nativeResponse = unitCode?.let {
                nativeInterface.requestLoyaltyPointsRedemption(
                    terminalId = configuration.ationet.terminalId,
                    systemModel = deviceInfo.model,
                    systemVersion = BuildConfig.VERSION_NAME,
                    transactionSequenceNumber = tsn,
                    manualEntry = manualEntry,
                    localDateTime = localDateTime,
                    primaryTrack = primaryTrack,
                    productCode = productCode,
                    productUnitPrice = productUnitPrice,
                    productAmount = productAmount,
                    productQuantity = productQuantity,
                    unitCode = it,
                    currencyCode = currencyCode,
                    username = username,
                    password = password
                )
            }
            return Result.success(nativeResponse)
        } catch (e: Exception) {
            logger.error("Error requesting pin change for track $primaryTrack", e)
            return Result.failure(e)
        }
    }

    suspend operator fun invoke(
        transactionSequenceNumber: Long,
        productCode: String,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        primaryTrack: String,
        unitCode: String,
        currencyCode: String,
        productUnitPrice: Double,
        productAmount: Double? = null,
        productQuantity: Double? = null,
        username: String? = null,
        password: String? = null,
    ): Result<NativeRequest?> {

        try {
            val tsn = getNextTransactionSequenceNumber.invoke()
            val configuration = configurationUseCase.getConfiguration()

            if (configuration.ationet.terminalId.isEmpty()) {
                throw TerminalIdNotConfiguredException()
            }

            val nativeInterface = nativeInterfaceFactory.getCurrentInterface()

            val nativeResponse = nativeInterface.requestLoyaltyPointsRedemption(
                terminalId = configuration.ationet.terminalId,
                systemModel = deviceInfo.model,
                systemVersion = BuildConfig.VERSION_NAME,
                transactionSequenceNumber = tsn,
                manualEntry = manualEntry,
                localDateTime = localDateTime,
                primaryTrack = primaryTrack,
                productCode = productCode,
                productUnitPrice = productUnitPrice,
                productAmount = productAmount,
                productQuantity = productQuantity,
                unitCode = unitCode,
                currencyCode = currencyCode,
                username = username,
                password = password
            )

            return Result.success(nativeResponse)
        } catch (e: Exception) {
            RequestPreAuthorization.logger.error("Error requesting pre-authorization", e)
            return Result.failure(e)
        }
    }
}