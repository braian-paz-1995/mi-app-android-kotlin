package com.ationet.androidterminal.standalone.sale.presentation.summary

import androidx.lifecycle.ViewModel
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.standalone.sale.data.local.SaleOperationStateRepository
import com.ationet.androidterminal.standalone.sale.domain.model.ProductStandAlone
import com.ationet.androidterminal.standalone.sale.domain.model.Quantity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

data class SummaryData(
    val date: LocalDateTime? = null,
    val authorizationCode: String,
    val product: String,
    val unitPrice: Double,
    val quantity: Quantity,
    val currency: String,
    val fuelMeasureUnit: String,
    val language: Configuration.LanguageType,
    val operationType: Boolean
)

sealed interface SummaryState {
    data class Summary(val data: SummaryData) : SummaryState
}

@HiltViewModel
class SummaryViewModel @Inject constructor(
    operationStateRepository: SaleOperationStateRepository,
    getConfiguration: GetConfiguration
) : ViewModel() {
    private val _state: MutableStateFlow<SummaryState>
    val state get() = _state.asStateFlow()

    private val operationState = operationStateRepository.getState()

    init {
        val configuration = getConfiguration()
        val product = (operationState.product as ProductStandAlone)

        val initialState =  SummaryState.Summary(
            SummaryData(
                date = operationState.receipt?.transactionLine?.dateTime,
                authorizationCode = operationState.authorizationCode,
                product = product.name,
                unitPrice = product.unitPrice,
                quantity = operationState.quantity,
                fuelMeasureUnit = configuration.fuelMeasureUnit,
                currency = configuration.currencyFormat,
                language = configuration.language,
                operationType = operationState.product.isFuel
            )
        )

        _state = MutableStateFlow(initialState)
    }
}