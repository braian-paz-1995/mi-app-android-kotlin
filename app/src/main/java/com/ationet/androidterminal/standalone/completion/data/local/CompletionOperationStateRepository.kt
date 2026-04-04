package com.ationet.androidterminal.standalone.completion.data.local

import com.ationet.androidterminal.core.domain.repository.OperationStateRepository
import com.ationet.androidterminal.standalone.completion.domain.model.CompletionOperationState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompletionOperationStateRepository @Inject constructor(

) : OperationStateRepository<CompletionOperationState> {
    private var state: CompletionOperationState = CompletionOperationState(
        receipt = null
    )

    override fun clear() {
        updateState {
            CompletionOperationState(
                receipt = null
            )
        }
    }

    override fun getState(): CompletionOperationState = state

    override fun updateState(block: CompletionOperationState.() -> CompletionOperationState) {
        state = block.invoke(state)
    }
}