package com.ationet.androidterminal.standalone.completion.presentation.printing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ationet.androidterminal.core.domain.util.PrinterErrorCodes
import com.ationet.androidterminal.standalone.completion.domain.use_cases.PrintCompletionResult
import com.ationet.androidterminal.standalone.completion.domain.use_cases.PrintCompletionUseCase
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
class CompletionPrintViewModel @Inject constructor(
    private val printCompletion: PrintCompletionUseCase
) : ViewModel() {
    private val _state: MutableStateFlow<CompletionPrintState> = MutableStateFlow(
        CompletionPrintState.Printing
    )
    val state: StateFlow<CompletionPrintState> get() = _state.asStateFlow()

    private var isCopy: Boolean = false

    init {
        viewModelScope.launch {
            printOriginal()
        }
    }

    private suspend fun printOriginal() {
        val result = try {
            printCompletion.invoke(
                isCopy = isCopy
            )
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            _state.update {
                CompletionPrintState.PrintingError(
                    errorCode = PrinterErrorCodes.ERROR,
                    error = CompletionPrintState.PrinterError.Error
                )
            }
            return
        }

        when (result) {
            is PrintCompletionResult.Error -> {
                _state.update {
                    CompletionPrintState.PrintingError(
                        errorCode = result.code,
                        error = CompletionPrintState.PrinterError.Error
                    )
                }
            }

            PrintCompletionResult.OutOfPaper -> {
                _state.update {
                    CompletionPrintState.PrintingError(
                        errorCode = PrinterErrorCodes.OUT_OF_PAPER,
                        error = CompletionPrintState.PrinterError.OutOfPaper
                    )
                }
            }

            PrintCompletionResult.Ok -> {
                _state.update { CompletionPrintState.PrintingDone }
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
        _state.update { CompletionPrintState.PrintingCopy }
        val result = try {
            printCompletion.invoke(
                isCopy = isCopy
            )
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            _state.update {
                CompletionPrintState.PrintingError(
                    errorCode = PrinterErrorCodes.ERROR,
                    error = CompletionPrintState.PrinterError.Error
                )
            }
            return
        }

        when (result) {
            is PrintCompletionResult.Error -> {
                _state.update {
                    CompletionPrintState.PrintingError(
                        errorCode = result.code,
                        error = CompletionPrintState.PrinterError.Error
                    )
                }
            }

            PrintCompletionResult.OutOfPaper -> {
                _state.update {
                    CompletionPrintState.PrintingError(
                        errorCode = PrinterErrorCodes.OUT_OF_PAPER,
                        error = CompletionPrintState.PrinterError.OutOfPaper
                    )
                }
            }

            PrintCompletionResult.Ok -> {
                _state.update { CompletionPrintState.PrintingCopyDone }
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

sealed interface CompletionPrintState {
    enum class PrinterError {
        Ok,
        OutOfPaper,
        OverHeat,
        Error,
    }

    data object Printing : CompletionPrintState

    data object PrintingDone : CompletionPrintState

    data object PrintingCopy : CompletionPrintState
    data object PrintingCopyDone : CompletionPrintState
    data class PrintingError(
        val errorCode: String,
        val error: PrinterError,
    ) : CompletionPrintState
}