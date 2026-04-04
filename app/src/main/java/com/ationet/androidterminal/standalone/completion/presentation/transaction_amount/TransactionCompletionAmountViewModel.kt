package com.ationet.androidterminal.standalone.completion.presentation.transaction_amount

import androidx.lifecycle.ViewModel
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TransactionCompletionAmountViewModel @Inject constructor(
    private val configurationUseCase: ConfigurationUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(TransactionCompletionAmountState())
    val state = _state.asStateFlow()

    val configuration = configurationUseCase.getConfiguration()

    init {
        _state.value = _state.value.copy(
            isAmount = configuration.ationet.promptAmountTransaction,
            isCompanyPrice = configuration.ticket.transactionDetails,
            isEnableFinalizationVariance = configuration.fusion.enableFinalizationVariance
        )
    }

    companion object {
        const val MAXIMUM_VALUE_PERMITTED = 8
    }
}


data class TransactionCompletionAmountState(
    val isAmount: Boolean = true,
    val isCompanyPrice: Boolean = true,
    val isEnableFinalizationVariance: Boolean = false
)