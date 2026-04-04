package com.ationet.androidterminal.standalone.receipts.presentation.print

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ationet.androidterminal.core.domain.util.PrinterErrorCodes
import com.ationet.androidterminal.standalone.receipts.domain.use_cases.PrintReceiptCopyUseCase
import com.ationet.androidterminal.standalone.receipts.domain.use_cases.PrintReceiptResult
import com.ationet.androidterminal.standalone.receipts.presentation.ReceiptsDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class ReceiptCopyPrintViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val printReceiptCopyUseCase: PrintReceiptCopyUseCase,
): ViewModel() {
    private val arguments = savedStateHandle.toRoute<ReceiptsDestination.PrintingCopy>()

    val state: StateFlow<ReceiptPrintState> get() = _state.asStateFlow()
    private val _state: MutableStateFlow<ReceiptPrintState> =
        MutableStateFlow(ReceiptPrintState.PrintingCopy)

    private var printingMutex: Mutex = Mutex()

    init {
        viewModelScope.launch {
            printCopy()
        }
    }

    private suspend fun printCopy() {
        if (printingMutex.isLocked) {
            return
        }

        printingMutex.withLock {
            _state.update { ReceiptPrintState.PrintingCopy }

            val result = try {
                printReceiptCopyUseCase.invoke(arguments.receiptId)
            } catch (e: Throwable) {
                currentCoroutineContext().ensureActive()

                _state.update {
                    ReceiptPrintState.PrintingError(
                        errorCode = PrinterErrorCodes.ERROR
                    )
                }

                return
            }

            when (result) {
                is PrintReceiptResult.Error -> {
                    _state.update {
                        ReceiptPrintState.PrintingError(
                            errorCode = result.code,
                        )
                    }
                }
                PrintReceiptResult.Ok ->{
                    _state.update { ReceiptPrintState.PrintingCopyDone }
                }
                PrintReceiptResult.OutOfPaper -> {
                    _state.update { ReceiptPrintState.PrintingOutOfPaper }
                }
            }
        }
    }

    fun retryPrinting() {
        viewModelScope.launch {
            printCopy()
        }
    }
}

sealed interface ReceiptPrintState {
    data object PrintingCopy : ReceiptPrintState
    data object PrintingCopyDone : ReceiptPrintState
    data object PrintingOutOfPaper: ReceiptPrintState

    data class PrintingError(
        val errorCode: String,
    ) : ReceiptPrintState
}