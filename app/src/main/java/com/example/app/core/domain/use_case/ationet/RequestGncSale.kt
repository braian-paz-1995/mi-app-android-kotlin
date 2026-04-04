package com.ationet.androidterminal.core.domain.use_case.ationet

import com.atio.log.Logger
import com.atio.log.util.error
import com.ationet.androidterminal.BuildConfig
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.data.remote.ationet.model.ProductData
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.hal.HALDeviceInfo
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import kotlinx.datetime.LocalDateTime

class RequestGncSale(
    private val configurationUseCase: ConfigurationUseCase,
    private val getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
    private val getInvoice: GetInvoice,
    private val nativeInterfaceFactory: NativeInterfaceFactory,
    private val deviceInfo: HALDeviceInfo
) {
    companion object {
        val logger = Logger("RequestGncSale")
    }

    suspend operator fun invoke(
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        pumpNumber: Int,
        productData: List<ProductData>,
        primaryTrack: String,
        primaryPin: String,
        secondaryPin: String?,
        secondaryTrack: String?,
        customerData: Map<String, String?>?
    ): Result<NativeRequest?> {
        try {
            val configuration = configurationUseCase.getConfiguration()

            if (configuration.ationet.terminalId.isEmpty()) {
                throw TerminalIdNotConfiguredException()
            }

            val nativeInterface = nativeInterfaceFactory.getCurrentInterface()

            val nativeResponse = nativeInterface.requestGncSale(
                terminalId = configuration.ationet.terminalId,
                systemModel = deviceInfo.model,
                systemVersion = BuildConfig.VERSION_NAME,
                transactionSequenceNumber = getNextTransactionSequenceNumber.invoke(),
                manualEntry = manualEntry,
                localDateTime = localDateTime,
                pumpNumber = pumpNumber,
                productData = productData,
                primaryTrack = primaryTrack,
                primaryPin = primaryPin,
                secondaryPin = secondaryPin,
                secondaryTrack = secondaryTrack,
                invoice = getInvoice.invoke(),
                customerData = customerData,
            )
            return Result.success(nativeResponse)
        } catch (e: Exception) {
            logger.error("Error requesting GNC sale", e)
            return Result.failure(e)
        }
    }
}