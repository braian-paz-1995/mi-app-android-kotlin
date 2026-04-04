package com.ationet.androidterminal.hal

import com.ationet.androidterminal.core.domain.hal.led.HALLed
import com.ationet.androidterminal.core.domain.hal.led.HALLedColor
import com.ationet.androidterminal.core.domain.hal.led.HALLedState
import javax.inject.Inject
import kotlin.time.Duration

class HALLedImpl @Inject constructor(

): HALLed {
    override suspend fun blinkLed(color: HALLedColor, count: Int, delay: Duration): Boolean {
        // NOT SUPPORTED
        return false
    }

    override suspend fun setLedState(color: HALLedColor, state: HALLedState): Boolean {
        // NOT SUPPORTED
        return false
    }
}