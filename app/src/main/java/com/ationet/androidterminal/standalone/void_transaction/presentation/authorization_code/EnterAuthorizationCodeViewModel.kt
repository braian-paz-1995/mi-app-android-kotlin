package com.ationet.androidterminal.standalone.void_transaction.presentation.authorization_code

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.use_case.transactions.GetVoidableTransactionByAuthorizationCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EnterAuthorizationCodeState {
    data class EnterAuthorizationCode(val authorizationCodeError: AuthorizationCodeError? = null) :
        EnterAuthorizationCodeState

    data class EnterAuthorizationCodeOk(
        val authorizationCode: String,
        val transactionType: String
    ) : EnterAuthorizationCodeState
}

sealed class AuthorizationCodeError(@StringRes val message: Int) {
    data object AlreadyBeenProcessed :
        AuthorizationCodeError(R.string.authorization_code_error_already_processed)

    data object InvalidTransactionType :
        AuthorizationCodeError(R.string.authorization_code_error_invalid_transaction_type)

    data object TransactionNotFound :
        AuthorizationCodeError(R.string.authorization_code_error_transaction_not_found)
}

@HiltViewModel
class EnterAuthorizationCodeViewModel @Inject constructor(
    private val getVoidableTransactionByAuthorizationCode: GetVoidableTransactionByAuthorizationCodeUseCase
) : ViewModel() {
    private var _state =
        MutableStateFlow<EnterAuthorizationCodeState>(EnterAuthorizationCodeState.EnterAuthorizationCode())
    val state = _state.asStateFlow()

    fun onAuthorizationCodeEntered(authorizationCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getVoidableTransactionByAuthorizationCode.invoke(
                authorizationCode = authorizationCode,
                controllerType = Configuration.ControllerType.STAND_ALONE
            )

            val newState = when (result) {
                GetVoidableTransactionByAuthorizationCodeUseCase.Result.InvalidTransactionType -> {
                    EnterAuthorizationCodeState.EnterAuthorizationCode(AuthorizationCodeError.InvalidTransactionType)
                }

                GetVoidableTransactionByAuthorizationCodeUseCase.Result.TransactionNotFound -> {
                    EnterAuthorizationCodeState.EnterAuthorizationCode(AuthorizationCodeError.TransactionNotFound)
                }

                is GetVoidableTransactionByAuthorizationCodeUseCase.Result.Ok -> {
                    EnterAuthorizationCodeState.EnterAuthorizationCodeOk(
                        authorizationCode = authorizationCode,
                        transactionType = result.transaction.type.name
                    )
                }
            }

            _state.update { newState }
        }
    }
}