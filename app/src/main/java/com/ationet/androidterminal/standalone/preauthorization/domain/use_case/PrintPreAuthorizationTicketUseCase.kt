package com.ationet.androidterminal.standalone.preauthorization.domain.use_case

import android.util.Log
import com.ationet.androidterminal.core.domain.receipt.PrinterStatus
import com.ationet.androidterminal.core.domain.receipt.ReceiptPrinter
import com.ationet.androidterminal.core.domain.util.PrinterErrorCodes
import com.ationet.androidterminal.standalone.preauthorization.data.local.PreAuthorizationOperationStateRepository
import com.ationet.androidterminal.standalone.preauthorization.domain.receipt.PreAuthorizationPrinter
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrintPreAuthorizationTicketUseCase @Inject constructor(
    @PreAuthorizationPrinter private val receiptPrinter: ReceiptPrinter,
    private val operationStateRepository: PreAuthorizationOperationStateRepository
) {

    suspend operator fun invoke(isCopy: Boolean): PrintPreAuthorizationResult {
        val receipt = operationStateRepository.getState().receipt
        if(receipt == null) {
            Log.w(TAG, "Pre-authorization: receipt not found")

            return PrintPreAuthorizationResult.Error(
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

            Log.e(TAG, "Pre-authorization printing failed", e)

            return PrintPreAuthorizationResult.Error(
                code = PrinterErrorCodes.ERROR
            )
        }

        return when (result) {
            PrinterStatus.Ok -> PrintPreAuthorizationResult.Ok
            PrinterStatus.OutOfPaper -> PrintPreAuthorizationResult.OutOfPaper
            is PrinterStatus.Error -> PrintPreAuthorizationResult.Error(
                code = result.errorCode
            )
        }
    }

    private companion object {
        private const val TAG: String = "PrintPreAuthReceipt"
    }
}

sealed interface PrintPreAuthorizationResult {
    data object Ok: PrintPreAuthorizationResult
    data object OutOfPaper: PrintPreAuthorizationResult
    data class Error(
        val code: String
    ): PrintPreAuthorizationResult
}