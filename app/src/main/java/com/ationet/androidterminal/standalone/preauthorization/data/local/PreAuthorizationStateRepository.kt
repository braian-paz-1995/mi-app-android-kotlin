package com.ationet.androidterminal.standalone.preauthorization.data.local

import com.ationet.androidterminal.core.domain.repository.OperationStateRepository
import com.ationet.androidterminal.standalone.preauthorization.domain.model.PreAuthorizationOperationState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreAuthorizationOperationStateRepository @Inject constructor() : OperationStateRepository<PreAuthorizationOperationState> {
    private var state = PreAuthorizationOperationState()
    override fun clear() {
        state = PreAuthorizationOperationState()
    }

    override fun getState(): PreAuthorizationOperationState {
        return state
    }

    override fun updateState(block: (PreAuthorizationOperationState) -> PreAuthorizationOperationState) {
        state = block(state)
    }
}