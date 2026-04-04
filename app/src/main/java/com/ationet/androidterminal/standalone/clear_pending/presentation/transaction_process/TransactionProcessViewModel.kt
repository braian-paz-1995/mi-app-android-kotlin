package com.ationet.androidterminal.standalone.clear_pending.presentation.transaction_process

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ationet.androidterminal.core.domain.model.LoadingState
import com.ationet.androidterminal.standalone.clear_pending.data.local.ClearPendingOperationStateRepository
import com.ationet.androidterminal.standalone.clear_pending.domain.use_case.ClearPendingResult
import com.ationet.androidterminal.standalone.clear_pending.domain.use_case.ExecuteClearPendingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

sealed interface TransactionProcessState {
    data class LoadingTransaction(val loadingState: LoadingState) : TransactionProcessState
    data class TransactionProcessOk(val authorizationCode: String) : TransactionProcessState
    data class TransactionProcessError(val code: String, val message: String) : TransactionProcessState
    data object CommunicationError : TransactionProcessState
}

@HiltViewModel
class TransactionProcessViewModel @Inject constructor(
    private val executeClearPendingUseCase: ExecuteClearPendingUseCase,
    operationStateRepository: ClearPendingOperationStateRepository
) : ViewModel() {
    private val _state = MutableStateFlow<TransactionProcessState>(TransactionProcessState.LoadingTransaction(LoadingState.Loading))
    val state = _state.asStateFlow()
    private val operationState = operationStateRepository.getState()

    init {
        viewModelScope.launch {
            sendSale()
        }
    }

    private suspend fun sendSale() {
        if (operationState.preAuthorizationData == null) {
            _state.update { TransactionProcessState.TransactionProcessError("Error", "Pre-authorization data missing") }
            return
        }

        when (val result = executeClearPendingUseCase.invoke(
            preAuthorizationId = operationState.preAuthorizationData.id,
            productName = operationState.preAuthorizationData.product.name,
            productCode = operationState.preAuthorizationData.product.code,
            unitPrice = operationState.preAuthorizationData.product.unitPrice,
            completionAmount = 0.0,
            completionQuantity = 0.0
        )) {
            ClearPendingResult.CommunicationError -> {
                _state.update { TransactionProcessState.LoadingTransaction(LoadingState.Failure) }
                delay(2.seconds)
                _state.update { TransactionProcessState.CommunicationError }
            }

            is ClearPendingResult.Error -> {
                _state.update { TransactionProcessState.LoadingTransaction(LoadingState.Failure) }
                delay(2.seconds)
                _state.update { TransactionProcessState.TransactionProcessError(result.errorCode.orEmpty(), result.message) }
            }

            is ClearPendingResult.Success -> {
                _state.update { TransactionProcessState.LoadingTransaction(LoadingState.Success) }
                delay(1.seconds)
                _state.update { TransactionProcessState.TransactionProcessOk(result.authorizationCode) }
            }
        }
    }
}