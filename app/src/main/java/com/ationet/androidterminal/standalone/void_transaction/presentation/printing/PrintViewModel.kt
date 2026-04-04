package com.ationet.androidterminal.standalone.void_transaction.presentation.printing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ationet.androidterminal.core.domain.util.PrinterErrorCodes
import com.ationet.androidterminal.standalone.void_transaction.domain.use_case.PrintVoidTransactionResult
import com.ationet.androidterminal.standalone.void_transaction.domain.use_case.PrintVoidTransactionTicketUseCase
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
    private val printVoidTransactionUseCase: PrintVoidTransactionTicketUseCase,
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
            printVoidTransactionUseCase.invoke(false)
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
            PrintVoidTransactionResult.Ok -> {
                _state.update { PrintState.PrintingDone }
                isCopy = true
            }

            is PrintVoidTransactionResult.Error -> {
                _state.update {
                    PrintState.PrintingError(
                        errorCode = result.code,
                        error = PrintState.PrinterError.Error
                    )
                }
            }

            PrintVoidTransactionResult.OutOfPaper -> {
                _state.update {
                    PrintState.PrintingError(
                        errorCode = PrinterErrorCodes.OUT_OF_PAPER,
                        error = PrintState.PrinterError.OutOfPaper
                    )
                }
            }
        }
    }

    private suspend fun printCopy() {
        _state.update { PrintState.PrintingCopy }
        val result = try {
            printVoidTransactionUseCase.invoke(true)
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
            PrintVoidTransactionResult.Ok -> {
                _state.update { PrintState.PrintingCopyDone }
                isCopy = true
            }

            is PrintVoidTransactionResult.Error -> {
                _state.update {
                    PrintState.PrintingError(
                        errorCode = result.code,
                        error = PrintState.PrinterError.Error
                    )
                }
            }

            PrintVoidTransactionResult.OutOfPaper -> {
                _state.update {
                    PrintState.PrintingError(
                        errorCode = PrinterErrorCodes.OUT_OF_PAPER,
                        error = PrintState.PrinterError.OutOfPaper
                    )
                }
            }
        }
    }

    fun onPrintCopy() {
        viewModelScope.launch {
            printCopy()
        }
    }

    fun onRetry() {
        viewModelScope.launch {
            if(isCopy) {
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