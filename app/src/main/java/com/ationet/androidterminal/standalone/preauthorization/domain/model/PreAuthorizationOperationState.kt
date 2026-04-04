package com.ationet.androidterminal.standalone.preauthorization.domain.model

import com.ationet.androidterminal.core.domain.model.preauthorization.InputType
import com.ationet.androidterminal.core.domain.model.receipt.Receipt
import kotlinx.datetime.LocalDateTime

data class PreAuthorizationOperationState(
    val identifier: Identifier = Identifier(),
    val product: Product = ProductStandAlone(),
    val error: Error = Error(),
    val quantity: Quantity = Quantity(),
    val receiptData: ReceiptData = ReceiptDataStandAlone(),
    val prompts: List<Prompt> = emptyList(),
    val authorizationData: AuthorizationData = AuthorizationData(),
    val originalData: OriginalData = OriginalData(),
    val receipt: Receipt? = null,
)

data class Identifier(
    val primaryTrack: String = "",
)

interface Product

data class ProductStandAlone(
    val id: Int = 0,
    val code: String = "",
    val name: String = "",
    val unitPrice: Double = 0.0,
    val isFuel: Boolean = false,
    val isSku: Boolean = false,
) : Product

data class Quantity(
    val inputType: InputType = InputType.Quantity,
    val value: Double = 0.0,
)

interface ReceiptData
data class ReceiptDataStandAlone(
    val transactionSequenceNumber: Long = 0,
    val invoice: String = "",
) : ReceiptData

data class Prompt(
    val key: String = "",
    val value: String? = null,
    val state: PromptState = PromptState.Pending,
    val type: PromptType = PromptType.Identifier,
) {
    enum class PromptState {
        Pending,
        Completed,
    }

    enum class PromptType {
        Identifier,
        Pin,
        Alphanumeric,
        Numeric,
        VisionRecognition,
        Attendant,
    }
}

data class AuthorizationData(
    val authorizationCode: String = "",
    val transactionSequenceNumber: Long = 0,
    val invoice: String = "",
    val localDateTime: LocalDateTime? = null,
    val amount: Double? = null,
    val quantity: Double? = null,
    val unitPrice: Double? = null,
)

data class OriginalData(
    val transactionSequenceNumber: Long = 0,
    val authorizationCode: String = "",
    val transactionCode: String = "",
    val localTransactionDate: String? = null,
    val localTransactionTime: String? = null,
)

data class Error(
    val code: String = "",
    val message: String = "",
)


