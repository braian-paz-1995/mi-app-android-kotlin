package com.ationet.androidterminal.standalone.balance_enquiry.domain.use_case

import com.atio.log.Logger
import com.atio.log.util.error
import com.atio.log.util.warn
import com.ationet.androidterminal.core.domain.receipt.PrinterStatus
import com.ationet.androidterminal.core.domain.receipt.ReceiptPrinter
import com.ationet.androidterminal.core.domain.util.PrinterErrorCodes
import com.ationet.androidterminal.standalone.balance_enquiry.data.local.BalanceEnquiryStateRepository
import com.ationet.androidterminal.standalone.balance_enquiry.domain.receipt.BalanceEnquiryPrinter
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrintBalanceEnquiryTicketUseCase @Inject constructor(
    @BalanceEnquiryPrinter private val receiptPrinter: ReceiptPrinter,
    private val operationStateRepository: BalanceEnquiryStateRepository
) {

    companion object {
        private val logger = Logger("PrintBalanceEnquiryTicketUseCase")
    }

    suspend operator fun invoke(isCopy: Boolean): PrintBalanceEnquiryResult {
        val receipt = operationStateRepository.getState().receipt
        if (receipt == null) {
            logger.warn(message = "Balance Enquiry: receipt not found")

            return PrintBalanceEnquiryResult.Error(
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

            logger.error(message = "Balance Enquiry printing failed", e)

            return PrintBalanceEnquiryResult.Error(
                code = PrinterErrorCodes.ERROR
            )
        }

        return when (result) {
            PrinterStatus.Ok -> PrintBalanceEnquiryResult.Ok
            PrinterStatus.OutOfPaper -> PrintBalanceEnquiryResult.OutOfPaper
            is PrinterStatus.Error -> PrintBalanceEnquiryResult.Error(
                code = result.errorCode
            )
        }
    }
}

sealed interface PrintBalanceEnquiryResult {
    data object Ok : PrintBalanceEnquiryResult
    data object OutOfPaper : PrintBalanceEnquiryResult
    data class Error(
        val code: String
    ) : PrintBalanceEnquiryResult
}