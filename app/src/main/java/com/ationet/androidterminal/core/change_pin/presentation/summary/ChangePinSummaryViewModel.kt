package com.ationet.androidterminal.core.change_pin.presentation.summary

import androidx.lifecycle.ViewModel
import com.ationet.androidterminal.core.change_pin.data.local.ChangePinOperationStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ChangePinSummaryViewModel @Inject constructor(
    operationStateRepository: ChangePinOperationStateRepository
) : ViewModel() {
    private val _state: MutableStateFlow<SummaryState>
    val state get() = _state.asStateFlow()

    init {
        val operationState = operationStateRepository.getState()

        val summaryScreenState = SummaryState.Summary(
            SummaryData(
                dateTime = operationState.dateTime,
                authorizationCode = operationState.authorizationCode,
                codeError = operationState.code,
                message = operationState.message,
                responseCode = operationState.receipt?.transactionData?.responseCode,
                responseText = operationState.receipt?.transactionData?.responseText,
            )
        )

        _state = MutableStateFlow(summaryScreenState)
    }
}

data class SummaryData(
    val dateTime: LocalDateTime,
    val authorizationCode: String,
    val codeError: String?,
    val message: String?,
    val responseCode: String?,
    val responseText: String?,
)

sealed interface SummaryState {
    data class Summary(val data: SummaryData) : SummaryState
}