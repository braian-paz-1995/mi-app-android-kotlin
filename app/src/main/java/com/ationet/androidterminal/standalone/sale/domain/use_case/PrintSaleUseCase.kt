package com.ationet.androidterminal.standalone.sale.domain.use_case

import android.util.Log
import com.ationet.androidterminal.core.domain.receipt.PrinterStatus
import com.ationet.androidterminal.core.domain.receipt.ReceiptPrinter
import com.ationet.androidterminal.core.domain.repository.ReceiptRepository
import com.ationet.androidterminal.core.domain.util.PrinterErrorCodes
import com.ationet.androidterminal.standalone.sale.data.local.SaleOperationStateRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import javax.inject.Inject

@ViewModelScoped
class PrintSaleUseCase @Inject constructor(
    @SalePrinter private val printer: ReceiptPrinter,
    private val receiptRepository: ReceiptRepository,
    private val operationStateRepository: SaleOperationStateRepository,
) {
    suspend operator fun invoke(
        isCopy: Boolean,
    ): PrintSaleResult {
        val receipt = operationStateRepository.getState().receipt
        if(receipt == null) {
            Log.w(TAG, "Sale printer: no receipt found")
            return PrintSaleResult.Error(
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
                Log.e(TAG, "Sale printer: failed to print receipt #$receiptId", e)
            } else {
                Log.e(TAG, "Sale printer: failed to print receipt", e)
            }

            return PrintSaleResult.Error(
                code = PrinterErrorCodes.ERROR
            )
        }

        if (hasReceiptId) {
            Log.i(TAG, "Sale printer: printed receipt $receiptId")
        } else {
            Log.i(TAG, "Sale printer: printed receipt")
        }

        return when (result) {
            PrinterStatus.Ok -> PrintSaleResult.Ok
            PrinterStatus.OutOfPaper -> PrintSaleResult.OutOfPaper
            is PrinterStatus.Error -> PrintSaleResult.Error(
                code = result.errorCode
            )
        }
    }

    private suspend fun setReceiptAsCopy(receiptId: Int) {
        val receipt = try {
            receiptRepository.getReceipt(receiptId)
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()
            Log.e(TAG, "Sale printer: failed to get receipt #$receiptId", e)
            return
        }

        if (receipt == null) {
            Log.d(TAG, "Sale printer: no receipt found")
            return
        }

        val printableReceipt = receipt.copy(
            copy = true
        )

        try {
            receiptRepository.saveReceipt(printableReceipt)

            Log.i(TAG, "Sale Printer: receipt #$receiptId is now a copy")
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            Log.e(TAG, "Sale printer: failed to save receipt #$receiptId as copy", e)

            return
        }
    }

    private companion object {
        private const val TAG: String = "SalePrinterUC"
    }
}

sealed interface PrintSaleResult {
    data object Ok: PrintSaleResult
    data object OutOfPaper: PrintSaleResult
    data class Error(
        val code: String
    ): PrintSaleResult
}