package com.ationet.androidterminal.hal.led

import com.ationet.androidterminal.core.domain.hal.led.HALLed
import com.ationet.androidterminal.core.domain.hal.led.HALLedColor
import com.ationet.androidterminal.core.domain.hal.led.HALLedState

// The sdk for the t650p is currently missing, to avoid compilation errors.

import javax.inject.Inject
import kotlin.time.Duration

class HALLedImpl @Inject constructor() : HALLed {

    override suspend fun blinkLed(color: HALLedColor, count: Int, delay: Duration): Boolean {
        // TODO: As I still haven't found the LEDs manager inside the t650p sdk, I leave the function in a
        // TODO state, to avoid compilation errors (this is temporary, as it allows me to continue certain tests
        return true
    }

    override suspend fun setLedState(color: HALLedColor, state: HALLedState): Boolean {
        // TODO: As I still haven't found the LEDs manager inside the t650p sdk, I leave the function in a
        // TODO state, to avoid compilation errors (this is temporary, as it allows me to continue certain tests
        return true
    }
}