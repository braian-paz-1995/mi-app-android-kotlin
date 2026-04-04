package com.ationet.androidterminal.standalone.batch_close.presentation.summary

import androidx.lifecycle.ViewModel
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.core.util.LocaleFormatter
import com.ationet.androidterminal.standalone.batch_close.data.local.BatchCloseStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SummaryData(
    val batchId: String = "",
    val salesCount: String = "",
    val salesTotal: String = "",
    val cancelledSalesCount: String = "",
    val cancelledSalesTotal: String = "",
    val rechargeCCCount: String = "",
    val rechargeCCTotal: String = "",
    val reverseCCCount: String = "",
    val reverseCCTotal: String = "",
)

sealed interface SummaryState {
    data class Summary(val data: SummaryData) : SummaryState
}

@HiltViewModel
class SummaryViewModel @Inject constructor(
    operationStateRepository: BatchCloseStateRepository,
    getConfiguration: GetConfiguration
) : ViewModel() {
    private val _state = MutableStateFlow<SummaryState>(SummaryState.Summary(SummaryData()))
    val state = _state.asStateFlow()
    val configuration = getConfiguration.invoke()

    val operationState = operationStateRepository.getState()
    val currencyFormat = configuration.currencyFormat

    init {
        _state.update {
            SummaryState.Summary(
                SummaryData(
                    batchId = operationState.batchId,
                    salesCount = operationState.countSales.toString(),
                    salesTotal = LocaleFormatter.formatNumber(operationState.totalSales.toString(), 2, configuration.language),
                    cancelledSalesCount = operationState.countVoid.toString(),
                    cancelledSalesTotal = LocaleFormatter.formatNumber(operationState.totalVoid.toString(), 2, configuration.language),
                    rechargeCCCount = operationState.countRechargeCC.toString(),
                    rechargeCCTotal = LocaleFormatter.formatNumber(operationState.totalRechargeCC.toString(), 2, configuration.language),
                    reverseCCCount = operationState.countReverseCC.toString(),
                    reverseCCTotal = LocaleFormatter.formatNumber(operationState.totalReverseCC.toString(), 2, configuration.language),
                )
            )
        }
    }
}