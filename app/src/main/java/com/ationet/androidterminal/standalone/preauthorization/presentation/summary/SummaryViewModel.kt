package com.ationet.androidterminal.standalone.preauthorization.presentation.summary

import androidx.lifecycle.ViewModel
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.model.preauthorization.InputType
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import com.ationet.androidterminal.core.util.LocalDateTimeUtils
import com.ationet.androidterminal.core.util.LocaleFormatter
import com.ationet.androidterminal.standalone.preauthorization.data.local.PreAuthorizationOperationStateRepository
import com.ationet.androidterminal.standalone.preauthorization.domain.model.ProductStandAlone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

data class SummaryData(
    val date: String = "",
    val authCode: String = "",
    val product: String = "",
    val quantityRequested: String? = "0.0",
    val quantityAuthorized: String? = "0.0",
    val amountRequested: String? = "0.0",
    val amountAuthorized: String? = "0.0",
    val currency: String = "0.0",
    val fuelMeasureUnit: String = "0.0",
    val selectedInputType: InputType? = null,
    val language: Configuration.LanguageType = Configuration.LanguageType.EN
)

sealed interface SummaryState {
    data class Summary(val data: SummaryData) : SummaryState
}

@HiltViewModel
class SummaryViewModel @Inject constructor(
    operationStateRepository: PreAuthorizationOperationStateRepository,
    configurationUseCase: ConfigurationUseCase
) : ViewModel() {
    val configuration = configurationUseCase.getConfiguration()

    private val _state = MutableStateFlow<SummaryState>(SummaryState.Summary(SummaryData()))
    val state = _state.asStateFlow()

    private val operationState = operationStateRepository.getState()

    init {
        _state.update {
            SummaryState.Summary(
                SummaryData(
                    date = LocalDateTimeUtils.convertToDateTimeFormat("dd/MM/yyyy HH:mm:ss")
                        .format(
                            operationState.authorizationData.localDateTime ?: Clock.System.now()
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                        ),
                    authCode = operationState.authorizationData.authorizationCode,
                    product = (operationState.product as ProductStandAlone).name,
                    amountRequested = if (operationState.quantity.inputType == InputType.Amount)
                        LocaleFormatter.formatNumber(
                        (operationState.quantity.value.toString()).toString(),
                        decimals = 2,
                        language = configuration.language
                    ) else null,
                    amountAuthorized =
                    operationState.authorizationData.amount?.let {
                        LocaleFormatter.formatNumber(
                            it.toString(),
                            decimals = 2,
                            language = configuration.language
                        )
                    } ?: null,
                    quantityRequested = if (operationState.quantity.inputType == InputType.Quantity)
                        LocaleFormatter.formatNumber(
                            (operationState.quantity?.value).toString(),
                            decimals = 3,
                            language = configuration.language
                        ) else null,
                    quantityAuthorized =
                    operationState.authorizationData.quantity?.let {
                        LocaleFormatter.formatNumber(
                            it.toString(),
                            decimals = 3,
                            language = configuration.language
                        )
                    } ?: null,
                    currency = configuration.currencyFormat,
                    fuelMeasureUnit = configuration.fuelMeasureUnit,
                    selectedInputType = operationState.quantity.inputType,
                    language = configuration.language
                )
            )
        }
    }
}