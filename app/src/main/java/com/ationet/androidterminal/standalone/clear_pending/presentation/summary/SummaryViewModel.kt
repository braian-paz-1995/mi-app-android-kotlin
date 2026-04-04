package com.ationet.androidterminal.standalone.clear_pending.presentation.summary

import androidx.lifecycle.ViewModel
import com.ationet.androidterminal.standalone.clear_pending.data.local.ClearPendingOperationStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

data class SummaryData(
    val date: LocalDateTime,
    val authorizationCode: String,
)

sealed interface SummaryState {
    data class Summary(val data: SummaryData) : SummaryState
}

@HiltViewModel
class SummaryViewModel @Inject constructor(
    operationStateRepository: ClearPendingOperationStateRepository,
) : ViewModel() {
    private val operationState = operationStateRepository.getState()

    private val _state = MutableStateFlow<SummaryState>(
        SummaryState.Summary(
            SummaryData(
                date = operationState.preAuthorizationData?.date ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                authorizationCode = operationState.preAuthorizationData?.authorizationCode.orEmpty()
            )
        )
    )
    val state = _state.asStateFlow()
}