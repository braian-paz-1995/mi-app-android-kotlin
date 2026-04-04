package com.ationet.androidterminal.standalone.completion.domain.use_cases

import android.util.Log
import com.ationet.androidterminal.core.domain.receipt.PrinterStatus
import com.ationet.androidterminal.core.domain.receipt.ReceiptPrinter
import com.ationet.androidterminal.core.domain.repository.ReceiptRepository
import com.ationet.androidterminal.core.domain.util.PrinterErrorCodes
import com.ationet.androidterminal.standalone.completion.data.local.CompletionOperationStateRepository
import com.ationet.androidterminal.standalone.completion.domain.receipt.CompletionPrinter
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrintCompletionUseCase @Inject constructor(
    @CompletionPrinter private val printer: ReceiptPrinter,
    private val receiptRepository: ReceiptRepository,
    private val operationRepository: CompletionOperationStateRepository
) {
    suspend operator fun invoke(
        isCopy: Boolean,
    ): PrintCompletionResult {
        val receipt = operationRepository.getState().receipt

        if(receipt == null) {
            Log.w(TAG, "Completion printer: receipt not found in operation")
            return PrintCompletionResult.Error(
                code = PrinterErrorCodes.TRANS_NOT_FOUND
            )
        }

        val receiptId = receipt.id
        val hasReceiptId = receiptId != 0

        if (hasReceiptId) {
            setReceiptAsCopy(receiptId)
        }

        val printableReceipt = receipt.copy(
            copy = isCopy
        )

        val result = try {
            printer.printReceipt(printableReceipt)
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            if (hasReceiptId) {
                Log.e(TAG, "Completion printer: failed to print receipt #$receiptId", e)
            } else {
                Log.e(TAG, "Completion printer: failed to print receipt", e)
            }

            Log.e(TAG, "Completion printer: failed to print receipt #$receiptId", e)

            return PrintCompletionResult.Error(
                code = PrinterErrorCodes.ERROR
            )
        }

        if (hasReceiptId) {
            Log.i(TAG, "Completion printer: printed receipt $receiptId")
        } else {
            Log.i(TAG, "Completion printer: printed receipt")
        }

        return when (result) {
            PrinterStatus.Ok -> PrintCompletionResult.Ok
            PrinterStatus.OutOfPaper -> PrintCompletionResult.OutOfPaper
            is PrinterStatus.Error -> PrintCompletionResult.Error(
                code = result.errorCode
            )
        }
    }

    private suspend fun setReceiptAsCopy(receiptId: Int) {
        val receipt = try {
            receiptRepository.getReceipt(receiptId)
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()
            Log.e(TAG, "Completion printer: failed to get receipt #$receiptId", e)
            return
        }

        if (receipt == null) {
            Log.d(TAG, "Completion printer: no receipt found")
            return
        }

        val printableReceipt = receipt.copy(
            copy = true
        )

        try {
            receiptRepository.saveReceipt(printableReceipt)

            Log.i(TAG, "Completion Printer: receipt #$receiptId is now a copy")
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            Log.e(TAG, "Completion printer: failed to save receipt #$receiptId as copy", e)

            return
        }
    }

    private companion object {
        private const val TAG: String = "CompletionPrinterUC"
    }
}

sealed interface PrintCompletionResult {
    data object Ok: PrintCompletionResult
    data object OutOfPaper: PrintCompletionResult
    data class Error(
        val code: String
    ): PrintCompletionResult
}