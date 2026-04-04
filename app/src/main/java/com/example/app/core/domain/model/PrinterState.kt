package com.ationet.androidterminal.core.domain.model

sealed interface PrinterState {
    data object Printing : PrinterState
    data object PrintingCopy : PrinterState
    data object Done : PrinterState
}