package com.ationet.androidterminal.standalone.completion.domain.receipt

import android.content.Context
import androidx.core.content.ContextCompat
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.data.local.util.splitWordLimit
import com.ationet.androidterminal.core.data.remote.ationet.OperationType
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
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptProduct
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptSite
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionType
import com.ationet.androidterminal.core.domain.receipt.PrinterStatus
import com.ationet.androidterminal.core.domain.receipt.ReceiptPrinter
import com.ationet.androidterminal.core.domain.util.CompanyPrice
import com.ationet.androidterminal.core.domain.util.PrinterErrorCodes
import com.ationet.androidterminal.core.domain.util.getTransactionTypeName
import com.ationet.androidterminal.core.util.LocaleFormatter
import com.ationet.androidterminal.core.util.limitDigits
import com.ationet.androidterminal.core.util.mask
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class CompletionReceiptPrinter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val printer: HALPrinter
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

                val balanceAvailable = when (receipt.transactionData.receiptData.operationType) {
                    OperationType.ConsumerCard.value,
                    OperationType.OldGiftCard.value,
                    OperationType.GiftCard.value -> {
                        currencySymbol + " " + receipt.transactionData.receiptData.availableBalance
                    }
                    else -> null
                }

                val client = when (receipt.transactionData.receiptData.operationType) {
                    OperationType.ConsumerCard.value -> receipt.transactionData.receiptData.customerName
                    else -> null
                }

                printTransactionInfo(
                    terminalId = terminalId,
                    authorizationCode = authorizationCode.orEmpty(),
                    transactionSequenceNumber = transactionSequenceNumber.orEmpty(),
                    invoice = invoice.orEmpty(),
                    availableBalance = balanceAvailable,
                    client = client,
                    configuration = receipt.printConfiguration
                )

                // Print ationet data
                printAtionetData(
                    primaryTrack = primaryTrack,
                    secondaryTrack = secondaryTrack,
                    receiptData = receiptData,
                    configuration = receipt.printConfiguration
                )

                // Print product data
                printProducts(
                    product = product,
                    transactionAmount = receipt.transactionData.transactionAmount ?: 0.0,
                    configuration = receipt.printConfiguration
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

        // Print receipt status
        if (receipt.copy) {
            printCopyTicket()
        } else {
            if (receipt.transactionData.responseCode == ResponseCodes.AUTHORIZED) {
                // Print signature
                printSignature()
            }

            printOriginalTicket()
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
        authorizationCode: String,
        transactionSequenceNumber: String,
        invoice: String,
        availableBalance: String?,
        client: String?,
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

        if (configuration.printInvoiceNumber) {
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
        } else {
            ContextCompat.getContextForLanguage(context)
                .getString(R.string.receipt_authorization_code).splitWordLimit(
                    ContextCompat.getContextForLanguage(context).getString(
                        R.string.receipt_limit_auth_code
                    ).toInt()
                ).forEach {

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
            authorizationCode.splitWordLimit(21).forEach {
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
        }

        printer.feedPaper()

        printer.write(
            text = ContextCompat.getContextForLanguage(context)
                .getString(R.string.receipt_tsn, transactionSequenceNumber),
            format = TextFormat(
                alignment = Alignment.Left,
                linefeed = true
            )
        )

        val invoiceLine = ContextCompat.getContextForLanguage(context)
            .getString(R.string.receipt_invoice_number) + " " + invoice
        printer.write(
            text = invoiceLine,
            format = TextFormat(
                alignment = Alignment.Left,
                linefeed = true
            )
        )

        printer.feedPaper()
        if (!availableBalance.isNullOrBlank()) {
            val balanceLine = ContextCompat.getContextForLanguage(context)
                .getString(R.string.receipt_balance) + " " + availableBalance

            printer.write(
                text = balanceLine,
                format = TextFormat(
                    alignment = Alignment.Left,
                    linefeed = true
                )
            )

            printer.feedPaper()
        }

        if (!client.isNullOrBlank()) {
            val clientLine = ContextCompat.getContextForLanguage(context)
                .getString(R.string.receipt_client) + " " + client

            printer.write(
                text = clientLine,
                format = TextFormat(
                    alignment = Alignment.Left,
                    linefeed = true
                )
            )
            printer.feedPaper()
        }
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
        } else if (!authorizationCode.isNullOrBlank()) {
            ContextCompat.getContextForLanguage(context)
                .getString(R.string.receipt_authorization_code).splitWordLimit(
                    ContextCompat.getContextForLanguage(context).getString(
                        R.string.receipt_limit_auth_code
                    ).toInt()
                ).forEach {

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
            authorizationCode.splitWordLimit(21).forEach {

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
        }

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
                .getString(R.string.receipt_company_name) + " " + receiptData.companyName

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

        printer.feedPaper()
    }

    private fun printProducts(
        product: ReceiptProduct,
        transactionAmount: Double,
        configuration: ReceiptPrintConfiguration,
    ) {
        printer.write(
            text = ContextCompat.getContextForLanguage(context)
                .getString(R.string.receipt_product_name, product.name),
            format = TextFormat(
                alignment = Alignment.Left,
                linefeed = true
            )
        )

        printer.feedPaper()
        if (configuration.printProductInColumns) {
            printProductInColumns(
                product = product,
                quantity = product.quantity,
                unitPrice = product.unitPrice,
                amount = product.amount
            )
        } else {
            printProductInRow(
                product = product,
                quantity = product.quantity,
                unitPrice = product.unitPrice,
                amount = product.amount
            )
        }

        printer.drawLine()

        if (configuration.printTransactionDetails) {
            printer.feedPaper()

            val companyPrice = CompanyPrice(product, product.quantity)
            val companyDiscounts = companyPrice.calculateDiscountsAndRebates()
            val companySurcharges = companyPrice.calculateSurcharge()

            if (companyDiscounts > 0) {
                printer.write(
                    text = ContextCompat.getContextForLanguage(context).getString(
                        R.string.receipt_discounts_and_rebates,
                        LocaleFormatter.formatNumber(companyDiscounts.toString(), 2, context)
                    ),
                    format = TextFormat(
                        alignment = Alignment.Right,
                        linefeed = true
                    )
                )
            }

            if (companySurcharges > 0) {
                printer.write(
                    text = ContextCompat.getContextForLanguage(context)
                        .getString(
                            R.string.receipt_surcharges,
                            LocaleFormatter.formatNumber(companySurcharges.toString(), 2, context)
                        ),
                    format = TextFormat(
                        alignment = Alignment.Right,
                        linefeed = true
                    )
                )
            }

            if (product.amount != null && (companyDiscounts > 0 || companySurcharges > 0)) {
                printer.feedPaper()
                printer.write(
                    text = ContextCompat.getContextForLanguage(context).getString(
                        R.string.total,
                        LocaleFormatter.formatNumber(transactionAmount.toString(), 2, context)
                    ),
                    format = TextFormat(
                        alignment = Alignment.Right,
                        linefeed = true,
                        textWeight = TextWeight.ExtraBold
                    )
                )
            }
        }

        printer.feedPaper()
    }

    private fun printProductInColumns(
        product: ReceiptProduct,
        quantity: Double?,
        unitPrice: Double?,
        amount: Double?
    ) {
        printer.drawTable {
            headers {
                header(
                    text = ContextCompat.getContextForLanguage(context).getString(R.string.prod),
                    fraction = 0.15
                )

                header(
                    text = ContextCompat.getContextForLanguage(context).getString(R.string.qty),
                    fraction = 0.35
                )

                header(
                    text = ContextCompat.getContextForLanguage(context).getString(R.string.ppu),
                    fraction = 0.15
                )

                header(
                    text = ContextCompat.getContextForLanguage(context).getString(R.string.amount)
                )
            }

            row {
                cell(
                    text = product.code
                )

                cell(
                    text = if (product.quantity == null) {
                        ""
                    } else {
                        LocaleFormatter.formatNumber(quantity.toString(), 3, context)
                    }
                )

                cell(
                    text = if (product.unitPrice == null) {
                        ""
                    } else {
                        unitPrice.limitDigits()
                    }
                )

                cell(
                    text = if (product.amount == null) {
                        ""
                    } else {
                        LocaleFormatter.formatNumber(amount.toString(), 2, context)
                    }
                )
            }
        }

        printer.feedPaper()
    }

    private fun printProductInRow(
        product: ReceiptProduct,
        quantity: Double?,
        unitPrice: Double?,
        amount: Double?
    ) {
        printer.write(
            text = ContextCompat.getContextForLanguage(context).getString(R.string.prod),
            format = TextFormat(
                alignment = Alignment.Left
            )
        )

        printer.write(
            text = product.code,
            format = TextFormat(
                alignment = Alignment.Right,
                linefeed = true
            )
        )

        printer.feedPaper()

        printer.write(
            text = ContextCompat.getContextForLanguage(context).getString(R.string.qty),
            format = TextFormat(
                alignment = Alignment.Left
            )
        )

        printer.write(
            text = LocaleFormatter.formatNumber(quantity.toString(), 3, context),
            format = TextFormat(
                alignment = Alignment.Right,
                linefeed = true
            )
        )

        printer.feedPaper()

        printer.write(
            text = ContextCompat.getContextForLanguage(context).getString(R.string.ppu),
            format = TextFormat(
                alignment = Alignment.Left
            )
        )

        printer.write(
            text = unitPrice.limitDigits(),
            format = TextFormat(
                alignment = Alignment.Right,
                linefeed = true
            )
        )

        printer.feedPaper()

        printer.write(
            text = ContextCompat.getContextForLanguage(context).getString(R.string.amount),
            format = TextFormat(
                alignment = Alignment.Left
            )
        )

        printer.write(
            text = LocaleFormatter.formatNumber(amount.toString(), 2, context),
            format = TextFormat(
                alignment = Alignment.Right,
                linefeed = true
            )
        )

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

    private fun printSignature() {
        printer.write(
            text = ContextCompat.getContextForLanguage(context)
                .getString(R.string.receipt_signature),
            format = TextFormat(
                alignment = Alignment.Left,
            )
        )

        printer.drawLine(
            format = TextFormat(
                linefeed = true
            )
        )

        printer.feedPaper(steps = 3)
    }

    private fun printOriginalTicket() {
        printer.write(
            text = ContextCompat.getContextForLanguage(context)
                .getString(R.string.receipt_commerce_ticket),
            format = TextFormat(
                alignment = Alignment.Center,
                underline = true,
            )
        )

        printer.feedPaper()
    }

    private fun printCopyTicket() {
        printer.write(
            text = ContextCompat.getContextForLanguage(context)
                .getString(R.string.receipt_client_copy),
            format = TextFormat(
                alignment = Alignment.Center,
                underline = true,
            )
        )

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