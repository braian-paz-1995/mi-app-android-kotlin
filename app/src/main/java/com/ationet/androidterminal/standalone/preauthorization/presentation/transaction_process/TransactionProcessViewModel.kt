package com.ationet.androidterminal.standalone.preauthorization.presentation.transaction_process

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ationet.androidterminal.core.domain.model.LoadingState
import com.ationet.androidterminal.core.domain.model.PromptTitle
import com.ationet.androidterminal.standalone.preauthorization.data.local.PreAuthorizationOperationStateRepository
import com.ationet.androidterminal.standalone.preauthorization.domain.model.Prompt
import com.ationet.androidterminal.standalone.preauthorization.domain.use_case.SendPreAuthorization
import com.ationet.androidterminal.standalone.preauthorization.domain.use_case.TransactionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

sealed interface TransactionProcessState {
    data class LoadingTransaction(val loadingState: LoadingState) : TransactionProcessState
    data class TransactionProcessOk(val authorizationCode: String) : TransactionProcessState
    data class TransactionProcessError(val code: String, val message: String) : TransactionProcessState
    data class RequiredPrompts(val prompt: Prompt, val promptTitle: PromptTitle) : TransactionProcessState
    data object LookingForPrompts : TransactionProcessState
    data object CommunicationError : TransactionProcessState
}

@HiltViewModel
class TransactionProcessViewModel @Inject constructor(
    private val sendPreAuthorizationUseCase: SendPreAuthorization,
    private val operationStateRepository: PreAuthorizationOperationStateRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<TransactionProcessState>(TransactionProcessState.LoadingTransaction(LoadingState.Loading))
    val state = _state.asStateFlow()

    private val pendingPrompts = mutableListOf<Prompt>()

    init {
        sendPreAuthorization()
    }

    private fun sendPreAuthorization() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (val result = sendPreAuthorizationUseCase()) {
                    TransactionResult.CommunicationError -> {
                        _state.update { TransactionProcessState.LoadingTransaction(LoadingState.Failure) }

                        delay(2.seconds)

                        _state.update { TransactionProcessState.CommunicationError }
                    }

                    is TransactionResult.Error -> {
                        _state.update {
                            TransactionProcessState.LoadingTransaction(LoadingState.Failure)
                        }

                        delay(2.seconds)

                        _state.update {
                            TransactionProcessState.TransactionProcessError(result.code, result.message)
                        }
                    }

                    is TransactionResult.RequiredPromptsAtionet -> {
                        pendingPrompts.addAll(result.prompts)
                        _state.update { TransactionProcessState.LookingForPrompts }
                        delay(2000)
                        val prompt = pendingPrompts.first()
                        _state.update { TransactionProcessState.RequiredPrompts(prompt, PromptTitle.entries.first { it.key == prompt.key }) }
                    }

                    is TransactionResult.RequiredPromptsFixed -> {
                        pendingPrompts.addAll(result.prompts)
                        val prompt = pendingPrompts.first()
                        _state.update { TransactionProcessState.RequiredPrompts(prompt, PromptTitle.entries.first { it.key == prompt.key }) }
                    }

                    is TransactionResult.Success -> {
                        _state.update { TransactionProcessState.LoadingTransaction(LoadingState.Success) }
                        delay(2.seconds)
                        _state.update { TransactionProcessState.TransactionProcessOk(result.authorizationCode) }
                    }
                }
            }
        }
    }

    fun setPromptValue(key: String, value: String) {
        pendingPrompts.removeAt(0)
        operationStateRepository.updateState {
            it.copy(
                prompts = it.prompts.map { prompt ->
                    if (prompt.key == key) {
                        prompt.copy(value = value, state = Prompt.PromptState.Completed)
                    } else {
                        prompt
                    }
                }
            )
        }
        if (pendingPrompts.isNotEmpty()) {
            val prompt = pendingPrompts.first()
            _state.update { TransactionProcessState.RequiredPrompts(prompt, PromptTitle.entries.first { it.key == prompt.key }) }
        } else {
            _state.update { TransactionProcessState.LoadingTransaction(LoadingState.Loading) }
            sendPreAuthorization()
        }
    }
}