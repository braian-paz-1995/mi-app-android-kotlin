package com.ationet.androidterminal.standalone.sale.presentation.transaction_amount

import androidx.lifecycle.ViewModel
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.standalone.sale.data.local.SaleOperationStateRepository
import com.ationet.androidterminal.standalone.sale.domain.model.Quantity
import dagger.hilt.android.lifecycle.HiltViewModel
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
}