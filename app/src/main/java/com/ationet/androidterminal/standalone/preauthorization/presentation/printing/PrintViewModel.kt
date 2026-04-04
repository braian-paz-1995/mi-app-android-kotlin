package com.ationet.androidterminal.standalone.preauthorization.presentation.printing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ationet.androidterminal.standalone.preauthorization.domain.use_case.PrintPreAuthorizationResult
import com.ationet.androidterminal.standalone.preauthorization.domain.use_case.PrintPreAuthorizationTicketUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrintViewModel @Inject constructor(
    private val printPreAuthorizationTicket: PrintPreAuthorizationTicketUseCase
) : ViewModel() {
    private val _state: MutableStateFlow<PrintState> = MutableStateFlow(PrintState.Printing)
    val state: StateFlow<PrintState> get() = _state.asStateFlow()

    private var isPrintingCopy: Boolean = false

    init {
        viewModelScope.launch {
            printOriginalTicket()
        }
    }

    private suspend fun printOriginalTicket() {
        when (val result = printPreAuthorizationTicket.invoke(isCopy = false)) {
            is PrintPreAuthorizationResult.Error -> {
                _state.update {
                    PrintState.PrintingError(
                        errorCode = result.code
                    )
                }
            }

            PrintPreAuthorizationResult.Ok -> {
                _state.update { PrintState.PrintingDone }
            }

            PrintPreAuthorizationResult.OutOfPaper -> {
                _state.update { PrintState.PrinterOutOfPaper }
            }
        }
    }

    private suspend fun printCopyTicket() {
        _state.update { PrintState.PrintingCopy }
        when (val result = printPreAuthorizationTicket.invoke(isCopy = true)) {
            is PrintPreAuthorizationResult.Error -> {
                _state.update {
                    PrintState.PrintingError(
                        errorCode = result.code
                    )
                }
            }

            PrintPreAuthorizationResult.Ok -> {
                _state.update { PrintState.PrintingCopyDone }
            }

            PrintPreAuthorizationResult.OutOfPaper -> {
                _state.update { PrintState.PrinterOutOfPaper }
            }
        }
    }

    fun onRetry() {
        if(isPrintingCopy) {
            viewModelScope.launch { printCopyTicket() }
        } else {
            viewModelScope.launch { printOriginalTicket() }
        }
    }

    fun onPrintCopy() {
        isPrintingCopy = true

        viewModelScope.launch {
            printCopyTicket()
        }
    }
}

sealed interface PrintState {
    data object Printing : PrintState

    data object PrintingDone : PrintState

    data object PrintingCopy : PrintState
    data object PrintingCopyDone : PrintState
    data object PrinterOutOfPaper : PrintState
    data class PrintingError(
        val errorCode: String,
    ) : PrintState
}