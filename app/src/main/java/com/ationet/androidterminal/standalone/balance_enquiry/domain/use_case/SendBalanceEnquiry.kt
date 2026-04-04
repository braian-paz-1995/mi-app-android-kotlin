package com.ationet.androidterminal.standalone.balance_enquiry.domain.use_case

import android.content.Context
import androidx.core.content.ContextCompat
import com.atio.log.Logger
import com.atio.log.util.error
import com.atio.log.util.info
import com.atio.log.util.warn
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.data.remote.ationet.model.ResponseCodes
import com.ationet.androidterminal.core.domain.exception.HostUrlNotConfiguredException
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
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
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestBalanceInquiry
import com.ationet.androidterminal.core.domain.use_case.batch.GetLastOpenBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.core.domain.util.createCommunicationErrorReceipt
import com.ationet.androidterminal.core.domain.util.createErrorReceipt
import com.ationet.androidterminal.core.domain.util.displayResponseText
import com.ationet.androidterminal.core.util.receiptData
import com.ationet.androidterminal.standalone.balance_enquiry.data.local.BalanceEnquiryStateRepository
import com.ationet.androidterminal.standalone.balance_enquiry.data.local.Quantity
import com.ationet.androidterminal.standalone.balance_enquiry.presentation.summary.SummaryData
import com.ationet.androidterminal.standalone.sale.domain.model.ProductStandAlone
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

sealed interface TransactionResult {
    data class Success(val summaryData: SummaryData) : TransactionResult
    data class Error(val code: String = "", val message: String) : TransactionResult
    data object CommunicationError : TransactionResult
}

@ViewModelScoped
class SendBalanceEnquiry @Inject constructor(
    @ApplicationContext private val context: Context,
    private val requestBalanceInquiry: RequestBalanceInquiry,
    private val operationStateRepository: BalanceEnquiryStateRepository,
    private val getConfiguration: GetConfiguration,
    private val getLastOpenBatchUseCase: GetLastOpenBatchUseCase
) {
    companion object {
        private val logger = Logger("SendBalanceEnquiryUseCase")
    }

    suspend operator fun invoke(): TransactionResult {
        logger.info("Sending balance enquiry")
        val operationState = operationStateRepository.getState()
        logger.info("Operation state: $operationState")
        val currentInstant = Clock.System.now()
        val configuration = getConfiguration()
        val batchId = getLastOpenBatchUseCase()?.id ?: 0

        if (operationState.product !is ProductStandAlone) {
            logger.error("Product is not a ProductStandAlone")
            val receipt = createErrorReceipt(
                transactionName = ReceiptTransactionTypeName.BalanceEnquiry,
                configuration = configuration,
                currentInstant = currentInstant,
                primaryTrack = operationState.primaryTrack,
                secondaryTrack = null,
                inputType = ReceiptProductInputType.Quantity,
                productName = "",
                productCode = "",
                productUnitPrice = 0.0,
                quantity = null,
                amount = null,
                responseCode = null,
                responseText = ContextCompat.getContextForLanguage(context).getString(R.string.product_not_found),
                receiptData = ReceiptData(),
                batchId = batchId,
                authorizationCode = null,
                invoiceNumber = null
            )
            operationStateRepository.updateState { it.copy(receipt = receipt) }
            return TransactionResult.Error(message = ContextCompat.getContextForLanguage(context).getString(R.string.product_not_found))
        }

        val response = try {
            requestBalanceInquiry.invoke(
                localDateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
                primaryTrack = operationState.primaryTrack,
                pumpNumber = 0,
                productCode = operationState.product.code,
                unitPrice = operationState.product.unitPrice,
            )
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()
            logger.error("Error sending balance enquiry", e)

            val receipt = createCommunicationErrorReceipt(
                transactionName = ReceiptTransactionTypeName.BalanceEnquiry,
                context = context,
                configuration = configuration,
                currentInstant = currentInstant,
                primaryTrack = operationState.primaryTrack,
                secondaryTrack = null,
                inputType = ReceiptProductInputType.Quantity,
                productName = operationState.product.name,
                productCode = operationState.product.code,
                productUnitPrice = operationState.product.unitPrice,
                quantity = null,
                amount = null,
                batchId = batchId
            )
            operationStateRepository.updateState { it.copy(receipt = receipt) }
            return TransactionResult.CommunicationError
        }

        val nativeResponse = response.getOrNull()

        if (response.isFailure) {
            val exception = response.exceptionOrNull()
            if (exception != null) {
                logger.error("Error sending balance enquiry", exception)
            } else {
                logger.error("Error sending balance enquiry")
            }

            return if (exception is TerminalIdNotConfiguredException || exception is HostUrlNotConfiguredException) {
                val receipt = createErrorReceipt(
                    transactionName = ReceiptTransactionTypeName.BalanceEnquiry,
                    configuration = configuration,
                    currentInstant = currentInstant,
                    primaryTrack = operationState.primaryTrack,
                    secondaryTrack = null,
                    inputType = ReceiptProductInputType.Quantity,
                    productName = operationState.product.name,
                    productCode = operationState.product.code,
                    productUnitPrice = operationState.product.unitPrice,
                    quantity = null,
                    amount = null,
                    responseCode = null,
                    responseText = exception.message.orEmpty(),
                    receiptData = ReceiptData(),
                    batchId = batchId,
                    authorizationCode = null,
                    invoiceNumber = null
                )

                operationStateRepository.updateState { it.copy(receipt = receipt) }

                return TransactionResult.Error(message = exception.message.orEmpty())
            } else if (nativeResponse != null) {
                val receipt = createErrorReceipt(
                    transactionName = ReceiptTransactionTypeName.BalanceEnquiry,
                    configuration = configuration,
                    currentInstant = currentInstant,
                    primaryTrack = operationState.primaryTrack,
                    secondaryTrack = null,
                    inputType = ReceiptProductInputType.Quantity,
                    productName = operationState.product.name,
                    productCode = operationState.product.code,
                    productUnitPrice = operationState.product.unitPrice,
                    quantity = null,
                    amount = null,
                    responseCode = nativeResponse.responseCode,
                    responseText = nativeResponse.displayResponseText,
                    receiptData = ReceiptData(),
                    batchId = batchId,
                    authorizationCode = nativeResponse.authorizationCode,
                    invoiceNumber = nativeResponse.invoiceNumber
                )

                operationStateRepository.updateState { it.copy(receipt = receipt) }

                TransactionResult.Error(
                    code = nativeResponse.responseCode.orEmpty(),
                    message = nativeResponse.displayResponseText
                )
            } else {
                val receipt = createCommunicationErrorReceipt(
                    transactionName = ReceiptTransactionTypeName.BalanceEnquiry,
                    context = context,
                    configuration = configuration,
                    currentInstant = currentInstant,
                    primaryTrack = operationState.primaryTrack,
                    secondaryTrack = null,
                    inputType = ReceiptProductInputType.Quantity,
                    productName = operationState.product.name,
                    productCode = operationState.product.code,
                    productUnitPrice = operationState.product.unitPrice,
                    quantity = null,
                    amount = null,
                    batchId = batchId
                )

                operationStateRepository.updateState { it.copy(receipt = receipt) }

                TransactionResult.CommunicationError
            }
        }

        if (nativeResponse == null) {
            val receipt = createCommunicationErrorReceipt(
                transactionName = ReceiptTransactionTypeName.BalanceEnquiry,
                context = context,
                configuration = configuration,
                currentInstant = currentInstant,
                primaryTrack = operationState.primaryTrack,
                secondaryTrack = null,
                inputType = ReceiptProductInputType.Quantity,
                productName = operationState.product.name,
                productCode = operationState.product.code,
                productUnitPrice = operationState.product.unitPrice,
                quantity = null,
                amount = null,
                batchId = batchId
            )

            operationStateRepository.updateState { it.copy(receipt = receipt) }

            return TransactionResult.CommunicationError
        }

        if (nativeResponse.responseCode != ResponseCodes.Authorized) {
            val receipt = createErrorReceipt(
                transactionName = ReceiptTransactionTypeName.BalanceEnquiry,
                configuration = configuration,
                currentInstant = currentInstant,
                primaryTrack = operationState.primaryTrack,
                secondaryTrack = null,
                inputType = ReceiptProductInputType.Quantity,
                productName = operationState.product.name,
                productCode = operationState.product.code,
                productUnitPrice = operationState.product.unitPrice,
                quantity = null,
                amount = null,
                responseCode = nativeResponse.responseCode,
                responseText = nativeResponse.displayResponseText,
                receiptData = try {
                    receiptData(nativeResponse.receiptData)
                } catch (e: Throwable) {
                    logger.error("Error decoding receipt data", e)
                    ReceiptData()
                },
                batchId = batchId,
                authorizationCode = nativeResponse.authorizationCode,
                invoiceNumber = nativeResponse.invoiceNumber
            )

            operationStateRepository.updateState { it.copy(receipt = receipt) }

            return TransactionResult.Error(
                code = nativeResponse.responseCode.orEmpty(),
                message = nativeResponse.displayResponseText
            )
        }

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
                dateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
                name = ReceiptTransactionTypeName.BalanceEnquiry
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
                responseCode = nativeResponse.responseCode,
                responseText = nativeResponse.longResponseText ?: nativeResponse.responseMessage ?: nativeResponse.responseText.orEmpty(),
                terminalId = configuration.ationet.terminalId,
                primaryTrack = operationState.primaryTrack,
                secondaryTrack = null,
                authorizationCode = nativeResponse.authorizationCode,
                invoice = nativeResponse.invoiceNumber,
                transactionSequenceNumber = nativeResponse.transactionSequenceNumber,
                receiptData = try {
                    receiptData(nativeResponse.receiptData)
                } catch (e: Throwable) {
                    currentCoroutineContext().ensureActive()
                    logger.warn("Balance Enquiry receipt: Failed to decode receipt data", e)
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
                    inputType = ReceiptProductInputType.Quantity,
                    name = operationState.product.name,
                    code = operationState.product.code,
                    unitPrice = operationState.product.unitPrice,
                    quantity = nativeResponse.productQuantity?.toDoubleOrNull(),
                    amount = nativeResponse.productAmount?.toDoubleOrNull(),
                    modifiers = nativeResponse.companyPrice?.modifiers?.map { priceModifier ->
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

        operationStateRepository.updateState {
            it.copy(
                quantity = if (nativeResponse.productAmount != null) {
                    Quantity(value = nativeResponse.productAmount.toDoubleOrNull() ?: 0.0, inputType = Quantity.InputType.Amount)
                } else {
                    Quantity(value = nativeResponse.productQuantity?.toDoubleOrNull() ?: 0.0, inputType = Quantity.InputType.Quantity)
                },
                authorizationCode = nativeResponse.authorizationCode.toString(),
                receipt = receipt
            )
        }
        logger.info("Balance enquiry sent successfully")
        return TransactionResult.Success(
            SummaryData(
                date = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
                authorizationCode = nativeResponse.authorizationCode.toString(),
                productName = operationState.product.name,
                quantity = if (nativeResponse.productAmount != null) {
                    Quantity(value = nativeResponse.productAmount.toDoubleOrNull() ?: 0.0, inputType = Quantity.InputType.Amount)
                } else {
                    Quantity(value = nativeResponse.productQuantity?.toDoubleOrNull() ?: 0.0, inputType = Quantity.InputType.Quantity)
                },
                currencyFormat = configuration.currencyFormat,
                fuelMeasureUnit = configuration.fuelMeasureUnit,
                language = configuration.language
            )
        )
    }
}