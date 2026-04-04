package com.ationet.androidterminal.standalone.batch_close.presentation.confirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ationet.androidterminal.core.domain.model.LoadingState
import com.ationet.androidterminal.standalone.batch_close.domain.use_case.BatchCloseResult
import com.ationet.androidterminal.standalone.batch_close.domain.use_case.ExecuteBatchCloseUseCase
import com.ationet.androidterminal.standalone.batch_close.domain.use_case.GetAllPreAuthorizationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

sealed interface ConfirmationState {
    data class Confirmation(val hasPreAuthorizations: Boolean) : ConfirmationState
    data class LoadingTransaction(val loadingState: LoadingState) : ConfirmationState
    data object TransactionProcessOk : ConfirmationState
    data class TransactionProcessError(val code: String, val message: String) : ConfirmationState
    data object CommunicationError : ConfirmationState
}

@HiltViewModel
class ConfirmationViewModel @Inject constructor(
    private val executeBatchCloseUseCase: ExecuteBatchCloseUseCase,
    getAllPreAuthorizationUseCase: GetAllPreAuthorizationUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<ConfirmationState>(ConfirmationState.Confirmation(false))
    private val hasPreAuthorizations = runBlocking { getAllPreAuthorizationUseCase().isNotEmpty() }
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { ConfirmationState.Confirmation(hasPreAuthorizations) }
        }
    }

    fun onConfirm() {
        viewModelScope.launch {
            _state.update { ConfirmationState.LoadingTransaction(LoadingState.Loading) }
            val result = withContext(Dispatchers.IO) {
                executeBatchCloseUseCase(hasPreAuthorizations)
            }
            when (result) {
                is BatchCloseResult.Success -> {
                    _state.update { ConfirmationState.LoadingTransaction(LoadingState.Success) }
                    delay(2.seconds)
                    _state.update { ConfirmationState.TransactionProcessOk }
                }

                is BatchCloseResult.Failure -> {
                    _state.update { ConfirmationState.LoadingTransaction(LoadingState.Failure) }
                    delay(2.seconds)
                    _state.update { ConfirmationState.TransactionProcessError(result.code, result.message) }
                }

                BatchCloseResult.CommunicationError -> {
                    _state.update { ConfirmationState.LoadingTransaction(LoadingState.Failure) }
                    delay(2.seconds)
                    _state.update { ConfirmationState.CommunicationError }
                }
            }
        }


    }
}
