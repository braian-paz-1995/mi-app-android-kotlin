package com.ationet.androidterminal.standalone.receipts.domain.use_cases

import android.util.Log
import com.ationet.androidterminal.core.domain.receipt.PrinterStatus
import com.ationet.androidterminal.core.domain.repository.ReceiptRepository
import com.ationet.androidterminal.core.domain.util.PrinterErrorCodes
import com.ationet.androidterminal.standalone.receipts.domain.receipts.ReceiptPrinterFactory
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrintReceiptCopyUseCase @Inject constructor(
    private val receiptRepository: ReceiptRepository,
    private val receiptPrinterFactory: ReceiptPrinterFactory,
) {
    suspend operator fun invoke(receiptId: Int): PrintReceiptResult {
        val receipt = try {
            receiptRepository.getReceipt(receiptId)
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            Log.e(TAG, "Receipt printer: failed to get receipt #$receiptId", e)

            return PrintReceiptResult.Error(
                code = PrinterErrorCodes.TRANS_NOT_FOUND
            )
        } ?: run {
            Log.w(TAG, "Receipt printer: receipt #$receiptId not found")

            return PrintReceiptResult.Error(
                code = PrinterErrorCodes.TRANS_NOT_FOUND
            )
        }

        val copyReceipt = if (receipt.copy) receipt else try {
            val copy = receipt.copy(
                copy = true
            )

            receiptRepository.saveReceipt(receipt)

            Log.i(TAG, "Receipt Printer: receipt #$receiptId is now a copy")
            copy
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            Log.e(TAG, "Receipt printer: receipt #$receiptId copy failed", e)
            return PrintReceiptResult.Error(
                code = PrinterErrorCodes.TRANS_NOT_FOUND
            )
        }

        val receiptPrinter = try {
            receiptPrinterFactory.getReceiptPrinterTransactionName(copyReceipt.transactionLine.name)
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            Log.e(TAG, "Receipt printer: failed to get receipt #$receiptId printer", e)

            return PrintReceiptResult.Error(
                code = PrinterErrorCodes.ERROR
            )
        }

        val result = try {
            receiptPrinter.printReceipt(copyReceipt)
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            Log.e(TAG, "Receipt printer: failed to print receipt #$receiptId", e)

            return PrintReceiptResult.Error(
                code = PrinterErrorCodes.ERROR
            )
        }

        return when (result) {
            PrinterStatus.Ok -> PrintReceiptResult.Ok
            PrinterStatus.OutOfPaper -> PrintReceiptResult.OutOfPaper
            is PrinterStatus.Error -> PrintReceiptResult.Error(
                code = result.errorCode
            )
        }
    }

    private companion object {
        private const val TAG: String = "PrintReceiptUC"
    }
}

sealed interface PrintReceiptResult {
    data object Ok: PrintReceiptResult
    data object OutOfPaper: PrintReceiptResult
    data class Error(
        val code: String
    ): PrintReceiptResult
}