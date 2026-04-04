package com.ationet.androidterminal.standalone.clear_pending.presentation.transaction_confirmation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ationet.androidterminal.core.domain.model.preauthorization.InputType
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.core.presentation.util.DisplayType
import com.ationet.androidterminal.standalone.clear_pending.data.local.ClearPendingOperationStateRepository
import com.ationet.androidterminal.standalone.clear_pending.domain.model.PreAuthorizationData
import com.ationet.androidterminal.standalone.clear_pending.domain.model.PreAuthorizationProductData
import com.ationet.androidterminal.standalone.clear_pending.presentation.ClearPendingTransactionDestination
import com.ationet.androidterminal.standalone.completion.domain.use_cases.GetPreAuthorizationByIdentifier
import com.ationet.androidterminal.standalone.completion.domain.use_cases.PreAuthorizationByIdentifierResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface TransactionConfirmationState {
    data class TransactionConfirmation(val data: TransactionConfirmationData) : TransactionConfirmationState
    data object NoTransactionFound : TransactionConfirmationState
}

data class TransactionConfirmationData(
    val authorizationData: PreAuthorizationData,
)

@HiltViewModel
class TransactionConfirmationViewModel @Inject constructor(
    getConfiguration: GetConfiguration,
    private val handle: SavedStateHandle,
    private val getPreAuthorizationByIdentifier: GetPreAuthorizationByIdentifier,
    private val operationStateRepository: ClearPendingOperationStateRepository,
) : ViewModel() {
    private val operationState = operationStateRepository.getState()
    private var _state = MutableStateFlow<TransactionConfirmationState>(TransactionConfirmationState.NoTransactionFound)
    val state = _state.asStateFlow()
    val configuration = getConfiguration.invoke()
    private val identification = handle.toRoute<ClearPendingTransactionDestination.TransactionConfirmation>().identification

    init {
        getPreAuthorization()
    }

    private fun getPreAuthorization() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = getPreAuthorizationByIdentifier.invoke(identification.ifEmpty {
                operationState.identifier.primaryTrack
            })) {
                PreAuthorizationByIdentifierResult.Error -> TODO()
                PreAuthorizationByIdentifierResult.NotFound -> {
                    _state.update { TransactionConfirmationState.NoTransactionFound }
                }

                is PreAuthorizationByIdentifierResult.PreAuthorizationFound -> {
                    val preAuthorizationData = with(result.preAuthorization) {
                        PreAuthorizationData(
                            id = preAuthorization.id,
                            authorizationCode = preAuthorization.authorization.authorizationCode,
                            amount = preAuthorization.authorization.authorizedAmount,
                            quantity = preAuthorization.authorization.authorizedQuantity,
                            maxAmount = 0.0,
                            maxQuantity = 0.0,
                            identification = preAuthorization.identification.primaryTrack,
                            preAuthorizationType = when (inputType) {
                                InputType.Quantity -> DisplayType.QUANTITY
                                InputType.Amount -> DisplayType.AMOUNT
                                InputType.FillUp -> if (configuration.ationet.promptAmountTransaction) {
                                    DisplayType.AMOUNT
                                } else {
                                    DisplayType.QUANTITY
                                }
                            },
                            product = PreAuthorizationProductData(
                                name = productName,
                                code = productCode,
                                unitPrice = preAuthorization.authorization.authorizedPrice ?: productUnitPrice
                            ),
                            currencySymbol = configuration.currencyFormat,
                            quantityUnit = configuration.fuelMeasureUnit,
                            language = configuration.language,
                            date = preAuthorization.authorization.localDateTime
                        )
                    }
                    operationStateRepository.updateState {
                        it.copy(
                            preAuthorizationData = preAuthorizationData
                        )
                    }
                    withContext(Dispatchers.Main.immediate) {
                        _state.update { TransactionConfirmationState.TransactionConfirmation(TransactionConfirmationData(preAuthorizationData)) }
                    }
                }
            }
        }
    }
}