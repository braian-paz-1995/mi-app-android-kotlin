package com.ationet.androidterminal.standalone.clear_pending.data.local

import com.ationet.androidterminal.core.domain.repository.OperationStateRepository
import com.ationet.androidterminal.standalone.clear_pending.domain.model.ClearPendingOperationState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClearPendingOperationStateRepository @Inject constructor() : OperationStateRepository<ClearPendingOperationState> {
    private var state = ClearPendingOperationState()
    override fun clear() {
        state = ClearPendingOperationState()
    }

    override fun getState(): ClearPendingOperationState {
        return state
    }

    override fun updateState(block: (ClearPendingOperationState) -> ClearPendingOperationState) {
        state = block(state)
    }
}