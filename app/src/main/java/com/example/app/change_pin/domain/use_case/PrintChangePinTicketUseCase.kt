package com.ationet.androidterminal.core.change_pin.domain.use_case

import com.atio.log.Logger
import com.atio.log.util.error
import com.atio.log.util.warn
import com.ationet.androidterminal.core.change_pin.data.local.ChangePinOperationStateRepository
import com.ationet.androidterminal.core.change_pin.domain.receipt.ChangePinPrinter
import com.ationet.androidterminal.core.domain.receipt.PrinterStatus
import com.ationet.androidterminal.core.domain.receipt.ReceiptPrinter
import com.ationet.androidterminal.core.domain.util.PrinterErrorCodes
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrintChangePinTicketUseCase @Inject constructor(
    @ChangePinPrinter private val receiptPrinter: ReceiptPrinter,
    private val operationStateRepository: ChangePinOperationStateRepository
) {

    companion object {
        private val logger = Logger("PrintChangePinTicketUseCase")
    }

    suspend operator fun invoke(isCopy: Boolean): PrintChangePinResult {
        val receipt = operationStateRepository.getState().receipt
        if (receipt == null) {
            logger.warn(message = "Change Pin: receipt not found")

            return PrintChangePinResult.Error(
                code = PrinterErrorCodes.ERROR
            )
        }

        val printableReceipt = receipt.copy(
            copy = isCopy
        )

        val result = try {
            receiptPrinter.printReceipt(printableReceipt)
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            logger.error(message = "Change Pin printing failed", e)

            return PrintChangePinResult.Error(
                code = PrinterErrorCodes.ERROR
            )
        }

        return when (result) {
            PrinterStatus.Ok -> PrintChangePinResult.Ok
            PrinterStatus.OutOfPaper -> PrintChangePinResult.OutOfPaper
            is PrinterStatus.Error -> PrintChangePinResult.Error(
                code = result.errorCode
            )
        }
    }
}

sealed interface PrintChangePinResult {
    data object Ok : PrintChangePinResult
    data object OutOfPaper : PrintChangePinResult
    data class Error(
        val code: String
    ) : PrintChangePinResult
}