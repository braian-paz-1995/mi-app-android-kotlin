package com.ationet.androidterminal.core.domain.hal.led

sealed interface HALLedState {
    data object On : HALLedState
    data object Off : HALLedState
}