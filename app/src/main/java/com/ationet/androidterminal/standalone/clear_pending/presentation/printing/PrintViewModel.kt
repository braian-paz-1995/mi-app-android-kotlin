package com.ationet.androidterminal.standalone.clear_pending.presentation.printing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ationet.androidterminal.core.domain.util.PrinterErrorCodes
import com.ationet.androidterminal.standalone.clear_pending.domain.use_case.PrintClearPendingResult
import com.ationet.androidterminal.standalone.clear_pending.domain.use_case.PrintClearPendingUseCase
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
    private val printClearPending: PrintClearPendingUseCase
) : ViewModel() {
    private val _state: MutableStateFlow<PrintState> = MutableStateFlow(PrintState.Printing)
    val state: StateFlow<PrintState> get() = _state.asStateFlow()

    private var isCopy: Boolean = false

    init {
        viewModelScope.launch {
            printOriginal()
        }
    }

    private suspend fun printOriginal() {
        val result = try {
            printClearPending.invoke(
                isCopy = isCopy
            )
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
            is PrintClearPendingResult.Error -> {
                _state.update {
                    PrintState.PrintingError(
                        errorCode = result.code,
                        error = PrintState.PrinterError.Error
                    )
                }
            }

            PrintClearPendingResult.OutOfPaper -> {
                _state.update {
                    PrintState.PrintingError(
                        errorCode = PrinterErrorCodes.OUT_OF_PAPER,
                        error = PrintState.PrinterError.OutOfPaper
                    )
                }
            }

            PrintClearPendingResult.Ok -> {
                _state.update { PrintState.PrintingDone }
                isCopy = true
            }
        }
    }

    fun onPrintCopy() {
        viewModelScope.launch {
            printCopy()
        }
    }

    private suspend fun printCopy() {
        _state.update { PrintState.PrintingCopy }
        val result = try {
            printClearPending.invoke(
                isCopy = isCopy
            )
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
            is PrintClearPendingResult.Error -> {
                _state.update {
                    PrintState.PrintingError(
                        errorCode = result.code,
                        error = PrintState.PrinterError.Error
                    )
                }
            }

            PrintClearPendingResult.OutOfPaper -> {
                _state.update {
                    PrintState.PrintingError(
                        errorCode = PrinterErrorCodes.OUT_OF_PAPER,
                        error = PrintState.PrinterError.OutOfPaper
                    )
                }
            }

            PrintClearPendingResult.Ok -> {
                _state.update { PrintState.PrintingCopyDone }
                isCopy = true
            }
        }
    }

    fun onRetry() {
        viewModelScope.launch {
            if (isCopy) {
                printCopy()
            } else {
                printOriginal()
            }
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

    data object PrintingCopy : PrintState
    data object PrintingCopyDone : PrintState
    data class PrintingError(
        val errorCode: String,
        val error: PrinterError,
    ) : PrintState
}