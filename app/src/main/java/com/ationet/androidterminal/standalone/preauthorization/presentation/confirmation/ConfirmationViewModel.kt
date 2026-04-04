package com.ationet.androidterminal.standalone.preauthorization.presentation.confirmation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.model.configuration.Configuration.Companion.Defaults
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import com.ationet.androidterminal.core.util.LocalDateTimeUtils
import com.ationet.androidterminal.standalone.preauthorization.data.local.PreAuthorizationOperationStateRepository
import com.ationet.androidterminal.standalone.preauthorization.domain.model.ProductStandAlone
import com.ationet.androidterminal.standalone.preauthorization.domain.model.Quantity
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val productName: String = "",
    val quantity: Quantity = Quantity(),
    val currency: String = "",
    val fuelMeasureUnit: String = "",
    val language: Configuration.LanguageType = Defaults.DEFAULT_LANGUAGE
)

sealed interface ConfirmationState {
    data class Confirmation(val confirmationData: ConfirmationData) : ConfirmationState
    data class ConfirmationFillUp(val confirmationData: ConfirmationData) : ConfirmationState
}

@HiltViewModel
class ConfirmationViewModel @Inject constructor(
    private val operationStateRepository: PreAuthorizationOperationStateRepository,
    private val configurationUseCase: ConfigurationUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<ConfirmationState>(ConfirmationState.Confirmation(ConfirmationData()))
    val state = _state.asStateFlow()

    val configuration = configurationUseCase.getConfiguration()

    private var confirmationData by mutableStateOf(ConfirmationData())

    init {
        viewModelScope.launch {
            val currentInstant = Clock.System.now()
            val operationState = operationStateRepository.getState()
            confirmationData = ConfirmationData(
                date = LocalDateTimeUtils.convertToDateTimeFormat("dd/MM/yyyy HH:mm:ss").format(currentInstant.toLocalDateTime(TimeZone.currentSystemDefault())),
                productName = (operationState.product as ProductStandAlone).name,
                quantity = operationState.quantity,
                currency = configuration.currencyFormat,
                fuelMeasureUnit = configuration.fuelMeasureUnit,
                language = configuration.language
            )
            _state.update {
                if (operationState.quantity.value == 0.0) {
                    ConfirmationState.ConfirmationFillUp(confirmationData)
                } else {
                    ConfirmationState.Confirmation(confirmationData)
                }

            }
        }
    }

}