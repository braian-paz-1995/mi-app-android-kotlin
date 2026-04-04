package com.ationet.androidterminal.standalone.void_transaction.domain.use_case

import com.atio.log.Logger
import com.atio.log.util.error
import com.atio.log.util.warn
import com.ationet.androidterminal.core.domain.receipt.PrinterStatus
import com.ationet.androidterminal.core.domain.receipt.ReceiptPrinter
import com.ationet.androidterminal.core.domain.util.PrinterErrorCodes
import com.ationet.androidterminal.standalone.void_transaction.data.local.VoidTransactionStateRepository
import com.ationet.androidterminal.standalone.void_transaction.domain.receipt.VoidPrinter
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrintVoidTransactionTicketUseCase @Inject constructor(
    @VoidPrinter private val receiptPrinter: ReceiptPrinter,
    private val operationStateRepository: VoidTransactionStateRepository
) {

    companion object {
        private val logger = Logger("PrintVoidTransactionTicketUseCase")
    }

    suspend operator fun invoke(isCopy: Boolean): PrintVoidTransactionResult {
        val receipt = operationStateRepository.getState().receipt
        if (receipt == null) {
            logger.warn(message = "Void transaction: receipt not found")

            return PrintVoidTransactionResult.Error(
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

            logger.error(message = "Void transaction printing failed", e)

            return PrintVoidTransactionResult.Error(
                code = PrinterErrorCodes.ERROR
            )
        }

        return when (result) {
            PrinterStatus.Ok -> PrintVoidTransactionResult.Ok
            PrinterStatus.OutOfPaper -> PrintVoidTransactionResult.OutOfPaper
            is PrinterStatus.Error -> PrintVoidTransactionResult.Error(
                code = result.errorCode
            )
        }
    }
}

sealed interface PrintVoidTransactionResult {
    data object Ok : PrintVoidTransactionResult
    data object OutOfPaper : PrintVoidTransactionResult
    data class Error(
        val code: String
    ) : PrintVoidTransactionResult
}