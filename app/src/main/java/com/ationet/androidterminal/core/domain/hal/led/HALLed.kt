package com.ationet.androidterminal.core.domain.hal.led

import kotlin.time.Duration

interface HALLed {
    suspend fun blinkLed(color: HALLedColor, count: Int, delay: Duration) : Boolean
    suspend fun setLedState(color: HALLedColor, state: HALLedState) : Boolean
}