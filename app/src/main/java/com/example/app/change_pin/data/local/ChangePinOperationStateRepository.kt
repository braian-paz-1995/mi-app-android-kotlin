package com.ationet.androidterminal.core.change_pin.data.local

import com.ationet.androidterminal.core.domain.model.receipt.Receipt
import com.ationet.androidterminal.core.domain.repository.OperationStateRepository
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

data class ChangePinOperationState(
    val track: String? = null,
    val authorizationCode: String = "",
    val dateTime: LocalDateTime = LocalDateTime(0, 1, 1, 0, 0, 0),
    val message: String? = "",
    val code: String? = "",
    val receipt: Receipt? = null
)

@Singleton
class ChangePinOperationStateRepository @Inject constructor() :
    OperationStateRepository<ChangePinOperationState> {
    private var state: ChangePinOperationState = ChangePinOperationState()

    override fun clear() {
        updateState {
            ChangePinOperationState()
        }
    }

    override fun getState(): ChangePinOperationState = state

    override fun updateState(block: (ChangePinOperationState) -> ChangePinOperationState) {
        state = block.invoke(state)
    }
}