package com.ationet.androidterminal.standalone.sale.domain.model

import com.ationet.androidterminal.core.domain.model.receipt.Receipt
import com.ationet.androidterminal.standalone.preauthorization.domain.model.Identifier
import com.ationet.androidterminal.standalone.preauthorization.domain.model.Prompt
enum class OperationType {
    Fuel,
    Sku
}
data class SaleOperationState(
    val identifier: Identifier = Identifier(),
    val product: Product = ProductStandAlone(),
    val quantity: Quantity = Quantity(),
    val prompts: List<Prompt> = emptyList(),
    val receiptId: Int = -1,
    val receipt: Receipt? = null,
    val authorizationCode: String = "",
    val operationType: OperationType? = null,
)

data class Identifier(
    val primaryTrack: String = "",
)

data class Quantity(
    val inputType: InputType = InputType.Quantity,
    val value: Double = 0.0,
) {
    enum class InputType {
        Quantity,
        Amount,
    }
}

interface Product

data class ProductStandAlone(
    val id: Int = 0,
    val code: String = "",
    val name: String = "",
    val unitPrice: Double = 0.0,
    val isFuel: Boolean = false,
    val isSku: Boolean = false,
) : Product