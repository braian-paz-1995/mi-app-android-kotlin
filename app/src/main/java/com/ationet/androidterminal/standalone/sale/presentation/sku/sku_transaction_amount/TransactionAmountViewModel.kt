package com.ationet.androidterminal.standalone.sale.presentation.sku.sku_transaction_amount

import androidx.lifecycle.ViewModel
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.standalone.sale.data.local.SaleOperationStateRepository
import com.ationet.androidterminal.standalone.sale.domain.model.Quantity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TransactionAmountViewModel @Inject constructor(
    private val operationStateRepository: SaleOperationStateRepository,
    getConfiguration: GetConfiguration
) : ViewModel() {
    val configuration = getConfiguration()
    val currency = configuration.currencyFormat
    val fuelMeasureUnit = configuration.fuelMeasureUnit
    val isAmount = configuration.ationet.promptAmountTransaction
    fun setTransaction(value: Double, inputType: Quantity.InputType) {
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



