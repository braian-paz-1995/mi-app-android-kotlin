package com.ationet.androidterminal.standalone.clear_pending.domain.use_case

import android.util.Log
import com.ationet.androidterminal.core.domain.receipt.PrinterStatus
import com.ationet.androidterminal.core.domain.receipt.ReceiptPrinter
import com.ationet.androidterminal.core.domain.repository.ReceiptRepository
import com.ationet.androidterminal.core.domain.util.PrinterErrorCodes
import com.ationet.androidterminal.standalone.clear_pending.data.local.ClearPendingOperationStateRepository
import com.ationet.androidterminal.standalone.clear_pending.domain.receipt.ClearPendingPrinter
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrintClearPendingUseCase @Inject constructor(
    @ClearPendingPrinter private val printer: ReceiptPrinter,
    private val receiptRepository: ReceiptRepository,
    private val operationRepository: ClearPendingOperationStateRepository
) {
    suspend operator fun invoke(
        isCopy: Boolean,
    ): PrintClearPendingResult {
        val receipt = operationRepository.getState().receipt
        if (receipt == null) {
            Log.w(TAG, "Clear Pending printer: receipt not found in operation")
            return PrintClearPendingResult.Error(
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
                Log.e(TAG, "Clear Pending printer: failed to print receipt #$receiptId", e)
            } else {
                Log.e(TAG, "Clear Pending printer: failed to print receipt", e)
            }

            Log.e(TAG, "Clear Pending printer: failed to print receipt #$receiptId", e)

            return PrintClearPendingResult.Error(
                code = PrinterErrorCodes.ERROR
            )
        }

        if (hasReceiptId) {
            Log.i(TAG, "Clear Pending printer: printed receipt $receiptId")
        } else {
            Log.i(TAG, "Clear Pending printer: printed receipt")
        }

        return when (result) {
            PrinterStatus.Ok -> PrintClearPendingResult.Ok
            PrinterStatus.OutOfPaper -> PrintClearPendingResult.OutOfPaper
            is PrinterStatus.Error -> PrintClearPendingResult.Error(
                code = result.errorCode
            )
        }
    }

    private suspend fun setReceiptAsCopy(receiptId: Int) {
        val receipt = try {
            receiptRepository.getReceipt(receiptId)
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()
            Log.e(TAG, "Clear Pending printer: failed to get receipt #$receiptId", e)
            return
        }

        if (receipt == null) {
            Log.d(TAG, "Clear Pending printer: no receipt found")
            return
        }

        val printableReceipt = receipt.copy(
            copy = true
        )

        try {
            receiptRepository.saveReceipt(printableReceipt)

            Log.i(TAG, "Clear Pending Printer: receipt #$receiptId is now a copy")
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            Log.e(TAG, "Clear Pending printer: failed to save receipt #$receiptId as copy", e)

            return
        }
    }

    private companion object {
        private const val TAG: String = "ClearPendingPrinterUC"
    }
}

sealed interface PrintClearPendingResult {
    data object Ok : PrintClearPendingResult
    data object OutOfPaper : PrintClearPendingResult
    data class Error(
        val code: String
    ) : PrintClearPendingResult
}