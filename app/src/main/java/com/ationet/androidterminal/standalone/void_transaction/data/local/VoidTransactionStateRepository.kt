package com.ationet.androidterminal.standalone.void_transaction.data.local

import com.ationet.androidterminal.core.domain.model.receipt.Receipt
import com.ationet.androidterminal.core.domain.repository.OperationStateRepository
import javax.inject.Inject
import javax.inject.Singleton

data class VoidTransactionOperationState(
    val receipt: Receipt? = null
)

@Singleton
class VoidTransactionStateRepository @Inject constructor() : OperationStateRepository<VoidTransactionOperationState> {
    private var state = VoidTransactionOperationState()
    override fun clear() {
        state = VoidTransactionOperationState()
    }

    override fun getState(): VoidTransactionOperationState {
        return state
    }

    override fun updateState(block: (VoidTransactionOperationState) -> VoidTransactionOperationState) {
        state = block(state)
    }
}