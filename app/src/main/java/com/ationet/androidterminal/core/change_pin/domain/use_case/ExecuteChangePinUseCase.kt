package com.ationet.androidterminal.core.change_pin.domain.use_case

import android.content.Context
import com.atio.log.Logger
import com.atio.log.util.e
import com.atio.log.util.error
import com.atio.log.util.warn
import com.ationet.androidterminal.core.change_pin.data.local.ChangePinOperationStateRepository
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
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestChangePin
import com.ationet.androidterminal.core.domain.use_case.batch.GetLastOpenBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.core.domain.util.createCommunicationErrorReceipt
import com.ationet.androidterminal.core.domain.util.createErrorReceipt
import com.ationet.androidterminal.core.domain.util.displayResponseText
import com.ationet.androidterminal.core.util.receiptData
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@ViewModelScoped
class ExecuteChangePinUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val requestChangePin: RequestChangePin,
    private val operationStateRepository: ChangePinOperationStateRepository,
    private val getConfiguration: GetConfiguration,
    private val getLastOpenBatchUseCase: GetLastOpenBatchUseCase
) {
    companion object {
        val logger = Logger("ExecuteChangePinUseCase")
    }

    val track = operationStateRepository.getState().track

    suspend operator fun invoke(
        primaryPin: String,
        newPin: String,
        confirmationPin: String,
        currentInstant: Instant
    ): ChangePinResult {
        val operationState = operationStateRepository.getState()
        val configuration = getConfiguration()
        val batchId = getLastOpenBatchUseCase()?.id ?: 0
        val response = try {
            requestChangePin.invoke(
                track = track?.let { it } ?: "",
                primaryPin = primaryPin,
                newPin = newPin,
                confirmationPin = confirmationPin
            )
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()
            val receipt = createCommunicationErrorReceipt(
                transactionName = ReceiptTransactionTypeName.ChangePin,
                context = context,
                configuration = configuration,
                currentInstant = currentInstant,
                primaryTrack = operationState.track.orEmpty(),
                secondaryTrack = null,
                inputType = ReceiptProductInputType.Quantity,
                productName = "",
                productCode = "",
                productUnitPrice = null,
                quantity = null,
                amount = null,
                batchId = batchId
            )
            operationStateRepository.updateState { it.copy(receipt = receipt) }
            logger.e("Communication error with the server")
            return ChangePinResult.CommunicationError
        }
        val nativeResponse = response.getOrNull()

        if (response.isFailure) {
            val exception = response.exceptionOrNull()
            if (exception != null) {
                logger.error("Error executing change pin:", exception)
            } else {
                logger.error("Error executing change pin")
            }
            return if (exception is TerminalIdNotConfiguredException || exception is HostUrlNotConfiguredException) {
                logger.error("Error executing change pin:", exception)

                val receipt = createErrorReceipt(
                    transactionName = ReceiptTransactionTypeName.ChangePin,
                    configuration = configuration,
                    currentInstant = currentInstant,
                    primaryTrack = operationState.track.orEmpty(),
                    secondaryTrack = null,
                    inputType = ReceiptProductInputType.Quantity,
                    productName = "",
                    productCode = "",
                    productUnitPrice = null,
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
                return ChangePinResult.Failure(
                    message = exception.message.orEmpty()
                )
            } else if (nativeResponse != null) {
                val receipt = createErrorReceipt(
                    transactionName = ReceiptTransactionTypeName.ChangePin,
                    configuration = configuration,
                    currentInstant = currentInstant,
                    primaryTrack = operationState.track.orEmpty(),
                    secondaryTrack = null,
                    inputType = ReceiptProductInputType.Quantity,
                    productName = "",
                    productCode = "",
                    productUnitPrice = null,
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

                ChangePinResult.Failure(
                    code = nativeResponse.responseCode.orEmpty(),
                    message = nativeResponse.longResponseText ?: nativeResponse.responseMessage,
                    authorizationCode = nativeResponse.authorizationCode,
                    date = nativeResponse.localTransactionDate,
                    time = nativeResponse.localTransactionTime
                        ?: nativeResponse.responseText.orEmpty()
                )
            } else {
                val receipt = createCommunicationErrorReceipt(
                    transactionName = ReceiptTransactionTypeName.ChangePin,
                    context = context,
                    configuration = configuration,
                    currentInstant = currentInstant,
                    primaryTrack = operationState.track.orEmpty(),
                    secondaryTrack = null,
                    inputType = ReceiptProductInputType.Quantity,
                    productName = "",
                    productCode = "",
                    productUnitPrice = null,
                    quantity = null,
                    amount = null,
                    batchId = batchId
                )

                operationStateRepository.updateState { it.copy(receipt = receipt) }

                ChangePinResult.CommunicationError
            }
        }

        if (nativeResponse == null) {
            val receipt = createCommunicationErrorReceipt(
                transactionName = ReceiptTransactionTypeName.ChangePin,
                context = context,
                configuration = configuration,
                currentInstant = currentInstant,
                primaryTrack = operationState.track.orEmpty(),
                secondaryTrack = null,
                inputType = ReceiptProductInputType.Quantity,
                productName = "",
                productCode = "",
                productUnitPrice = null,
                quantity = null,
                amount = null,
                batchId = batchId
            )

            operationStateRepository.updateState { it.copy(receipt = receipt) }

            return ChangePinResult.CommunicationError
        }

        if (nativeResponse.responseCode != ResponseCodes.Authorized) {
            val receipt = createErrorReceipt(
                transactionName = ReceiptTransactionTypeName.ChangePin,
                configuration = configuration,
                currentInstant = currentInstant,
                primaryTrack = operationState.track.orEmpty(),
                secondaryTrack = null,
                inputType = ReceiptProductInputType.Quantity,
                productName = "",
                productCode = "",
                productUnitPrice = null,
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

            return ChangePinResult.Failure(
                code = nativeResponse.responseCode.orEmpty(),
                message = nativeResponse.longResponseText ?: nativeResponse.responseMessage ?: nativeResponse.responseText.orEmpty(),
                authorizationCode = nativeResponse.authorizationCode?.let { it } ?: null,
                date = nativeResponse.localTransactionDate?.let { it } ?: null,
                time = nativeResponse.localTransactionTime
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
                name = ReceiptTransactionTypeName.ChangePin
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
                    printProductInColumns = isDetailInColumn,
                )
            },
            transactionData = ReceiptTransactionData(
                responseCode = nativeResponse.responseCode,
                responseText = nativeResponse.longResponseText ?: nativeResponse.responseMessage ?: nativeResponse.responseText.orEmpty(),
                terminalId = configuration.ationet.terminalId,
                primaryTrack = operationState.track.orEmpty(),
                secondaryTrack = null,
                authorizationCode = nativeResponse.authorizationCode,
                invoice = nativeResponse.invoiceNumber,
                transactionSequenceNumber = nativeResponse.transactionSequenceNumber,
                receiptData = try {
                    receiptData(nativeResponse.receiptData)
                } catch (e: Throwable) {
                    currentCoroutineContext().ensureActive()
                    logger.warn("Change Pin receipt: Failed to decode receipt data", e)
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
                    unitPrice = null,
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

        operationStateRepository.updateState { it.copy(receipt = receipt) }

        return ChangePinResult.Success(
            authorizationCode = nativeResponse.authorizationCode,
            date = nativeResponse.localTransactionDate?.let { it } ?: null,
            time = nativeResponse.localTransactionTime
        )
    }
}

sealed interface ChangePinResult {
    data class Success(
        val authorizationCode: String?,
        val date: String?,
        val time: String?
    ) : ChangePinResult

    data class Failure(
        val code: String = "",
        val message: String?,
        val authorizationCode: String? = null,
        val date: String? = null,
        val time: String? = null,
    ) : ChangePinResult

    data object CommunicationError : ChangePinResult
}