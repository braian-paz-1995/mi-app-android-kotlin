package com.ationet.androidterminal.standalone.preauthorization.presentation.pending_transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ationet.androidterminal.standalone.completion.domain.use_cases.GetPreAuthorizationByIdentifier
import com.ationet.androidterminal.standalone.completion.domain.use_cases.PreAuthorizationByIdentifierResult
import com.ationet.androidterminal.standalone.preauthorization.data.local.PreAuthorizationOperationStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PendingTransactionState {
    data object ProductSelection : PendingTransactionState
    data class PendingTransaction(val identification: String) : PendingTransactionState
}

@HiltViewModel
class PendingTransactionViewModel @Inject constructor(
    operationStateRepository: PreAuthorizationOperationStateRepository,
    getPreAuthorizationByIdentifier: GetPreAuthorizationByIdentifier
) : ViewModel() {
    private val _state = MutableStateFlow<PendingTransactionState>(PendingTransactionState.ProductSelection)
    val state = _state.asStateFlow()

    init {
        val identifier = operationStateRepository.getState().identifier.primaryTrack
        viewModelScope.launch {
            when (getPreAuthorizationByIdentifier(identifier)) {
                is PreAuthorizationByIdentifierResult.Error -> {}
                PreAuthorizationByIdentifierResult.NotFound -> _state.update { PendingTransactionState.ProductSelection }
                is PreAuthorizationByIdentifierResult.PreAuthorizationFound -> _state.update { PendingTransactionState.PendingTransaction(identifier) }
            }
        }
    }
}