package com.ationet.androidterminal.standalone.clear_pending.domain.use_case

import android.content.Context
import android.util.Log
import com.ationet.androidterminal.core.data.local.preauthorization.PreAuthorizationStandAloneRepository
import com.ationet.androidterminal.core.data.remote.ationet.model.ResponseCodes
import com.ationet.androidterminal.core.domain.model.preauthorization.InputType
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptProductInputType
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionTypeName
import com.ationet.androidterminal.core.domain.repository.CompletionRepository
import com.ationet.androidterminal.core.domain.use_case.ationet.GetInvoice
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestCompletion
import com.ationet.androidterminal.core.domain.use_case.batch.GetLastOpenBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.core.domain.util.createCommunicationErrorReceipt
import com.ationet.androidterminal.standalone.clear_pending.data.local.ClearPendingOperationStateRepository
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
class ExecuteClearPendingUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getConfiguration: GetConfiguration,
    private val preAuthorizationRepository: PreAuthorizationStandAloneRepository,
    private val completionRepository: CompletionRepository,
    private val createClearPendingReceipt: CreateClearPendingReceiptUseCase,
    private val requestCompletion: RequestCompletion,
    private val getInvoice: GetInvoice,
    private val operationRepository: ClearPendingOperationStateRepository,
    private val getLastOpenBatchUseCase: GetLastOpenBatchUseCase
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

            Log.e(TAG, "Standalone clear pending: pre-authorization $preAuthorizationId lookup failed", e)

            val receipt = createCommunicationErrorReceipt(
                transactionName = ReceiptTransactionTypeName.ClearPending,
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

            operationRepository.updateState { it.copy(receipt = receipt) }

            return ClearPendingResult.CommunicationError
        }

        if (preAuthorization == null) {
            Log.w(TAG, "Standalone clear pending: pre-authorization $preAuthorizationId not found")

            val receipt = createCommunicationErrorReceipt(
                transactionName = ReceiptTransactionTypeName.ClearPending,
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

            operationRepository.updateState { it.copy(receipt = receipt) }

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
                primaryPin = null,
                secondaryTrack = null,
                secondaryPin = null,
                customerData = null,
                originalData = null,
                transactionDateTime = transactionInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            )
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()
            Log.e(TAG, "Standalone clear pending: pre-authorization #$preAuthorizationId error requesting completion", e)

            val receipt = createCommunicationErrorReceipt(
                transactionName = ReceiptTransactionTypeName.ClearPending,
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

            operationRepository.updateState { it.copy(receipt = receipt) }

            return ClearPendingResult.CommunicationError
        }

        if (result.isFailure) {
            val exception = result.exceptionOrNull()
            if (exception == null) {
                Log.w(TAG, "Standalone clear pending: pre-authorization #$preAuthorizationId communication with controller failed")
            } else {
                Log.e(TAG, "Standalone clear pending: pre-authorization #$preAuthorizationId communication with controller failed with exception", exception)
            }

            val receipt = createCommunicationErrorReceipt(
                transactionName = ReceiptTransactionTypeName.ClearPending,
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

            operationRepository.updateState { it.copy(receipt = receipt) }

            return ClearPendingResult.CommunicationError
        }

        val nativeResult = result.getOrNull()
        if (nativeResult == null) {
            Log.w(TAG, "Standalone clear pending: pre-authorization #$preAuthorizationId communication with controller failed")

            val receipt = createCommunicationErrorReceipt(
                transactionName = ReceiptTransactionTypeName.ClearPending,
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

            operationRepository.updateState { it.copy(receipt = receipt) }

            return ClearPendingResult.CommunicationError
        }

        val completionSucceeded = nativeResult.responseCode == ResponseCodes.Authorized
        if (completionSucceeded) {
            // Create completion
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
                        amount = nativeResult.companyPrice?.productAmount ?: nativeResult.productAmount?.toDoubleOrNull() ?: completionAmount
                    )
                ),
                batchId = batchId,
                controllerType = configuration.controllerType
            )

            val createdCompletion = try {
                completionRepository.createCompletion(completion)
            } catch (e: Throwable) {
                currentCoroutineContext().ensureActive()
                Log.e(TAG, "Standalone clear pending: pre-authorization #$preAuthorizationId failed to save completion to database", e)
                return ClearPendingResult.CommunicationError
            }
            Log.i(TAG, "Standalone clear pending: pre-authorization #$preAuthorizationId created completion #${createdCompletion.id}")
        } else {
            Log.w(TAG, "Clear Pending failed: '${nativeResult.longResponseText}'(${nativeResult.responseCode})")
        }

        /* We always delete the transaction in case it is cancelled from the ationet portal. */
        try {
            preAuthorizationRepository.deletePreAuthorization(preAuthorizationId)
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()
            Log.e(TAG, "Standalone clear pending: pre-authorization #$preAuthorizationId delete failed", e)
            return ClearPendingResult.CommunicationError
        }

        // Create receipt
        val receipt = createClearPendingReceipt.invoke(
            requestDate = transactionInstant,
            primaryTrack = preAuthorization.preAuthorization.identification.primaryTrack,
            secondaryTrack = preAuthorization.preAuthorization.identification.secondaryTrack,
            productCode = productCode,
            productName = productName,
            productUnitPrice = unitPrice,
            inputType = preAuthorization.inputType,
            response = nativeResult,
            quantity = completionQuantity,
            amount = completionAmount,
            batchId = batchId
        )

        operationRepository.updateState { it.copy(receipt = receipt) }

        return if (completionSucceeded) {
            ClearPendingResult.Success(
                authorizationCode = nativeResult.authorizationCode.orEmpty(),
                product = productName,
                quantity = completionQuantity,
                amount = completionAmount,
                date = transactionInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
                receiptId = receipt.id,
            )
        } else {
            ClearPendingResult.Error(
                errorCode = nativeResult.responseCode,
                message = nativeResult.longResponseText ?: nativeResult.responseMessage
                ?: nativeResult.responseText.orEmpty()
            )
        }
    }

    private companion object {
        private const val TAG: String = "ExecuteClearPending"
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
        val receiptId: Int,
    ) : ClearPendingResult
}