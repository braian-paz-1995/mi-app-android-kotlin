package com.ationet.androidterminal.standalone.void_transaction.presentation.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.ationet.androidterminal.core.util.LocalDateTimeUtils
import com.ationet.androidterminal.standalone.void_transaction.presentation.VoidTransactionDestination
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
    val authorizationCode: String = "",
)

sealed interface SummaryState {
    data class Summary(val data: SummaryData) : SummaryState
}

@HiltViewModel
class SummaryViewModel @Inject constructor(
    handle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow<SummaryState>(SummaryState.Summary(SummaryData()))
    val state = _state.asStateFlow()

    private val authorizationCode = handle.toRoute<VoidTransactionDestination.Summary>().newAuthorizationCode
    private val currentInstant = Clock.System.now()

    init {
        _state.update {
            SummaryState.Summary(
                SummaryData(
                    date = LocalDateTimeUtils.convertToDateTimeFormat("dd/MM/yyyy HH:mm:ss").format(
                        currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
                    ),
                    authorizationCode = authorizationCode
                )
            )
        }
    }
}