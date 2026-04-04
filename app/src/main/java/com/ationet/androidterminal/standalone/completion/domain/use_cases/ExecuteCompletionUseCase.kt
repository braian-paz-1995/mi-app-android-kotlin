package com.ationet.androidterminal.standalone.completion.domain.use_cases

import android.content.Context
import android.util.Log
import com.ationet.androidterminal.core.data.local.preauthorization.PreAuthorizationStandAloneRepository
import com.ationet.androidterminal.core.data.remote.ationet.model.DealerData
import com.ationet.androidterminal.core.data.remote.ationet.model.PromptKey
import com.ationet.androidterminal.core.data.remote.ationet.model.ResponseCodes
import com.ationet.androidterminal.core.domain.model.preauthorization.CustomerData
import com.ationet.androidterminal.core.domain.model.preauthorization.InputType
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptProductInputType
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionTypeName
import com.ationet.androidterminal.core.domain.repository.CompletionRepository
import com.ationet.androidterminal.core.domain.use_case.ationet.GetInvoice
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestCompletion
import com.ationet.androidterminal.core.domain.use_case.batch.GetLastOpenBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.core.domain.util.createCommunicationErrorReceipt
import com.ationet.androidterminal.standalone.completion.data.local.CompletionOperationStateRepository
import com.ationet.androidterminal.standalone.completion.domain.model.Completion
import com.ationet.androidterminal.standalone.completion.domain.model.ProductData
import com.ationet.androidterminal.standalone.completion.domain.model.TransactionData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExecuteCompletionUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getConfiguration: GetConfiguration,
    private val preAuthorizationRepository: PreAuthorizationStandAloneRepository,
    private val completionRepository: CompletionRepository,
    private val createCompletionReceipt: CreateCompletionReceiptUseCase,
    private val requestCompletion: RequestCompletion,
    private val getInvoice: GetInvoice,
    private val operationRepository: CompletionOperationStateRepository,
    private val getLastOpenBatchUseCase: GetLastOpenBatchUseCase,
) {
    suspend operator fun invoke(
        preAuthorizationId: Int,
        productName: String,
        productCode: String,
        unitPrice: Double,
        completionAmount: Double,
        completionQuantity: Double,
    ): ClearPendingResult {
        val configuration = getConfiguration.invoke()
        val transactionInstant = Clock.System.now()
        val batchId = getLastOpenBatchUseCase.invoke()?.id ?: 0
        // Try get pre authorization
        val preAuthorization = try {
            preAuthorizationRepository.getPreAuthorization(preAuthorizationId)
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            Log.e(TAG, "Standalone completion: pre-authorization $preAuthorizationId lookup failed", e)

            val receipt = createCommunicationErrorReceipt(
                transactionName = ReceiptTransactionTypeName.Completion,
                context = context,
                configuration = configuration,
                currentInstant = transactionInstant,
                primaryTrack = "",
                secondaryTrack = null,
                inputType = ReceiptProductInputType.FillUp,
                productName = productName,
                productCode = productCode,
                productUnitPrice = unitPrice,
                quantity = completionQuantity,
                amount = completionAmount,
                batchId = batchId
            )

            operationRepository.updateState { copy(receipt = receipt) }

            return ClearPendingResult.CommunicationError
        }

        if (preAuthorization == null) {
            Log.w(TAG, "Standalone completion: pre-authorization $preAuthorizationId not found")

            val receipt = createCommunicationErrorReceipt(
                transactionName = ReceiptTransactionTypeName.Completion,
                context = context,
                configuration = configuration,
                currentInstant = transactionInstant,
                primaryTrack = "",
                secondaryTrack = null,
                inputType = ReceiptProductInputType.FillUp,
                productName = productName,
                productCode = productCode,
                productUnitPrice = unitPrice,
                quantity = completionQuantity,
                amount = completionAmount,
                batchId = batchId
            )

            operationRepository.updateState { copy(receipt = receipt) }

            return ClearPendingResult.CommunicationError
        }

        val invoice = getInvoice.invoke()

        val result = try {
            requestCompletion.invoke(
                authorizationCode = preAuthorization.preAuthorization.authorization.authorizationCode,
                primaryTrack = preAuthorization.preAuthorization.identification.primaryTrack,
                invoice = invoice,
                fuelPosition = 0,
                productCode = preAuthorization.productCode,
                productAmount = completionAmount,
                productQuantity = completionQuantity,
                unitPrice = unitPrice,
                transactionAmount = completionAmount,
                primaryPin = preAuthorization.preAuthorization.customerData.primaryPin,
                secondaryTrack = preAuthorization.preAuthorization.identification.secondaryTrack,
                secondaryPin = preAuthorization.preAuthorization.customerData.secondaryPin,
                customerData = preAuthorization.preAuthorization.customerData.toMap(),
                originalData = null,
                transactionDateTime = transactionInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
                dealerData = preAuthorization.preAuthorization.customerData.attendantId?.let {
                    DealerData(
                        attendantCode = it
                    )
                }
            )
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()
            Log.e(TAG, "Standalone completion: pre-authorization #$preAuthorizationId error requesting completion", e)

            val receipt = createCommunicationErrorReceipt(
                transactionName = ReceiptTransactionTypeName.Completion,
                context = context,
                configuration = configuration,
                currentInstant = transactionInstant,
                primaryTrack = preAuthorization.preAuthorization.identification.primaryTrack,
                secondaryTrack = null,
                inputType = when (preAuthorization.inputType) {
                    InputType.Quantity -> ReceiptProductInputType.Quantity
                    InputType.Amount -> ReceiptProductInputType.Amount
                    InputType.FillUp -> ReceiptProductInputType.FillUp
                },
                productName = productName,
                productCode = productCode,
                productUnitPrice = unitPrice,
                quantity = completionQuantity,
                amount = completionAmount,
                batchId = batchId
            )

            operationRepository.updateState { copy(receipt = receipt) }

            return ClearPendingResult.CommunicationError
        }

        if (result.isFailure) {
            val exception = result.exceptionOrNull()
            if (exception == null) {
                Log.w(TAG, "Standalone completion: pre-authorization #$preAuthorizationId communication with controller failed")
            } else {
                Log.e(TAG, "Standalone completion: pre-authorization #$preAuthorizationId communication with controller failed with exception", exception)
            }

            val receipt = createCommunicationErrorReceipt(
                transactionName = ReceiptTransactionTypeName.Completion,
                context = context,
                configuration = configuration,
                currentInstant = transactionInstant,
                primaryTrack = preAuthorization.preAuthorization.identification.primaryTrack,
                secondaryTrack = null,
                inputType = when (preAuthorization.inputType) {
                    InputType.Quantity -> ReceiptProductInputType.Quantity
                    InputType.Amount -> ReceiptProductInputType.Amount
                    InputType.FillUp -> ReceiptProductInputType.FillUp
                },
                productName = productName,
                productCode = productCode,
                productUnitPrice = unitPrice,
                quantity = completionQuantity,
                amount = completionAmount,
                batchId = batchId
            )

            operationRepository.updateState { copy(receipt = receipt) }

            return ClearPendingResult.CommunicationError
        }

        val nativeResult = result.getOrNull()
        if (nativeResult == null) {
            Log.w(TAG, "Standalone completion: pre-authorization #$preAuthorizationId communication with controller failed")

            val receipt = createCommunicationErrorReceipt(
                transactionName = ReceiptTransactionTypeName.Completion,
                context = context,
                configuration = configuration,
                currentInstant = transactionInstant,
                primaryTrack = preAuthorization.preAuthorization.identification.primaryTrack,
                secondaryTrack = null,
                inputType = when (preAuthorization.inputType) {
                    InputType.Quantity -> ReceiptProductInputType.Quantity
                    InputType.Amount -> ReceiptProductInputType.Amount
                    InputType.FillUp -> ReceiptProductInputType.FillUp
                },
                productName = productName,
                productCode = productCode,
                productUnitPrice = unitPrice,
                quantity = completionQuantity,
                amount = completionAmount,
                batchId = batchId
            )

            operationRepository.updateState { copy(receipt = receipt) }

            return ClearPendingResult.CommunicationError
        }

        val completionSucceeded = nativeResult.responseCode == ResponseCodes.Authorized
        // Create completion
        val productUnitPrice = nativeResult.companyPrice?.productUnitPrice ?: nativeResult.productUnitPrice?.toDoubleOrNull() ?: unitPrice

        val productAmountCalculated = nativeResult.companyPrice?.productAmount ?: nativeResult.productAmount?.toDoubleOrNull()
        ?: if (preAuthorization.inputType == InputType.Amount) completionAmount else productUnitPrice * completionQuantity

        val productQuantityCalculated = (nativeResult.companyPrice?.productAmount ?: nativeResult.productAmount?.toDoubleOrNull()
        ?: if (preAuthorization.inputType == InputType.Amount) completionAmount else productAmountCalculated) / productUnitPrice

        if (completionSucceeded) {
            val completion = Completion(
                authorizationCode = nativeResult.authorizationCode.orEmpty(),
                transactionDateTime = transactionInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
                transactionSequenceNumber = nativeResult.transactionSequenceNumber?.toLongOrNull()
                    ?: 0L,
                transactionData = TransactionData(
                    primaryTrack = preAuthorization.preAuthorization.identification.primaryTrack,
                    product = ProductData(
                        inputType = preAuthorization.inputType.name,
                        name = productName,
                        code = productCode,
                        unitPrice = unitPrice,
                        quantity = completionQuantity,
                        amount = completionAmount
                    )
                ),
                batchId = batchId,
                controllerType = configuration.controllerType
            )

            val createdCompletion = try {
                completionRepository.createCompletion(completion)
            } catch (e: Throwable) {
                currentCoroutineContext().ensureActive()
                Log.e(TAG, "Standalone completion: pre-authorization #$preAuthorizationId failed to save completion to database", e)
                return ClearPendingResult.CommunicationError
            }
            Log.i(TAG, "Standalone completion: pre-authorization #$preAuthorizationId created completion #${createdCompletion.id}")
        } else {
            Log.w(TAG, "Completion failed: '${nativeResult.longResponseText}'(${nativeResult.responseCode})")
        }

        /* We always delete the transaction in case it is cancelled from the ationet portal. */
        try {
            preAuthorizationRepository.deletePreAuthorization(preAuthorizationId)
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()
            Log.e(TAG, "Standalone completion: pre-authorization #$preAuthorizationId delete failed", e)
            return ClearPendingResult.CommunicationError
        }
        val productUnitPriceTicket = if (configuration.ticket.transactionDetails) nativeResult.companyPrice?.productUnitPriceBase ?: unitPrice else unitPrice
        val productAmountCalculatedTicket = productUnitPriceTicket * completionQuantity
        val productQuantityCalculatedTicket = completionAmount / productUnitPriceTicket

        // Create receipt
        val receipt = createCompletionReceipt.invoke(
            requestDate = transactionInstant,
            primaryTrack = preAuthorization.preAuthorization.identification.primaryTrack,
            secondaryTrack = preAuthorization.preAuthorization.identification.secondaryTrack,
            productCode = productCode,
            productName = productName,
            productUnitPrice = productUnitPriceTicket,
            inputType = preAuthorization.inputType,
            response = nativeResult,
            quantity = completionQuantity,
            amount = productAmountCalculatedTicket,
            batchId = batchId,
            pumpId = nativeResult.pumpNumber ?: ""
        )

        operationRepository.updateState { copy(receipt = receipt) }

        return if (completionSucceeded) {
            ClearPendingResult.Success(
                authorizationCode = nativeResult.authorizationCode.orEmpty(),
                product = productName,
                quantity = productQuantityCalculated,
                amount = productAmountCalculated,
                date = transactionInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
                receiptId = receipt.id
            )
        } else {
            ClearPendingResult.Error(
                errorCode = nativeResult.responseCode,
                message = nativeResult.longResponseText ?: nativeResult.responseMessage
                ?: nativeResult.responseText.orEmpty()
            )
        }
    }

    private fun CustomerData.toMap(): Map<String, String> {
        val customerDataMap = mutableMapOf<String, String>()
        if (odometer != null) customerDataMap[PromptKey.Odometer] = odometer
        if (engineHours != null) customerDataMap[PromptKey.EngineHours] = engineHours
        if (driverId != null) customerDataMap[PromptKey.DriverId] = driverId
        if (vehicleId != null) customerDataMap[PromptKey.VehicleId] = vehicleId
        if (miscellaneous != null) customerDataMap[PromptKey.Miscellaneous] = miscellaneous
        if (trailerNumber != null) customerDataMap[PromptKey.TrailerNumber] = trailerNumber
        if (truckUnitNumber != null) customerDataMap[PromptKey.TruckUnitNumber] = truckUnitNumber
        if (attendantId != null) customerDataMap[PromptKey.AttendantId] = attendantId
        return customerDataMap
    }

    private companion object {
        private const val TAG: String = "ExecuteCompletion"
    }
}

sealed interface ClearPendingResult {
    data object CommunicationError : ClearPendingResult

    data class Error(
        val errorCode: String?,
        val message: String,
    ) : ClearPendingResult

    data class Success(
        val date: LocalDateTime,
        val product: String,
        val authorizationCode: String,
        val quantity: Double,
        val amount: Double,
        val receiptId: Int
    ) : ClearPendingResult
}