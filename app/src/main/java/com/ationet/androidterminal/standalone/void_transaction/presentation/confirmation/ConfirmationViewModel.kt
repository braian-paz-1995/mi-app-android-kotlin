package com.ationet.androidterminal.standalone.void_transaction.presentation.confirmation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ationet.androidterminal.core.domain.model.LoadingState
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.model.configuration.Configuration.Companion.Defaults
import com.ationet.androidterminal.core.domain.model.preauthorization.InputType
import com.ationet.androidterminal.core.domain.model.transaction.TransactionView
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import com.ationet.androidterminal.core.domain.use_case.transactions.GetVoidableTransactionByAuthorizationCodeUseCase
import com.ationet.androidterminal.core.util.LocalDateTimeUtils
import com.ationet.androidterminal.standalone.preauthorization.domain.model.Quantity
import com.ationet.androidterminal.standalone.void_transaction.domain.use_case.ExecuteVoidTransaction
import com.ationet.androidterminal.standalone.void_transaction.domain.use_case.TransactionResult
import com.ationet.androidterminal.standalone.void_transaction.presentation.VoidTransactionDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

data class ConfirmationData(
    val date: String = "",
    val authorizationCode: String = "",
    val transactionType: String = "",
    val quantity: Quantity = Quantity(),
    val currency: String = "",
    val fuelMeasureUnit: String = "",
    val language: Configuration.LanguageType = Defaults.DEFAULT_LANGUAGE,
    val unitPrice: Double = 0.0
)

sealed interface ConfirmationState {
    data class Confirmation(val confirmationData: ConfirmationData) : ConfirmationState
    data class LoadingTransaction(val loadingState: LoadingState) : ConfirmationState
    data class TransactionProcessOk(val authorizationCode: String) : ConfirmationState
    data class TransactionProcessError(val code: String, val message: String) : ConfirmationState
    data object CommunicationError : ConfirmationState
}

@HiltViewModel
class ConfirmationViewModel @Inject constructor(
    configurationUseCase: ConfigurationUseCase,
    handle: SavedStateHandle,
    private val executeVoidTransaction: ExecuteVoidTransaction,
    private val getVoidableTransactionByAuthorizationCode: GetVoidableTransactionByAuthorizationCodeUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow<ConfirmationState>(ConfirmationState.Confirmation(ConfirmationData()))
    val state = _state.asStateFlow()

    private val configuration = configurationUseCase.getConfiguration()
    private var confirmationData by mutableStateOf(ConfirmationData())
    private val authorizationCode = handle.toRoute<VoidTransactionDestination.Confirmation>().authorizationCode
    private val transactionType = handle.toRoute<VoidTransactionDestination.Confirmation>().typeTransaction
    private var voidTransaction: TransactionView? = null

    init {
        viewModelScope.launch {
            val currentInstant = Clock.System.now()
            voidTransaction = getTransactionView()
            confirmationData = ConfirmationData(
                date = LocalDateTimeUtils.convertToDateTimeFormat("dd/MM/yyyy HH:mm:ss").format(currentInstant.toLocalDateTime(TimeZone.currentSystemDefault())),
                authorizationCode = authorizationCode,
                transactionType = transactionType,
                quantity = Quantity(
                    inputType = when (voidTransaction?.transactionData?.product?.inputType) {
                        "Amount" -> InputType.Amount
                        "Quantity" -> InputType.Quantity
                        else -> if (voidTransaction?.transactionData?.product?.amount != null) InputType.Amount else InputType.Quantity
                    },
                    value = when (voidTransaction?.transactionData?.product?.inputType) {
                        "Amount" -> voidTransaction?.transactionData?.product?.amount ?: 0.0
                        "Quantity" -> voidTransaction?.transactionData?.product?.quantity ?: 0.0
                        else -> if (voidTransaction?.transactionData?.product?.amount != null) voidTransaction?.transactionData?.product?.amount
                            ?: 0.0 else voidTransaction?.transactionData?.product?.quantity ?: 0.0
                    }
                ),
                currency = configuration.currencyFormat,
                fuelMeasureUnit = configuration.fuelMeasureUnit,
                language = configuration.language,
                unitPrice = voidTransaction?.transactionData?.product?.unitPrice ?: 0.0
            )
            _state.update { ConfirmationState.Confirmation(confirmationData) }
        }
    }

    private suspend fun getTransactionView(): TransactionView {
        val result = getVoidableTransactionByAuthorizationCode.invoke(
            authorizationCode = authorizationCode,
            controllerType = Configuration.ControllerType.STAND_ALONE
        )

        if(result !is GetVoidableTransactionByAuthorizationCodeUseCase.Result.Ok) {
            Log.w(TAG, "Confirmation: transaction not found with authorization code: '$authorizationCode'")
            throw IllegalStateException()
        }

        return result.transaction
    }

    fun onConfirm() {
        if (voidTransaction == null) return

        _state.update { ConfirmationState.LoadingTransaction(LoadingState.Loading) }

        viewModelScope.launch {
            when (val result = executeVoidTransaction(transactionView = voidTransaction!!)) {
                TransactionResult.CommunicationError -> {
                    _state.update { ConfirmationState.LoadingTransaction(LoadingState.Failure) }
                    delay(2000)
                    _state.update {
                        ConfirmationState.CommunicationError
                    }
                }

                is TransactionResult.Error -> {
                    _state.update { ConfirmationState.LoadingTransaction(LoadingState.Failure) }
                    delay(2000)
                    _state.update { ConfirmationState.TransactionProcessError(result.code, result.message) }
                }

                is TransactionResult.Success -> {
                    _state.update { ConfirmationState.LoadingTransaction(LoadingState.Success) }
                    delay(2000)
                    _state.update { ConfirmationState.TransactionProcessOk(result.authorizationCode) }
                }
            }
        }
    }

    private companion object {
        private const val TAG: String = "ConfirmationViewModel"
    }
}