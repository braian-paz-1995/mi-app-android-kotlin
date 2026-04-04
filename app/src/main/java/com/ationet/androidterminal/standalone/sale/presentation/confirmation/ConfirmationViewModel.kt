package com.ationet.androidterminal.standalone.sale.presentation.confirmation

import androidx.lifecycle.ViewModel
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.standalone.sale.data.local.SaleOperationStateRepository
import com.ationet.androidterminal.standalone.sale.domain.model.ProductStandAlone
import com.ationet.androidterminal.standalone.sale.domain.model.Quantity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ConfirmationData(
    val productName: String,
    val unitPrice: Double,
    val quantity: Quantity,
    val currency: String,
    val fuelMeasureUnit: String,
    val language: Configuration.LanguageType,
    val operationType: Boolean
)

sealed interface ConfirmationState {
    data class Confirmation(val confirmationData: ConfirmationData) : ConfirmationState
}

@HiltViewModel
class ConfirmationViewModel @Inject constructor(
    operationStateRepository: SaleOperationStateRepository,
    getConfiguration: GetConfiguration
) : ViewModel() {
    private val _state: MutableStateFlow<ConfirmationState>
    val state get() = _state.asStateFlow()

    init {
        val configuration = getConfiguration()
        val operationState = operationStateRepository.getState()

        val product = operationState.product as ProductStandAlone
        val initialValue = ConfirmationData(
            productName = product.name,
            unitPrice = product.unitPrice,
            quantity = operationState.quantity,
            fuelMeasureUnit = configuration.fuelMeasureUnit,
            currency = configuration.currencyFormat,
            language = configuration.language,
            operationType = product.isFuel
        )

        _state = MutableStateFlow(ConfirmationState.Confirmation(initialValue))
    }
}