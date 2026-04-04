package com.ationet.androidterminal.standalone.balance_enquiry.presentation.transaction_process

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ationet.androidterminal.core.domain.model.LoadingState
import com.ationet.androidterminal.standalone.balance_enquiry.domain.use_case.SendBalanceEnquiry
import com.ationet.androidterminal.standalone.balance_enquiry.domain.use_case.TransactionResult
import com.ationet.androidterminal.standalone.balance_enquiry.presentation.summary.SummaryData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

sealed interface TransactionProcessState {
    data class LoadingTransaction(val loadingState: LoadingState, val summaryData: SummaryData? = null) : TransactionProcessState
    data class TransactionProcessError(val code: String, val message: String) : TransactionProcessState
    data object CommunicationError : TransactionProcessState
}

@HiltViewModel
class TransactionProcessViewModel @Inject constructor(
    private val sendBalanceEnquiryUseCase: SendBalanceEnquiry,
) : ViewModel() {
    private val _state = MutableStateFlow<TransactionProcessState>(TransactionProcessState.LoadingTransaction(LoadingState.Loading))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            sendBalanceEnquiry()
        }
    }

    private suspend fun sendBalanceEnquiry() {
        when (val result = sendBalanceEnquiryUseCase()) {
            TransactionResult.CommunicationError -> {
                _state.update { TransactionProcessState.LoadingTransaction(LoadingState.Failure) }

                delay(2.seconds)

                _state.update { TransactionProcessState.CommunicationError }
            }

            is TransactionResult.Error -> {
                _state.update { TransactionProcessState.LoadingTransaction(LoadingState.Failure) }

                delay(2.seconds)

                _state.update {
                    TransactionProcessState.TransactionProcessError(result.code, result.message)
                }
            }

            is TransactionResult.Success -> {
                _state.update { TransactionProcessState.LoadingTransaction(LoadingState.Success, result.summaryData) }
            }
        }
    }
}