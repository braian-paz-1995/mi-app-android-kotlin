package com.ationet.androidterminal.core.change_pin.presentation.printing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ationet.androidterminal.core.change_pin.domain.use_case.PrintChangePinResult
import com.ationet.androidterminal.core.change_pin.domain.use_case.PrintChangePinTicketUseCase
import com.ationet.androidterminal.core.domain.util.PrinterErrorCodes
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
class ChangePinPrintViewModel @Inject constructor(
    private val printChangePin: PrintChangePinTicketUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<ChangePinPrintState> =
        MutableStateFlow(ChangePinPrintState.Printing)
    val state: StateFlow<ChangePinPrintState> get() = _state.asStateFlow()

    private var isCopy: Boolean = false
    private var started: Boolean = false

    fun start(skipOriginal: Boolean) {
        if (started) return
        started = true

        viewModelScope.launch {
            if (skipOriginal) {
                isCopy = true
                printCopy()
            } else {
                printOriginal()
            }
        }
    }

    private suspend fun printOriginal() {
        val result = try {
            printChangePin.invoke(isCopy = isCopy)
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            _state.update {
                ChangePinPrintState.PrintingError(
                    errorCode = PrinterErrorCodes.ERROR,
                    error = ChangePinPrintState.PrinterError.Error
                )
            }
            return
        }

        when (result) {
            is PrintChangePinResult.Error -> {
                _state.update {
                    ChangePinPrintState.PrintingError(
                        errorCode = result.code,
                        error = ChangePinPrintState.PrinterError.Error
                    )
                }
            }

            PrintChangePinResult.OutOfPaper -> {
                _state.update {
                    ChangePinPrintState.PrintingError(
                        errorCode = PrinterErrorCodes.OUT_OF_PAPER,
                        error = ChangePinPrintState.PrinterError.OutOfPaper
                    )
                }
            }

            PrintChangePinResult.Ok -> {
                _state.update { ChangePinPrintState.PrintingDone }
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
        _state.update { ChangePinPrintState.PrintingCopy }

        val result = try {
            printChangePin.invoke(isCopy = isCopy)
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            _state.update {
                ChangePinPrintState.PrintingError(
                    errorCode = PrinterErrorCodes.ERROR,
                    error = ChangePinPrintState.PrinterError.Error
                )
            }
            return
        }

        when (result) {
            is PrintChangePinResult.Error -> {
                _state.update {
                    ChangePinPrintState.PrintingError(
                        errorCode = result.code,
                        error = ChangePinPrintState.PrinterError.Error
                    )
                }
            }

            PrintChangePinResult.OutOfPaper -> {
                _state.update {
                    ChangePinPrintState.PrintingError(
                        errorCode = PrinterErrorCodes.OUT_OF_PAPER,
                        error = ChangePinPrintState.PrinterError.OutOfPaper
                    )
                }
            }

            PrintChangePinResult.Ok -> {
                _state.update { ChangePinPrintState.PrintingCopyDone }
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

sealed interface ChangePinPrintState {
    enum class PrinterError {
        Ok,
        OutOfPaper,
        OverHeat,
        Error,
    }

    data object Printing : ChangePinPrintState
    data object PrintingDone : ChangePinPrintState
    data object PrintingCopy : ChangePinPrintState
    data object PrintingCopyDone : ChangePinPrintState

    data class PrintingError(
        val errorCode: String,
        val error: PrinterError,
    ) : ChangePinPrintState
}