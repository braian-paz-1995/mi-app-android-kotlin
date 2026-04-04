package com.ationet.androidterminal.core.domain.hal.led
/**
 * LED color abstraction
 * */
sealed interface HALLedColor {
    data object Red : HALLedColor
    data object Green : HALLedColor
    data object Blue: HALLedColor
    data object Yellow : HALLedColor
}