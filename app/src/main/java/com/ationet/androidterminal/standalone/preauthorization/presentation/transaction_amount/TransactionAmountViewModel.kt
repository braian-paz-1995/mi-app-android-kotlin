package com.ationet.androidterminal.standalone.preauthorization.presentation.transaction_amount

import androidx.lifecycle.ViewModel
import com.ationet.androidterminal.core.domain.model.preauthorization.InputType
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import com.ationet.androidterminal.standalone.preauthorization.data.local.PreAuthorizationOperationStateRepository
import com.ationet.androidterminal.standalone.preauthorization.domain.model.Quantity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TransactionAmountViewModel @Inject constructor(
    private val operationStateRepository: PreAuthorizationOperationStateRepository,
    configurationUseCase: ConfigurationUseCase
) : ViewModel() {
    private val configuration = configurationUseCase.getConfiguration()

    private val _state: MutableStateFlow<TransactionAmountState> = MutableStateFlow(
        TransactionAmountState(
            currency = configuration.currencyFormat,
            fuelMeasureUnit = configuration.fuelMeasureUnit,
            isAmount = configuration.ationet.promptAmountTransaction
        )
    )
    val state: StateFlow<TransactionAmountState> get() = _state.asStateFlow()

    fun setTransaction(value: Double, inputType: InputType) {
        operationStateRepository.updateState {
            it.copy(
                quantity = Quantity(
                    inputType = inputType,
                    value = value
                )
            )
        }
    }

    companion object {
        const val MAXIMUM_VALUE_PERMITTED = 8
    }
}


data class TransactionAmountState(
    val currency: String,
    val fuelMeasureUnit: String,
    val isAmount: Boolean
)
