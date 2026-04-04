package com.ationet.androidterminal.standalone.batch_close.domain.use_case

import com.atio.log.Logger
import com.atio.log.util.error
import com.atio.log.util.warn
import com.ationet.androidterminal.core.domain.receipt.PrinterStatus
import com.ationet.androidterminal.core.domain.receipt.ReceiptPrinter
import com.ationet.androidterminal.core.domain.util.PrinterErrorCodes
import com.ationet.androidterminal.standalone.batch_close.data.local.BatchCloseStateRepository
import com.ationet.androidterminal.standalone.batch_close.domain.receipt.BatchPrinter
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrintBatchCloseTicketUseCase @Inject constructor(
    @BatchPrinter private val receiptPrinter: ReceiptPrinter,
    private val operationStateRepository: BatchCloseStateRepository
) {

    companion object {
        private val logger = Logger("PrintBatchCloseTicketUseCase")
    }

    suspend operator fun invoke(isCopy: Boolean): PrintBatchCloseResult {

        val receipt = operationStateRepository.getState().receipt
        if (receipt == null) {
            logger.warn(message = "Batch close: receipt not found")

            return PrintBatchCloseResult.Error(
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

            logger.error(message = "Batch close printing failed", e)

            return PrintBatchCloseResult.Error(
                code = PrinterErrorCodes.ERROR
            )
        }

        return when (result) {
            PrinterStatus.Ok -> PrintBatchCloseResult.Ok
            PrinterStatus.OutOfPaper -> PrintBatchCloseResult.OutOfPaper
            is PrinterStatus.Error -> PrintBatchCloseResult.Error(
                code = result.errorCode
            )
        }
    }
}

sealed interface PrintBatchCloseResult {
    data object Ok : PrintBatchCloseResult
    data object OutOfPaper : PrintBatchCloseResult
    data class Error(
        val code: String
    ) : PrintBatchCloseResult
}