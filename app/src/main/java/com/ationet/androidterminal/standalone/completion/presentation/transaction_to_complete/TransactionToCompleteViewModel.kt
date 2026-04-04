package com.ationet.androidterminal.standalone.completion.presentation.transaction_to_complete

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ationet.androidterminal.core.domain.model.preauthorization.InputType
import com.ationet.androidterminal.core.domain.model.preauthorization.PreAuthorizationStandalone
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.core.presentation.util.DisplayType
import com.ationet.androidterminal.standalone.completion.domain.use_cases.GetPreAuthorizationByIdentifier
import com.ationet.androidterminal.standalone.completion.domain.use_cases.PreAuthorizationByIdentifierResult
import com.ationet.androidterminal.standalone.completion.navigation.CompletionDestination
import com.ationet.androidterminal.standalone.completion.navigation.PreAuthorizationData
import com.ationet.androidterminal.standalone.completion.navigation.PreAuthorizationProductData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionToCompleteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getConfiguration: GetConfiguration,
    private val getPreAuthorizationByIdentifier: GetPreAuthorizationByIdentifier,
) : ViewModel() {
    private val arguments =
        savedStateHandle.toRoute<CompletionDestination.TransactionToCompleteConfirmation>()
    private val _state: MutableStateFlow<TransactionToCompleteState> =
        MutableStateFlow(TransactionToCompleteState.Loading)
    val state: StateFlow<TransactionToCompleteState> get() = _state.asStateFlow()


    init {
        loadTransactionToComplete()
    }

    private fun loadTransactionToComplete() {
        viewModelScope.launch {
            when (val result = getPreAuthorizationByIdentifier.invoke(arguments.identification)) {
                PreAuthorizationByIdentifierResult.Error,
                PreAuthorizationByIdentifierResult.NotFound -> setNotFound()
                is PreAuthorizationByIdentifierResult.PreAuthorizationFound -> setTransaction(
                    preAuthorizationData = result.preAuthorization
                )
            }
        }
    }

    private fun setTransaction(preAuthorizationData: PreAuthorizationStandalone) {
        Log.i(TAG, "Pre-authorization found. Id=${preAuthorizationData.preAuthorization.id}")

        val unitPrice = if(preAuthorizationData.preAuthorization.authorization.authorizedPrice == null) {
            Log.w(TAG, "Pre-authorization unit price missing. Using local value=${preAuthorizationData.productUnitPrice}")
            preAuthorizationData.productUnitPrice
        } else {
            preAuthorizationData.preAuthorization.authorization.authorizedPrice
        }

        val maxAmount = with(preAuthorizationData.preAuthorization.authorization) {
            when {
                authorizedAmount != null -> authorizedAmount
                authorizedQuantity != null -> {
                    authorizedQuantity * unitPrice
                }
                else -> null
            }
        }

        val maxQuantity = with(preAuthorizationData.preAuthorization.authorization) {
            when {
                authorizedQuantity != null -> authorizedQuantity
                authorizedAmount != null -> {
                    authorizedAmount / unitPrice
                }
                else -> null
            }
        }

        Log.i(TAG, "Pre-authorization input type=${preAuthorizationData.inputType}. Max amount='$maxAmount'. Max quantity='$maxQuantity'")
        val configuration = getConfiguration.invoke()

        _state.update {
            TransactionToCompleteState.Transaction(
                preAuthorizationData = with(preAuthorizationData.preAuthorization) {
                    PreAuthorizationData(
                        id = id,
                        authorizationCode = authorization.authorizationCode,
                        product = PreAuthorizationProductData(
                            name = preAuthorizationData.productName,
                            unitPrice = unitPrice,
                            code = preAuthorizationData.productCode,
                        ),
                        quantity = authorization.authorizedQuantity,
                        amount = authorization.authorizedAmount,
                        preAuthorizationType = when (preAuthorizationData.inputType) {
                            InputType.Quantity -> DisplayType.QUANTITY
                            InputType.Amount -> DisplayType.AMOUNT
                            InputType.FillUp -> DisplayType.FILLUP
                        },
                        identification = arguments.identification,
                        maxAmount = maxAmount,
                        maxQuantity = maxQuantity,
                        currencySymbol = configuration.currencyFormat,
                        quantityUnit = configuration.fuelMeasureUnit,
                        language = configuration.language
                    )
                }
            )
        }
    }

    private fun setNotFound() {
        _state.update { TransactionToCompleteState.NoTransaction }
    }

    private companion object {
        private const val TAG: String = "Transaction2CompleteVM"
    }
}

sealed interface TransactionToCompleteState {
    data object Loading : TransactionToCompleteState
    data class Transaction(
        val preAuthorizationData: PreAuthorizationData,
    ) : TransactionToCompleteState

    data object NoTransaction : TransactionToCompleteState
}