package com.ationet.androidterminal.standalone.void_transaction.domain.use_case

import android.content.Context
import androidx.core.content.ContextCompat
import com.atio.log.Logger
import com.atio.log.util.debug
import com.atio.log.util.error
import com.atio.log.util.warn
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.data.remote.ationet.model.OriginalData
import com.ationet.androidterminal.core.data.remote.ationet.model.ResponseCodes
import com.ationet.androidterminal.core.data.remote.ationet.transactionDate
import com.ationet.androidterminal.core.data.remote.ationet.transactionTime
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
import com.ationet.androidterminal.core.domain.model.transaction.TransactionType
import com.ationet.androidterminal.core.domain.model.transaction.TransactionView
import com.ationet.androidterminal.core.domain.model.void_transaction.VoidTransaction
import com.ationet.androidterminal.core.domain.repository.VoidRepository
import com.ationet.androidterminal.core.domain.use_case.ationet.GetNextTransactionSequenceNumber
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestVoidTransaction
import com.ationet.androidterminal.core.domain.use_case.batch.GetLastOpenBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.core.util.receiptData
import com.ationet.androidterminal.standalone.void_transaction.data.local.VoidTransactionStateRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

sealed interface TransactionResult {
    data class Success(val authorizationCode: String) : TransactionResult
    data class Error(val code: String = "", val message: String) : TransactionResult
    data object CommunicationError : TransactionResult
}

@ViewModelScoped
class ExecuteVoidTransaction @Inject constructor(
    @ApplicationContext private val context: Context,
    private val requestVoidTransaction: RequestVoidTransaction,
    private val getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
    private val getConfiguration: GetConfiguration,
    private val operationStateRepository: VoidTransactionStateRepository,
    private val voidRepository: VoidRepository,
    private val getLastOpenBatchUseCase: GetLastOpenBatchUseCase
) {
    companion object {
        val logger = Logger("ExecuteVoidTransaction")
    }

    suspend operator fun invoke(transactionView: TransactionView): TransactionResult {

        val configuration = getConfiguration()
        val currentInstant = Clock.System.now()
        val batchId = getLastOpenBatchUseCase.invoke()?.id ?: 0

        logger.debug("Executing void transaction for voidDetail: $transactionView")
        val response = try {
            requestVoidTransaction.invoke(
                transactionSequenceNumber = getNextTransactionSequenceNumber(),
                localTransactionDate = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
                localTransactionTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
                primaryTrack = transactionView.transactionData.primaryTrack,
                originalData = OriginalData(
                    transactionSequenceNumber = transactionView.transactionSequenceNumber.toString(),
                    localTransactionDate = transactionView.dateTime.transactionDate,
                    localTransactionTime = transactionView.dateTime.transactionTime,
                    authorizationCode = transactionView.authorizationCode,
                    transactionCode = if (transactionView.type == TransactionType.Sale) "200" else "120"
                )
            )
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            logger.error("Error executing void transaction for voidDetail: $transactionView", e)

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
                    name = ReceiptTransactionTypeName.VoidTransaction
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
                    responseCode = null,
                    responseText = ContextCompat.getContextForLanguage(context)
                        .getString(R.string.communication_failure),
                    terminalId = configuration.ationet.terminalId,
                    primaryTrack = transactionView.transactionData.primaryTrack.orEmpty(),
                    secondaryTrack = null,
                    authorizationCode = null,
                    invoice = null,
                    transactionSequenceNumber = null,
                    receiptData = ReceiptData(),
                    product = ReceiptProduct(
                        inputType = if (transactionView.transactionData.product.inputType == "Amount") ReceiptProductInputType.Amount else ReceiptProductInputType.Quantity,
                        name = transactionView.transactionData.product.name,
                        code = transactionView.transactionData.product.code,
                        unitPrice = transactionView.transactionData.product.unitPrice,
                        quantity = if (transactionView.transactionData.product.inputType == "Quantity") transactionView.transactionData.product.quantity else null,
                        amount = if (transactionView.transactionData.product.inputType == "Amount") transactionView.transactionData.product.quantity else null,
                        modifiers = emptyList()
                    ),
                    unitOfMeasure = configuration.fuelMeasureUnit,
                    currencySymbol = configuration.currencyFormat,
                ),
                batchId = batchId
            )

            operationStateRepository.updateState {
                it.copy(
                    receipt = receipt
                )
            }
            return TransactionResult.CommunicationError
        }

        val nativeResponse = response.getOrNull()

        if (response.isFailure) {
            val exception = response.exceptionOrNull()
            if (exception != null) {
                logger.error("Error executing void transaction for voidDetail: $transactionView", exception)
            } else {
                logger.error("Error executing void transaction for voidDetail: $transactionView")
            }
            return if (exception is TerminalIdNotConfiguredException || exception is HostUrlNotConfiguredException) {
                logger.error("Error executing void transaction for voidDetail: $transactionView", exception)
                return TransactionResult.Error(message = exception.message.orEmpty())
            } else if (nativeResponse != null) {
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
                        name = ReceiptTransactionTypeName.VoidTransaction
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
                        responseCode = null,
                        responseText = ContextCompat.getContextForLanguage(context)
                            .getString(R.string.communication_failure),
                        terminalId = configuration.ationet.terminalId,
                        primaryTrack = transactionView.transactionData.primaryTrack.orEmpty(),
                        secondaryTrack = null,
                        authorizationCode = null,
                        invoice = null,
                        transactionSequenceNumber = null,
                        receiptData = ReceiptData(),
                        product = ReceiptProduct(
                            inputType = if (transactionView.transactionData.product.inputType == "Amount") ReceiptProductInputType.Amount else ReceiptProductInputType.Quantity,
                            name = transactionView.transactionData.product.name,
                            code = transactionView.transactionData.product.code,
                            unitPrice = transactionView.transactionData.product.unitPrice,
                            quantity = if (transactionView.transactionData.product.inputType == "Quantity") transactionView.transactionData.product.quantity else null,
                            amount = if (transactionView.transactionData.product.inputType == "Amount") transactionView.transactionData.product.amount else null,
                            modifiers = emptyList()
                        ),
                        unitOfMeasure = configuration.fuelMeasureUnit,
                        currencySymbol = configuration.currencyFormat
                    ),
                    batchId = batchId
                )

                operationStateRepository.updateState {
                    it.copy(
                        receipt = receipt
                    )
                }

                TransactionResult.Error(
                    code = nativeResponse.responseCode.orEmpty(),
                    message = nativeResponse.longResponseText ?: nativeResponse.responseMessage
                    ?: nativeResponse.responseText.orEmpty()
                )
            } else {
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
                        name = ReceiptTransactionTypeName.VoidTransaction
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
                        responseCode = null,
                        responseText = ContextCompat.getContextForLanguage(context)
                            .getString(R.string.communication_failure),
                        terminalId = configuration.ationet.terminalId,
                        primaryTrack = transactionView.transactionData.primaryTrack.orEmpty(),
                        secondaryTrack = null,
                        authorizationCode = null,
                        invoice = null,
                        transactionSequenceNumber = null,
                        receiptData = ReceiptData(),
                        product = ReceiptProduct(
                            inputType = if (transactionView.transactionData.product.inputType == "Amount") ReceiptProductInputType.Amount else ReceiptProductInputType.Quantity,
                            name = transactionView.transactionData.product.name,
                            code = transactionView.transactionData.product.code,
                            unitPrice = transactionView.transactionData.product.unitPrice,
                            quantity = if (transactionView.transactionData.product.inputType == "Quantity") transactionView.transactionData.product.quantity else null,
                            amount = if (transactionView.transactionData.product.inputType == "Amount") transactionView.transactionData.product.amount else null,
                            modifiers = emptyList()
                        ),
                        unitOfMeasure = configuration.fuelMeasureUnit,
                        currencySymbol = configuration.currencyFormat
                    ),
                    batchId = batchId
                )

                operationStateRepository.updateState {
                    it.copy(
                        receipt = receipt
                    )
                }
                TransactionResult.CommunicationError
            }
        }

        if (nativeResponse == null) {
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
                    name = ReceiptTransactionTypeName.VoidTransaction
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
                    responseCode = null,
                    responseText = ContextCompat.getContextForLanguage(context)
                        .getString(R.string.communication_failure),
                    terminalId = configuration.ationet.terminalId,
                    primaryTrack = transactionView.transactionData.primaryTrack.orEmpty(),
                    secondaryTrack = null,
                    authorizationCode = null,
                    invoice = null,
                    transactionSequenceNumber = null,
                    receiptData = ReceiptData(),
                    product = ReceiptProduct(
                        inputType = if (transactionView.transactionData.product.inputType == "Amount") ReceiptProductInputType.Amount else ReceiptProductInputType.Quantity,
                        name = transactionView.transactionData.product.name,
                        code = transactionView.transactionData.product.code,
                        unitPrice = transactionView.transactionData.product.unitPrice,
                        quantity = if (transactionView.transactionData.product.inputType == "Quantity") transactionView.transactionData.product.quantity else null,
                        amount = if (transactionView.transactionData.product.inputType == "Amount") transactionView.transactionData.product.amount else null,
                        modifiers = emptyList()
                    ),
                    unitOfMeasure = configuration.fuelMeasureUnit,
                    currencySymbol = configuration.currencyFormat
                ),
                batchId = batchId
            )

            operationStateRepository.updateState {
                it.copy(
                    receipt = receipt
                )
            }
            return TransactionResult.CommunicationError
        }

        if (nativeResponse.responseCode != ResponseCodes.Authorized) {
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
                    name = ReceiptTransactionTypeName.VoidTransaction
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
                    responseCode = nativeResponse.responseCode,
                    responseText = nativeResponse.longResponseText ?: nativeResponse.responseMessage ?: nativeResponse.responseText.orEmpty(),
                    terminalId = configuration.ationet.terminalId,
                    primaryTrack = transactionView.transactionData.primaryTrack.orEmpty(),
                    secondaryTrack = null,
                    authorizationCode = nativeResponse.authorizationCode,
                    invoice = nativeResponse.invoiceNumber,
                    transactionSequenceNumber = nativeResponse.transactionSequenceNumber,
                    receiptData = try {
                        receiptData(nativeResponse.receiptData)
                    } catch (e: Throwable) {
                        logger.warn("Void Transaction receipt: Failed to decode receipt data", e)
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
                        inputType = if (transactionView.transactionData.product.inputType == "Amount") ReceiptProductInputType.Amount else ReceiptProductInputType.Quantity,
                        name = transactionView.transactionData.product.name,
                        code = transactionView.transactionData.product.code,
                        unitPrice = transactionView.transactionData.product.unitPrice,
                        quantity = if (transactionView.transactionData.product.inputType == "Quantity") transactionView.transactionData.product.quantity else null,
                        amount = if (transactionView.transactionData.product.inputType == "Amount") transactionView.transactionData.product.amount else null,
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
                    currencySymbol = configuration.currencyFormat
                ),
                batchId = batchId
            )

            operationStateRepository.updateState {
                it.copy(
                    receipt = receipt
                )
            }

            return TransactionResult.Error(
                code = nativeResponse.responseCode.orEmpty(),
                message = nativeResponse.longResponseText ?: nativeResponse.responseMessage ?: nativeResponse.responseText.orEmpty()
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
                name = ReceiptTransactionTypeName.VoidTransaction,

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
                responseCode = nativeResponse.responseCode,
                responseText = nativeResponse.longResponseText ?: nativeResponse.responseMessage ?: nativeResponse.responseText.orEmpty(),
                terminalId = configuration.ationet.terminalId,
                primaryTrack = transactionView.transactionData.primaryTrack,
                secondaryTrack = null,
                authorizationCode = nativeResponse.authorizationCode,
                invoice = nativeResponse.invoiceNumber,
                transactionSequenceNumber = nativeResponse.transactionSequenceNumber,
                receiptData = receiptData(nativeResponse.receiptData),
                product = ReceiptProduct(
                    inputType = if (transactionView.transactionData.product.inputType == "Amount") ReceiptProductInputType.Amount else ReceiptProductInputType.Quantity,
                    name = transactionView.transactionData.product.name,
                    code = transactionView.transactionData.product.code,
                    unitPrice = transactionView.transactionData.product.unitPrice,
                    quantity = if (transactionView.transactionData.product.inputType == "Quantity") transactionView.transactionData.product.quantity else transactionView.transactionData.product.amount.div(transactionView.transactionData.product.unitPrice),
                    amount = if (transactionView.transactionData.product.inputType == "Amount") transactionView.transactionData.product.amount else transactionView.transactionData.product.quantity * transactionView.transactionData.product.unitPrice,
                    modifiers = emptyList()
                ),
                unitOfMeasure = configuration.fuelMeasureUnit,
                currencySymbol = configuration.currencyFormat
            ),
            batchId = batchId
        )

        operationStateRepository.updateState {
            it.copy(
                receipt = receipt
            )
        }

        voidRepository.insertVoidTransaction(
            VoidTransaction(
                transactionId = transactionView.id,
                authorizationCode = nativeResponse.authorizationCode.orEmpty(),
                transactionSequenceNumber = nativeResponse.transactionSequenceNumber?.toInt() ?: 0
            )
        )

        return TransactionResult.Success(nativeResponse.authorizationCode.orEmpty())
    }
}