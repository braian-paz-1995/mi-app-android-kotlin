package com.ationet.androidterminal.standalone.sale.domain.use_case

import android.util.Log
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.domain.model.receipt.Receipt
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptData
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptFooter
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptHeader
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptModifierClass
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptModifierType
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptPrintConfiguration
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptProduct
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptProductInputType
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptSite
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionData
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionType
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionTypeName
import com.ationet.androidterminal.core.domain.model.receipt.TransactionModifier
import com.ationet.androidterminal.core.domain.repository.ReceiptRepository
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.core.util.receiptData
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateSaleReceiptUseCase @Inject constructor(
    private val receiptRepository: ReceiptRepository,
    private val getConfiguration: GetConfiguration,
) {
    suspend operator fun invoke(
        requestDate: Instant,
        primaryTrack: String,
        productName: String,
        productCode: String,
        productUnitPrice: Double,
        inputType: ReceiptProductInputType,
        quantity: Double,
        amount: Double,
        response: NativeRequest,
        secondaryTrack: String?,
        batchId: Int
    ): Receipt {
        val configuration = getConfiguration.invoke()
        val receipt = Receipt(
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
                dateTime = requestDate.toLocalDateTime(TimeZone.currentSystemDefault()),
                name = ReceiptTransactionTypeName.Sale
            ),
            site = ReceiptSite(
                name = configuration.site.siteName,
                code = configuration.site.siteCode,
                address = configuration.site.siteAddress,
                cuit = configuration.site.siteCuit,
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
                transactionAmount = response.companyPrice?.transactionAmount ?: amount,
                responseCode = response.responseCode,
                responseText = response.longResponseText ?: response.responseMessage ?: response.responseText.orEmpty(),
                terminalId = configuration.ationet.terminalId,
                primaryTrack = primaryTrack,
                secondaryTrack = secondaryTrack,
                authorizationCode = response.authorizationCode,
                invoice = response.invoiceNumber,
                transactionSequenceNumber = response.transactionSequenceNumber,
                receiptData = try {
                    receiptData(response.receiptData)
                } catch (e: Throwable) {
                    currentCoroutineContext().ensureActive()

                    Log.w(TAG, "Completion receipt: Failed to decode receipt data", e)
                    ReceiptData(
                        customerPan = null,
                        companyName = null,
                        customerPlate = null,
                        customerDriverId = null,
                        customerDriverName = null,
                        customerVehicleCode = null
                    )
                },
                product = ReceiptProduct(
                    inputType = inputType,
                    name = productName,
                    code = productCode,
                    unitPrice = productUnitPrice,
                    quantity = quantity,
                    amount = amount,
                    modifiers = response.companyPrice?.modifiers?.map { priceModifier ->
                        TransactionModifier(
                            type = when (priceModifier.type) {
                                0 -> ReceiptModifierType.Percentage
                                1 -> ReceiptModifierType.FixedTransaction
                                else -> ReceiptModifierType.FixedUnit
                            },
                            modifierClass = when (priceModifier.modifierClass) {
                                0 -> ReceiptModifierClass.Discount
                                else -> ReceiptModifierClass.Surcharge
                            },
                            value = priceModifier.value,
                            total = priceModifier.total,
                            base = priceModifier.base
                        )
                    } ?: emptyList(),
                ),
                unitOfMeasure = configuration.fuelMeasureUnit,
                currencySymbol = configuration.currencyFormat,
            ),
            batchId = batchId
        )

        val receiptId = receiptRepository.saveReceipt(receipt)

        Log.i(TAG, "Sale receipt: Created receipt #$receiptId")
        return receipt.copy(id = receiptId)
    }

    private companion object {
        private const val TAG: String = "SaleReceiptUC"
    }
}