package com.ationet.androidterminal.core.domain.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.model.receipt.Receipt
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptData
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptFooter
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptHeader
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptPrintConfiguration
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptProduct
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptProductInputType
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptSite
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionData
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionType
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionTypeName
import com.ationet.androidterminal.core.domain.model.receipt.TransactionModifier
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun createCommunicationErrorReceipt(
    context: Context,
    configuration: Configuration,
    transactionName: ReceiptTransactionTypeName,
    currentInstant: Instant,
    primaryTrack: String,
    secondaryTrack: String?,
    inputType: ReceiptProductInputType,
    productName: String,
    productCode: String,
    productUnitPrice: Double?,
    quantity: Double?,
    amount: Double?,
    batchId: Int
): Receipt {
    return createErrorReceipt(
        configuration = configuration,
        currentInstant = currentInstant,
        primaryTrack = primaryTrack,
        secondaryTrack = secondaryTrack,
        inputType = inputType,
        productName = productName,
        productCode = productCode,
        productUnitPrice = productUnitPrice,
        quantity = quantity,
        amount = amount,
        responseText = ContextCompat.getContextForLanguage(context)
            .getString(R.string.communication_failure),
        responseCode = null,
        receiptData = ReceiptData(),
        transactionName = transactionName,
        batchId = batchId,
        authorizationCode = null,
        invoiceNumber = null
    )
}

fun createErrorReceipt(
    configuration: Configuration,
    transactionName: ReceiptTransactionTypeName,
    currentInstant: Instant,
    primaryTrack: String,
    secondaryTrack: String?,
    inputType: ReceiptProductInputType,
    productName: String,
    productCode: String,
    productUnitPrice: Double?,
    quantity: Double?,
    amount: Double?,
    responseCode: String?,
    responseText: String,
    receiptData: ReceiptData,
    batchId:Int,
    authorizationCode: String?,
    invoiceNumber: String?,
    pumpId: String? = null
): Receipt {
    return createReceipt(
        configuration = configuration,
        currentInstant = currentInstant,
        primaryTrack = primaryTrack,
        secondaryTrack = secondaryTrack,
        inputType = inputType,
        productName = productName,
        productCode = productCode,
        productUnitPrice = productUnitPrice,
        quantity = quantity,
        amount = amount,
        responseText = responseText,
        responseCode = responseCode,
        receiptData = receiptData,
        authorizationCode = authorizationCode,
        invoiceNumber = invoiceNumber,
        transactionSequenceNumber = null,
        modifiers = emptyList(),
        transactionName = transactionName,
        batchId = batchId,
        pumpId = pumpId
    )
}

fun createReceipt(
    configuration: Configuration,
    transactionName: ReceiptTransactionTypeName,
    currentInstant: Instant,
    authorizationCode: String?,
    invoiceNumber: String?,
    transactionSequenceNumber: String?,
    primaryTrack: String,
    secondaryTrack: String?,
    inputType: ReceiptProductInputType,
    productName: String,
    productCode: String,
    productUnitPrice: Double?,
    quantity: Double?,
    amount: Double?,
    responseCode: String?,
    responseText: String,
    receiptData: ReceiptData,
    modifiers: List<TransactionModifier>,
    batchId: Int,
    pumpId: String? = null,
    transactionAmount: Double? = null
): Receipt {
    return Receipt(
        copy = false,
        controllerOwner = configuration.controllerType,
        header = ReceiptHeader(
            title = configuration.ticket.title,
            subtitle = configuration.ticket.subtitle,
        ),
        footer = ReceiptFooter(
            footer = configuration.ticket.footer,
            bottomNote = configuration.ticket.bottomNote,
        ),
        transactionLine = ReceiptTransactionType(
            dateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
            name = transactionName
        ),
        site = ReceiptSite(
            name = configuration.site.siteName,
            code = configuration.site.siteCode,
            address = configuration.site.siteAddress,
            cuit = configuration.site.siteCuit
        ),
        printConfiguration = with(configuration.ticket) {
            ReceiptPrintConfiguration(
                printDriver = driverIdentification,
                printVehicle = vehicleIdentification,
                printCompanyName = companyName,
                printPrimaryTrack = primaryIdentification,
                printSecondaryTrack = secondaryIdentification,
                printTransactionDetails = transactionDetails,
                printInvoiceNumber = invoiceNumberInsteadOfAuthorizationCode,
                printProductInColumns = isDetailInColumn
            )
        },
        transactionData = ReceiptTransactionData(
            responseCode = responseCode,
            responseText = responseText,
            terminalId = configuration.ationet.terminalId,
            primaryTrack = primaryTrack,
            secondaryTrack = secondaryTrack,
            authorizationCode = authorizationCode,
            invoice = invoiceNumber,
            transactionSequenceNumber = transactionSequenceNumber,
            receiptData = receiptData,
            product = ReceiptProduct(
                inputType = inputType,
                name = productName,
                code = productCode,
                unitPrice = productUnitPrice,
                quantity = quantity,
                amount = amount,
                modifiers = modifiers
            ),
            unitOfMeasure = configuration.fuelMeasureUnit,
            currencySymbol = configuration.currencyFormat,
            transactionAmount = transactionAmount?.toDouble() ?: 0.0
        ),
        batchId = batchId,
        pumpId = pumpId
    )
}

/* Gets the display response text in length order, or an empty string if all of them are empty */
val NativeRequest.displayResponseText
    get() = longResponseText ?: responseMessage ?: responseText.orEmpty()