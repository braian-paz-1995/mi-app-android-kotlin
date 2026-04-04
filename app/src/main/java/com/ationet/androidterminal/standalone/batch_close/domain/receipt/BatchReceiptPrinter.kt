package com.ationet.androidterminal.standalone.batch_close.domain.receipt

import android.content.Context
import androidx.core.content.ContextCompat
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.data.local.util.splitWordLimit
import com.ationet.androidterminal.core.data.remote.ationet.ResponseCodes
import com.ationet.androidterminal.core.domain.hal.printer.Alignment
import com.ationet.androidterminal.core.domain.hal.printer.HALPrinter
import com.ationet.androidterminal.core.domain.hal.printer.TextFormat
import com.ationet.androidterminal.core.domain.hal.printer.TextSize
import com.ationet.androidterminal.core.domain.hal.printer.TextWeight
import com.ationet.androidterminal.core.domain.model.receipt.Receipt
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptData
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptFooter
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptHeader
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptPrintConfiguration
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptSite
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionData
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionType
import com.ationet.androidterminal.core.domain.receipt.PrinterStatus
import com.ationet.androidterminal.core.domain.receipt.ReceiptPrinter
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.core.domain.util.PrinterErrorCodes
import com.ationet.androidterminal.core.domain.util.getTransactionTypeName
import com.ationet.androidterminal.core.util.LocaleFormatter
import com.ationet.androidterminal.core.util.mask
import dagger.hilt.android.qualifiers.ApplicationContext

@BatchPrinter
class BatchReceiptPrinter(
    @ApplicationContext private val context: Context,
    private val printer: HALPrinter,
    private val getConfiguration: GetConfiguration,

) : ReceiptPrinter {
    override suspend fun printReceipt(receipt: Receipt): PrinterStatus {


        // Print header
        printMainHeader(receipt.header)

        // Print transaction header
        printTransactionHeader(receipt.transactionLine)

        // Print site info
        printSiteInfo(receipt.site)

        // Print transaction info
        with(receipt.transactionData) {
            if (responseCode == ResponseCodes.AUTHORIZED) {
                printTransactionInfo(
                    terminalId = terminalId,
                    transactionSequenceNumber = transactionSequenceNumber.orEmpty(),
                )

                // Print ationet data
                printAtionetData(
                    primaryTrack = primaryTrack,
                    secondaryTrack = secondaryTrack,
                    receiptData = receiptData,
                    configuration = receipt.printConfiguration
                )

                // Print sales overview
                printSaleOverview(
                    transactionData = receipt.transactionData,
                )

                printCCOverview(
                    transactionData = receipt.transactionData,
                )

            } else {
                printErrorTransactionInfo(
                    terminalId = terminalId,
                    transactionSequenceNumber = transactionSequenceNumber,
                    authorizationCode = authorizationCode,
                    invoice = invoice,
                    configuration = receipt.printConfiguration
                )
                printError(responseText)
            }
        }

        // Print footers
        printFooter(receipt.footer)

        val result = printer.print()
        return when (result) {
            com.ationet.androidterminal.core.domain.hal.printer.PrinterStatus.Ok -> PrinterStatus.Ok
            com.ationet.androidterminal.core.domain.hal.printer.PrinterStatus.OutOfPaper -> PrinterStatus.OutOfPaper
            com.ationet.androidterminal.core.domain.hal.printer.PrinterStatus.Busy -> PrinterStatus.Error(
                errorCode = PrinterErrorCodes.BUSY
            )

            com.ationet.androidterminal.core.domain.hal.printer.PrinterStatus.Overheat -> PrinterStatus.Error(
                errorCode = PrinterErrorCodes.OVER_HEAT
            )

            com.ationet.androidterminal.core.domain.hal.printer.PrinterStatus.UnderVoltage -> PrinterStatus.Error(
                errorCode = PrinterErrorCodes.UNDER_VOLTAGE
            )

            com.ationet.androidterminal.core.domain.hal.printer.PrinterStatus.DriverError -> PrinterStatus.Error(
                errorCode = PrinterErrorCodes.DRIVER_ERROR
            )

            com.ationet.androidterminal.core.domain.hal.printer.PrinterStatus.Error -> PrinterStatus.Error(
                errorCode = PrinterErrorCodes.ERROR
            )
        }
    }

    private fun printMainHeader(header: ReceiptHeader) {
        val titleLines = header.title.splitWordLimit(21)
        for (titleLine in titleLines) {
            printer.write(
                text = titleLine,
                format = TextFormat(
                    textWeight = TextWeight.Bold,
                    alignment = Alignment.Center,
                    textSize = TextSize.Large,
                    linefeed = true
                )
            )
        }

        printer.feedPaper()

        val subtitleLines = header.subtitle.splitWordLimit(42)
        for (subtitleLine in subtitleLines) {
            printer.write(
                text = subtitleLine,
                format = TextFormat(
                    alignment = Alignment.Center,
                    linefeed = true
                )
            )
        }

        printer.feedPaper()
    }

    private fun printTransactionHeader(header: ReceiptTransactionType) {
        val transactionName = "(" + getTransactionTypeName(context, header.name) + ")"
        val transactionDate = LocaleFormatter.formatDate(header.dateTime, context = context)
        val transactionTime = LocaleFormatter.formatTime(header.dateTime, context = context)

        printer.write(
            text = transactionDate,
            format = TextFormat(
                alignment = Alignment.Left,
            )
        )

        printer.write(
            text = transactionName,
            format = TextFormat(
                alignment = Alignment.Center,
            )
        )

        printer.write(
            text = transactionTime,
            format = TextFormat(
                alignment = Alignment.Right,
                linefeed = true
            )
        )

        printer.feedPaper()
    }

    private fun printSiteInfo(siteInfo: ReceiptSite) {
        printer.write(
            text = "${siteInfo.code} - ${siteInfo.name}",
            format = TextFormat(
                alignment = Alignment.Left,
                linefeed = true
            )
        )

        printer.write(
            text = siteInfo.address,
            format = TextFormat(
                alignment = Alignment.Left,
                linefeed = true
            )
        )

        printer.write(
            text = siteInfo.cuit,
            format = TextFormat(
                alignment = Alignment.Left,
                linefeed = true
            )
        )

        printer.feedPaper()
    }

    private fun printTransactionInfo(
        terminalId: String,
        transactionSequenceNumber: String
    ) {
        printer.write(
            text = ContextCompat.getContextForLanguage(context)
                .getString(R.string.receipt_terminal_id, terminalId),
            format = TextFormat(
                alignment = Alignment.Left,
                linefeed = true
            )
        )

        printer.feedPaper()

        printer.feedPaper()

        printer.write(
            text = ContextCompat.getContextForLanguage(context)
                .getString(R.string.receipt_tsn, transactionSequenceNumber),
            format = TextFormat(
                alignment = Alignment.Left,
                linefeed = true
            )
        )

        printer.feedPaper()
    }

    private fun printErrorTransactionInfo(
        terminalId: String,
        transactionSequenceNumber: String?,
        authorizationCode: String?,
        invoice: String?,
        configuration: ReceiptPrintConfiguration,
    ) {
        printer.write(
            text = ContextCompat.getContextForLanguage(context)
                .getString(R.string.receipt_terminal_id, terminalId),
            format = TextFormat(
                alignment = Alignment.Left,
                linefeed = true
            )
        )

        printer.feedPaper()

        if (configuration.printInvoiceNumber && !invoice.isNullOrBlank()) {
            ContextCompat.getContextForLanguage(context)
                .getString(R.string.receipt_invoice_number).splitWordLimit(21).forEach {

                    printer.write(
                        text = it,
                        format = TextFormat(
                            alignment = Alignment.Left,
                            textWeight = TextWeight.Bold,
                            textSize = TextSize.Large,
                            linefeed = true
                        )
                    )
                }

            invoice.splitWordLimit(21).forEach {

                printer.write(
                    text = it,
                    format = TextFormat(
                        alignment = Alignment.Left,
                        textWeight = TextWeight.Bold,
                        textSize = TextSize.Large,
                        linefeed = true
                    )
                )
            }
        } /*else if (!authorizationCode.isNullOrBlank()) {
            printer.write(
                text = ContextCompat.getContextForLanguage(context)
                    .getString(R.string.receipt_authorization_code),
                format = TextFormat(
                    alignment = Alignment.Left,
                    textWeight = TextWeight.Bold,
                    textSize = TextSize.Large,
                    linefeed = true
                )
            )

            printer.write(
                text = authorizationCode,
                format = TextFormat(
                    alignment = Alignment.Left,
                    textWeight = TextWeight.Bold,
                    textSize = TextSize.Large,
                    linefeed = true
                )
            )
        }*/

        printer.feedPaper()

        if (!transactionSequenceNumber.isNullOrBlank()) {
            printer.write(
                text = ContextCompat.getContextForLanguage(context)
                    .getString(R.string.receipt_tsn, transactionSequenceNumber),
                format = TextFormat(
                    alignment = Alignment.Left,
                    linefeed = true
                )
            )

            printer.feedPaper()
        }
    }

    private fun printAtionetData(
        primaryTrack: String,
        secondaryTrack: String?,
        receiptData: ReceiptData,
        configuration: ReceiptPrintConfiguration,
    ) {
        if (configuration.printDriver) {
            if (!receiptData.customerDriverName.isNullOrBlank()) {
                val customerDriverLine = ContextCompat.getContextForLanguage(context)
                    .getString(R.string.receipt_client) + " " + receiptData.customerDriverName

                printer.write(
                    text = customerDriverLine,
                    format = TextFormat(
                        alignment = Alignment.Left,
                        linefeed = true
                    )
                )

                printer.feedPaper()
            }

            if (!receiptData.customerDriverId.isNullOrBlank()) {
                val clientIdLine = ContextCompat.getContextForLanguage(context)
                    .getString(R.string.receipt_client_id) + " " + receiptData.customerDriverId

                printer.write(
                    text = clientIdLine,
                    format = TextFormat(
                        alignment = Alignment.Left,
                        linefeed = true
                    )
                )

                printer.feedPaper()
            }

            if (!receiptData.customerPan.isNullOrBlank()) {
                val customerPanLine = ContextCompat.getContextForLanguage(context)
                    .getString(R.string.receipt_customer_pan) + " " + receiptData.customerPan.mask()

                printer.write(
                    text = customerPanLine,
                    format = TextFormat(
                        alignment = Alignment.Left,
                        linefeed = true
                    )
                )

                printer.feedPaper()
            }
        }

        if (configuration.printVehicle) {
            if (!receiptData.customerVehicleCode.isNullOrBlank()) {
                val vehicleCodeLine = ContextCompat.getContextForLanguage(context)
                    .getString(R.string.receipt_vehicle_code) + " " + receiptData.customerVehicleCode

                printer.write(
                    text = vehicleCodeLine,
                    format = TextFormat(
                        alignment = Alignment.Left,
                        linefeed = true
                    )
                )

                printer.feedPaper()
            }

            if (!receiptData.customerPlate.isNullOrBlank()) {
                val vehiclePlateLine = ContextCompat.getContextForLanguage(context)
                    .getString(R.string.receipt_vehicle_plate) + " " + receiptData.customerPlate

                printer.write(
                    text = vehiclePlateLine,
                    format = TextFormat(
                        alignment = Alignment.Left,
                        linefeed = true
                    )
                )

                printer.feedPaper()
            }
        }

        if (configuration.printCompanyName && !receiptData.companyName.isNullOrBlank()) {
            val companyNameLine = ContextCompat.getContextForLanguage(context)
                .getString(R.string.receipt_company_name) + receiptData.companyName

            printer.write(
                text = companyNameLine,
                format = TextFormat(
                    alignment = Alignment.Left,
                    linefeed = true
                )
            )

            printer.feedPaper()
        }

        if (configuration.printPrimaryTrack && primaryTrack.isNotBlank()) {
            val primaryTrackLine = ContextCompat.getContextForLanguage(context)
                .getString(R.string.receipt_primary_track) + " " + primaryTrack.mask()

            printer.write(
                text = primaryTrackLine,
                format = TextFormat(
                    alignment = Alignment.Left,
                    linefeed = true
                )
            )

            printer.feedPaper()
        }

        if (configuration.printSecondaryTrack && !secondaryTrack.isNullOrBlank()) {
            val secondaryTrackLine = ContextCompat.getContextForLanguage(context)
                .getString(R.string.receipt_secondary_track) + " " + secondaryTrack

            printer.write(
                text = secondaryTrackLine,
                format = TextFormat(
                    alignment = Alignment.Left,
                    linefeed = true
                )
            )

            printer.feedPaper()
        }
    }

    private fun printSaleOverview(
        transactionData: ReceiptTransactionData,
    ) {
        val configuration = getConfiguration()
        printer.write(
            text = "${ContextCompat.getContextForLanguage(context).getString(R.string.batch_id)}: ${transactionData.batchNumber}",
            format = TextFormat(
                alignment = Alignment.Left,
                linefeed = true
            )
        )
        printer.write(
            text = "${ContextCompat.getContextForLanguage(context).getString(R.string.receipt_sales)}: ${transactionData.salesCounter}",
            format = TextFormat(
                alignment = Alignment.Left,
                linefeed = true
            )
        )
        printer.write(
            text = "${ContextCompat.getContextForLanguage(context).getString(R.string.receipt_total_sale)}: ${
                LocaleFormatter.formatNumber(
                    transactionData.salesTotal.toString(),
                    2,
                    configuration.language
                )
            }",
            format = TextFormat(
                alignment = Alignment.Left,
                linefeed = true
            )
        )

        printer.write(
            text = "${ContextCompat.getContextForLanguage(context).getString(R.string.receipt_cancelled)}: ${transactionData.voidedCounter}",
            format = TextFormat(
                alignment = Alignment.Left,
                linefeed = true
            )
        )

        printer.write(
            text = "${ContextCompat.getContextForLanguage(context).getString(R.string.receipt_total_cancelled)}: ${
                LocaleFormatter.formatNumber(
                    transactionData.voidedTotal.toString(),
                    2,
                    configuration.language
                )
            }",
            format = TextFormat(
                alignment = Alignment.Left,
                linefeed = true
            )
        )

        printer.feedPaper()
        printer.drawLine()
        printer.feedPaper()
    }
    private fun printCCOverview(
        transactionData: ReceiptTransactionData,

        ) {

        val configuration = getConfiguration()

        if (configuration.ationet.promptConsumerCard) {
            printer.write(
                text = "${ContextCompat.getContextForLanguage(context).getString(R.string.rechargecc_count)}: ${transactionData.rechargeCCCounter}",
                format = TextFormat(alignment = Alignment.Left, linefeed = true)
            )
            printer.write(
                text = "${ContextCompat.getContextForLanguage(context).getString(R.string.rechargecc_amount)}: ${
                    LocaleFormatter.formatNumber(transactionData.rechargeCCTotal.toString(), 2, configuration.language)
                }",
                format = TextFormat(alignment = Alignment.Left, linefeed = true)
            )

            val paymentGroups = transactionData.batchClose.rechargeCC
                .groupBy { it.paymentMethod }
                .filterKeys { !it.isNullOrBlank() }

            if (paymentGroups.isNotEmpty()) {
                printer.write(
                    text = ContextCompat.getContextForLanguage(context)
                        .getString(R.string.receipt_payment_method_batch),
                    format = TextFormat(alignment = Alignment.Left, linefeed = true)
                )

                val transaccionesLabel = ContextCompat.getContextForLanguage(context)
                    .getString(R.string.rechargecc_count)
                val totalLabel = ContextCompat.getContextForLanguage(context)
                    .getString(R.string.rechargecc_amount)
                val paymentMethodLabel = ContextCompat.getContextForLanguage(context).getString(R.string.empty_Payment_Method)

                paymentGroups.forEach { (method, transactions) ->
                    val count = transactions.size
                    val total = transactions.sumOf { it.amount }


                    printer.write(
                        text = "      ${method ?: paymentMethodLabel}",
                        format = TextFormat(alignment = Alignment.Left, linefeed = true)
                    )

                    printer.write(
                        text = "      $transaccionesLabel: $count",
                        format = TextFormat(alignment = Alignment.Left, linefeed = true)
                    )


                    printer.write(
                        text = "      $totalLabel: ${
                            LocaleFormatter.formatNumber(
                                total.toString(),
                                2,
                                configuration.language
                            )
                        }",
                        format = TextFormat(alignment = Alignment.Left, linefeed = true)
                    )
                }
            }
            printer.write(
                text = "${ContextCompat.getContextForLanguage(context).getString(R.string.reversecc_count)}: ${transactionData.reverseCCCounter}",
                format = TextFormat(alignment = Alignment.Left, linefeed = true)
            )
            printer.write(
                text = "${ContextCompat.getContextForLanguage(context).getString(R.string.reversecc_amount)}: ${
                    LocaleFormatter.formatNumber(transactionData.reverseCCTotal.toString(), 2, configuration.language)
                }",
                format = TextFormat(alignment = Alignment.Left, linefeed = true)
            )
        }
        printer.feedPaper()
        printer.drawLine()
        printer.feedPaper()
    }


    private fun printError(
        responseText: String
    ) {
        val responseTextLines = responseText.splitWordLimit(21)
        for (responseTextLine in responseTextLines) {
            printer.write(
                text = responseTextLine,
                format = TextFormat(
                    alignment = Alignment.Center,
                    textWeight = TextWeight.Bold,
                    textSize = TextSize.Large,
                    linefeed = true
                )
            )
        }

        printer.feedPaper()
    }

    private fun printFooter(footer: ReceiptFooter) {
        if (footer.footer.isNotBlank()) {
            val footerLines = footer.footer.splitWordLimit(21)
            for (footerLine in footerLines) {
                printer.write(
                    text = footerLine,
                    format = TextFormat(
                        alignment = Alignment.Center,
                        textWeight = TextWeight.Bold,
                        textSize = TextSize.Large,
                        linefeed = true
                    )
                )
            }

            printer.feedPaper()
        }

        if (footer.bottomNote.isNotBlank()) {
            val bottomNoteLines = footer.bottomNote.splitWordLimit(42)
            for (bottomNoteLine in bottomNoteLines) {
                printer.write(
                    text = bottomNoteLine,
                    format = TextFormat(
                        alignment = Alignment.Center,
                        linefeed = true
                    )
                )
            }

            printer.feedPaper()
        }

        printer.feedPaper()
    }
}