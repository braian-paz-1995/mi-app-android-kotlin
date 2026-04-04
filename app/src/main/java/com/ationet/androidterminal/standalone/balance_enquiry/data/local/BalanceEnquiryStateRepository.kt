package com.ationet.androidterminal.standalone.balance_enquiry.data.local

import com.ationet.androidterminal.core.domain.model.receipt.Receipt
import com.ationet.androidterminal.core.domain.repository.OperationStateRepository
import com.ationet.androidterminal.standalone.sale.domain.model.Product
import com.ationet.androidterminal.standalone.sale.domain.model.ProductStandAlone
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

data class BalanceEnquiryOperationState(
    val primaryTrack: String = "",
    val product: Product = ProductStandAlone(),
    val authorizationCode: String = "",
    val receipt: Receipt? = null,
    val quantity: Quantity = Quantity(),
)

@Serializable
data class Quantity(
    val inputType: InputType = InputType.Quantity,
    val value: Double = 0.0,
) {
    enum class InputType {
        Quantity,
        Amount,
    }
}

@Singleton
class BalanceEnquiryStateRepository @Inject constructor() : OperationStateRepository<BalanceEnquiryOperationState> {
    private var state = BalanceEnquiryOperationState()
    override fun clear() {
        state = BalanceEnquiryOperationState()
    }

    override fun getState(): BalanceEnquiryOperationState {
        return state
    }

    override fun updateState(block: (BalanceEnquiryOperationState) -> BalanceEnquiryOperationState) {
        state = block(state)
    }

}