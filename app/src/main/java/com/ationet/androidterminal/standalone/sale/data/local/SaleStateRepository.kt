package com.ationet.androidterminal.standalone.sale.data.local

import com.ationet.androidterminal.core.domain.repository.OperationStateRepository
import com.ationet.androidterminal.standalone.sale.domain.model.OperationType
import com.ationet.androidterminal.standalone.sale.domain.model.SaleOperationState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaleOperationStateRepository @Inject constructor() : OperationStateRepository<SaleOperationState> {
    private var state = SaleOperationState()
    override fun clear() {
        state = SaleOperationState()
    }

    override fun getState(): SaleOperationState {
        return state
    }

    override fun updateState(block: (SaleOperationState) -> SaleOperationState) {
        state = block(state)
    }
    fun setOperationType(type: OperationType) {
        updateState { it.copy(operationType = type) }
    }
}