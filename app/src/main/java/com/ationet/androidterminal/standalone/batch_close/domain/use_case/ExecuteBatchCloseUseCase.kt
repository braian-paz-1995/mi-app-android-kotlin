package com.ationet.androidterminal.standalone.batch_close.domain.use_case

import android.content.Context
import androidx.core.content.ContextCompat
import com.atio.log.Logger
import com.atio.log.util.error
import com.atio.log.util.warn
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.data.remote.ationet.model.ResponseCodes
import com.ationet.androidterminal.core.domain.exception.HostUrlNotConfiguredException
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.model.batch.Batch
import com.ationet.androidterminal.core.domain.model.receipt.BatchCloseData
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
import com.ationet.androidterminal.core.domain.use_case.ationet.GetNextTransactionSequenceNumber
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestBatchClose
import com.ationet.androidterminal.core.domain.use_case.batch.CloseBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.batch.GetLastOpenBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.batch.GetTransactionsRechargeCCWithBatchIdUseCase
import com.ationet.androidterminal.core.domain.use_case.batch.GetTransactionsReverseCCWithBatchIdUseCase
import com.ationet.androidterminal.core.domain.use_case.batch.GetTransactionsWithBatchIdUseCase
import com.ationet.androidterminal.core.domain.use_case.batch.GetVoidTransactionsWithBatchIdUseCase
import com.ationet.androidterminal.core.domain.use_case.batch.OpenBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.core.domain.worker.ClearPendingAuthorizationWorker
import com.ationet.androidterminal.core.util.receiptData
import com.ationet.androidterminal.standalone.batch_close.data.local.BatchCloseStateRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

sealed interface BatchCloseResult {
    data class Success(val authorizationCode: String) : BatchCloseResult
    data class Failure(val code: String = "", val message: String) : BatchCloseResult
    data object CommunicationError : BatchCloseResult
}

@ViewModelScoped
class ExecuteBatchCloseUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getLastOpenBatchUseCase: GetLastOpenBatchUseCase,
    private val operationStateRepository: BatchCloseStateRepository,
    private val closeBatchUseCase: CloseBatchUseCase,
    private val openBatchUseCase: OpenBatchUseCase,
    private val getTransactionsWithBatchIdUseCase: GetTransactionsWithBatchIdUseCase,
    private val getTransactionsRechargeCCWithBatchIdUseCase: GetTransactionsRechargeCCWithBatchIdUseCase,
    private val getTransactionsReverseCCWithBatchIdUseCase: GetTransactionsReverseCCWithBatchIdUseCase,
    private val getBatchClosesWithBatchIdUseCase: GetVoidTransactionsWithBatchIdUseCase,
    private val getNextTransactionSequenceNumber: GetNextTransactionSequenceNumber,
    private val getConfiguration: GetConfiguration,
    private val requestBatchClose: RequestBatchClose
) {
    companion object {
        private val logger = Logger("ExecuteBatchCloseUseCase")
    }

    suspend operator fun invoke(hasPreAuthorizations: Boolean): BatchCloseResult {
        val batch = getLastOpenBatchUseCase.invoke()
        val configuration = getConfiguration()

        if (batch == null) {
            logger.error("No batch to close")
            return BatchCloseResult.CommunicationError
        }

        val sales = getTransactionsWithBatchIdUseCase.invoke(
            batchId = batch.id.toLong(),
            controllerType = configuration.controllerType
        )
        val rechargeCC = getTransactionsRechargeCCWithBatchIdUseCase.invoke(
            batchId = batch.id.toLong(),
            controllerType = configuration.controllerType
        )
        val reverseCC = getTransactionsReverseCCWithBatchIdUseCase.invoke(
            batchId = batch.id.toLong(),
            controllerType = configuration.controllerType
        )
        val voids = getBatchClosesWithBatchIdUseCase.invoke(batchId = batch.id.toLong())

        val currentInstant = Clock.System.now()
        val salesCounter = sales.size
        val salesTotal = sales.sumOf { sale -> sale.amount }
        val rechargeCCCounter = rechargeCC.size
        val rechargeCCTotal = rechargeCC.sumOf { rechargeCC -> rechargeCC.amount }
        val reverseCCCounter = reverseCC.size
        val reverseCCTotal = reverseCC.sumOf { reverseCC -> reverseCC.amount }
        val voidedCounter = voids.size
        val voidedTotal = voids.sumOf { void -> void.amount }
        val batchNumberFormat = LocalDateTime.Format {
            yearTwoDigits(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year)
            monthNumber()
            dayOfMonth()
            hour()
            minute()
        }

        val response = try {
            requestBatchClose.invoke(
                transactionSequenceNumber = getNextTransactionSequenceNumber(),
                localTransactionDate = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
                localTransactionTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
                salesCounter = salesCounter,
                salesTotal = salesTotal,
                voidedCounter = voidedCounter,
                voidedTotal = voidedTotal,
                batchNumber = batchNumberFormat.format(batch.transactionDateTime)
            )
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            logger.error("Error closing batch", e)

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
                    name = ReceiptTransactionTypeName.BatchClose
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
                        .getString(R.string.communication_error_with_controller),
                    terminalId = configuration.ationet.terminalId,
                    primaryTrack = "",
                    secondaryTrack = null,
                    authorizationCode = null,
                    invoice = null,
                    transactionSequenceNumber = null,
                    receiptData = ReceiptData(),
                    product = ReceiptProduct(
                        inputType = ReceiptProductInputType.Quantity,
                        name = "",
                        code = "",
                        unitPrice = 0.0,
                        quantity = 0.0,
                        amount = 0.0,
                        modifiers = emptyList()
                    ),
                    unitOfMeasure = configuration.fuelMeasureUnit,
                    currencySymbol = configuration.currencyFormat,

                ),
                batchId = batch.id
            )

            operationStateRepository.updateState {
                it.copy(
                    receipt = receipt
                )
            }

            return BatchCloseResult.CommunicationError
        }
        val nativeResponse = response.getOrNull()

        if (response.isFailure) {
            val exception = response.exceptionOrNull()
            if (exception != null) {
                logger.error("Error executing batch close", exception)
            } else {
                logger.error("Error executing  batch close")
            }
            return if (exception is TerminalIdNotConfiguredException || exception is HostUrlNotConfiguredException) {
                logger.error("Error executing batch close", exception)
                return BatchCloseResult.Failure(message = exception.message.orEmpty())
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
                        name = ReceiptTransactionTypeName.BatchClose
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
                            .getString(R.string.communication_error_with_controller),
                        terminalId = configuration.ationet.terminalId,
                        primaryTrack = "",
                        secondaryTrack = null,
                        authorizationCode = null,
                        invoice = null,
                        transactionSequenceNumber = null,
                        receiptData = ReceiptData(),
                        product = ReceiptProduct(
                            inputType = ReceiptProductInputType.Quantity,
                            name = "",
                            code = "",
                            unitPrice = 0.0,
                            quantity = 0.0,
                            amount = 0.0,
                            modifiers = emptyList()
                        ),
                        unitOfMeasure = configuration.fuelMeasureUnit,
                        currencySymbol = configuration.currencyFormat
                    ),
                    batchId = batch.id
                )

                operationStateRepository.updateState {
                    it.copy(
                        receipt = receipt
                    )
                }

                BatchCloseResult.Failure(
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
                        name = ReceiptTransactionTypeName.BatchClose
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
                            .getString(R.string.communication_error_with_controller),
                        terminalId = configuration.ationet.terminalId,
                        primaryTrack = "",
                        secondaryTrack = null,
                        authorizationCode = null,
                        invoice = null,
                        transactionSequenceNumber = null,
                        receiptData = ReceiptData(),
                        product = ReceiptProduct(
                            inputType = ReceiptProductInputType.Quantity,
                            name = "",
                            code = "",
                            unitPrice = 0.0,
                            quantity = 0.0,
                            amount = 0.0,
                            modifiers = emptyList()
                        ),
                        unitOfMeasure = configuration.fuelMeasureUnit,
                        currencySymbol = configuration.currencyFormat
                    ),
                    batchId = batch.id
                )

                operationStateRepository.updateState {
                    it.copy(
                        receipt = receipt
                    )
                }
                BatchCloseResult.CommunicationError
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
                    name = ReceiptTransactionTypeName.BatchClose
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
                        .getString(R.string.communication_error_with_controller),
                    terminalId = configuration.ationet.terminalId,
                    primaryTrack = "",
                    secondaryTrack = null,
                    authorizationCode = null,
                    invoice = null,
                    transactionSequenceNumber = null,
                    receiptData = ReceiptData(),
                    product = ReceiptProduct(
                        inputType = ReceiptProductInputType.Quantity,
                        name = "",
                        code = "",
                        unitPrice = 0.0,
                        quantity = 0.0,
                        amount = 0.0,
                        modifiers = emptyList()
                    ),
                    unitOfMeasure = configuration.fuelMeasureUnit,
                    currencySymbol = configuration.currencyFormat
                ),
                batchId = batch.id
            )

            operationStateRepository.updateState {
                it.copy(
                    receipt = receipt
                )
            }
            return BatchCloseResult.CommunicationError
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
                    name = ReceiptTransactionTypeName.BatchClose
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
                    responseText = nativeResponse.longResponseText ?: nativeResponse.responseMessage
                    ?: nativeResponse.responseText.orEmpty(),
                    terminalId = configuration.ationet.terminalId,
                    primaryTrack = "",
                    secondaryTrack = null,
                    authorizationCode = nativeResponse.authorizationCode,
                    invoice = nativeResponse.invoiceNumber,
                    transactionSequenceNumber = nativeResponse.transactionSequenceNumber,
                    receiptData = try {
                        receiptData(nativeResponse.receiptData)
                    } catch (e: Throwable) {
                        logger.warn("Batch close receipt: Failed to decode receipt data", e)
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
                        name = "",
                        code = "",
                        unitPrice = 0.0,
                        quantity = 0.0,
                        amount = 0.0,
                        modifiers = emptyList()
                    ),
                    unitOfMeasure = configuration.fuelMeasureUnit,
                    currencySymbol = configuration.currencyFormat,
                ),
                batchId = batch.id
            )

            operationStateRepository.updateState {
                it.copy(
                    receipt = receipt
                )
            }

            return BatchCloseResult.Failure(
                code = nativeResponse.responseCode.orEmpty(),
                message = nativeResponse.longResponseText ?: nativeResponse.responseMessage
                ?: nativeResponse.responseText.orEmpty()
            )
        }

        closeBatchUseCase.invoke(batch.id.toLong())
        openBatchUseCase.invoke(
            Batch(
                transactionDateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
                state = Batch.State.OPEN
            )
        )

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
                name = ReceiptTransactionTypeName.BatchClose
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
                batchNumber = batchNumberFormat.format(batch.transactionDateTime),
                responseCode = nativeResponse.responseCode,
                responseText = nativeResponse.longResponseText ?: nativeResponse.responseMessage
                ?: nativeResponse.responseText.orEmpty(),
                terminalId = configuration.ationet.terminalId,
                primaryTrack = "",
                secondaryTrack = null,
                authorizationCode = nativeResponse.authorizationCode,
                invoice = nativeResponse.invoiceNumber,
                transactionSequenceNumber = nativeResponse.transactionSequenceNumber,
                receiptData = receiptData(nativeResponse.receiptData),
                product = ReceiptProduct(
                    inputType = ReceiptProductInputType.Quantity,
                    name = "",
                    code = "",
                    unitPrice = 0.0,
                    quantity = 0.0,
                    amount = 0.0,
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
                salesCounter = nativeResponse.salesCounter,
                salesTotal = nativeResponse.salesTotal,
                voidedCounter = nativeResponse.voidedCounter,
                voidedTotal = nativeResponse.voidedTotal,
                rechargeCCCounter = rechargeCCCounter,
                rechargeCCTotal = rechargeCCTotal,
                reverseCCCounter = reverseCCCounter,
                reverseCCTotal = reverseCCTotal,
                batchClose = BatchCloseData(
                    sales = sales,
                    rechargeCC = rechargeCC,
                    reverseCC = reverseCC,
                    voids = voids
                )
            ),
            batchId = batch.id
        )

        operationStateRepository.updateState {
            it.copy(
                batchId = batchNumberFormat.format(batch.transactionDateTime),
                countSales = salesCounter,
                totalSales = salesTotal,
                countVoid = voidedCounter,
                totalVoid = voidedTotal,
                countRechargeCC = rechargeCCCounter,
                totalRechargeCC = rechargeCCTotal,
                countReverseCC = reverseCCCounter,
                totalReverseCC = reverseCCTotal,
                receipt = receipt,
            )
        }

        if (hasPreAuthorizations) {
            ClearPendingAuthorizationWorker.enqueue(context)
        }

        return BatchCloseResult.Success(nativeResponse.authorizationCode.orEmpty())
    }
}