package com.ationet.androidterminal.standalone.completion.domain.use_cases

import android.util.Log
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.domain.model.preauthorization.InputType
import com.ationet.androidterminal.core.domain.model.receipt.Receipt
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptData
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptModifierClass
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptModifierType
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptProductInputType
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionTypeName
import com.ationet.androidterminal.core.domain.model.receipt.TransactionModifier
import com.ationet.androidterminal.core.domain.repository.ReceiptRepository
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.core.domain.util.createReceipt
import com.ationet.androidterminal.core.domain.util.displayResponseText
import com.ationet.androidterminal.core.util.receiptData
import kotlinx.datetime.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateCompletionReceiptUseCase @Inject constructor(
    private val receiptRepository: ReceiptRepository,
    private val getConfiguration: GetConfiguration,
) {
    suspend operator fun invoke(
        requestDate: Instant,
        primaryTrack: String,
        productName: String,
        productCode: String,
        productUnitPrice: Double,
        inputType: InputType,
        quantity: Double,
        amount: Double,
        response: NativeRequest,
        secondaryTrack: String?,
        batchId: Int,
        pumpId: String
    ): Receipt {
        val configuration = getConfiguration.invoke()

        val receipt = createReceipt(
            transactionName = ReceiptTransactionTypeName.Completion,
            authorizationCode = response.authorizationCode,
            invoiceNumber = response.invoiceNumber,
            transactionSequenceNumber = response.transactionSequenceNumber,
            responseText = response.displayResponseText,
            responseCode = response.responseCode,
            configuration = configuration,
            currentInstant = requestDate,
            primaryTrack = primaryTrack,
            secondaryTrack = secondaryTrack,
            inputType = when (inputType) {
                InputType.Quantity -> ReceiptProductInputType.Quantity
                InputType.Amount -> ReceiptProductInputType.Amount
                InputType.FillUp -> ReceiptProductInputType.FillUp
            },
            productName = productName,
            productCode = productCode,
            productUnitPrice = productUnitPrice,
            quantity = quantity,
            amount = amount,
            receiptData = try {
                receiptData(response.receiptData)
            } catch (e: Throwable) {
                Log.w(TAG, "Completion receipt: Failed to decode receipt data", e)
                ReceiptData()
            },
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
            batchId = batchId,
            transactionAmount = response.companyPrice?.transactionAmount,
            pumpId = pumpId
        )

        val receiptId = receiptRepository.saveReceipt(receipt)

        Log.i(TAG, "Completion receipt: Created receipt #$receiptId")
        return receipt.copy(id = receiptId)
    }

    private companion object {
        private const val TAG: String = "CompletionReceipt"
    }
}