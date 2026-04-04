package com.ationet.androidterminal.standalone.balance_enquiry.presentation.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavType
import androidx.navigation.toRoute
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.standalone.balance_enquiry.data.local.Quantity
import com.ationet.androidterminal.standalone.balance_enquiry.presentation.BalanceEnquiryDestination
import com.ationet.androidterminal.standalone.balance_enquiry.presentation.util.BalanceEnquiryNavType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import javax.inject.Inject
import kotlin.reflect.typeOf

@Serializable
data class SummaryData(
    val date: LocalDateTime,
    val authorizationCode: String,
    val productName: String,
    val quantity: Quantity,
    val currencyFormat: String,
    val fuelMeasureUnit: String,
    val language: Configuration.LanguageType
)

sealed interface SummaryState {
    data class Summary(val data: SummaryData) : SummaryState
}

@HiltViewModel
class SummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val summary = savedStateHandle.toRoute<BalanceEnquiryDestination.Summary>(
        typeMap = mapOf(
            typeOf<Double>() to BalanceEnquiryNavType.Double,
            typeOf<SummaryData>() to BalanceEnquiryNavType.SummaryData,
            typeOf<Configuration.LanguageType>() to NavType.EnumType(Configuration.LanguageType::class.java),
            typeOf<Quantity.InputType>() to NavType.EnumType(Quantity.InputType::class.java),
            typeOf<LocalDateTime>() to BalanceEnquiryNavType.LocalDateTime
        )
    ).summary
    private val _state: MutableStateFlow<SummaryState> = MutableStateFlow(SummaryState.Summary(summary))
    val state get() = _state.asStateFlow()
}