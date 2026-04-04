package com.ationet.androidterminal.standalone.batch_close.presentation.printing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ationet.androidterminal.core.domain.util.PrinterErrorCodes
import com.ationet.androidterminal.standalone.batch_close.domain.use_case.PrintBatchCloseResult
import com.ationet.androidterminal.standalone.batch_close.domain.use_case.PrintBatchCloseTicketUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrintViewModel @Inject constructor(
    private val printBatchCloseTicketUseCase: PrintBatchCloseTicketUseCase
) : ViewModel() {
    private val _state: MutableStateFlow<PrintState> = MutableStateFlow(PrintState.Printing)
    val state: StateFlow<PrintState> get() = _state.asStateFlow()

    init {
        viewModelScope.launch {
            printOriginal()
        }
    }

    private suspend fun printOriginal() {
        val result = try {
            printBatchCloseTicketUseCase.invoke(false)
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            _state.update {
                PrintState.PrintingError(
                    errorCode = PrinterErrorCodes.ERROR,
                    error = PrintState.PrinterError.Error
                )
            }

            return
        }

        when (result) {
            PrintBatchCloseResult.Ok -> {
                _state.update { PrintState.PrintingDone }
            }

            is PrintBatchCloseResult.Error -> {
                _state.update {
                    PrintState.PrintingError(
                        errorCode = result.code,
                        error = PrintState.PrinterError.Error
                    )
                }
            }

            PrintBatchCloseResult.OutOfPaper -> {
                _state.update {
                    PrintState.PrintingError(
                        errorCode = PrinterErrorCodes.OUT_OF_PAPER,
                        error = PrintState.PrinterError.OutOfPaper
                    )
                }
            }
        }
    }

    fun onRetry() {
        viewModelScope.launch {
            printOriginal()
        }
    }
}

sealed interface PrintState {
    enum class PrinterError {
        Ok,
        OutOfPaper,
        OverHeat,
        Error,
    }

    data object Printing : PrintState

    data object PrintingDone : PrintState
    data class PrintingError(
        val errorCode: String,
        val error: PrinterError,
    ) : PrintState
}