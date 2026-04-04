package com.ationet.androidterminal.hal.led

import com.ationet.androidterminal.core.domain.hal.led.HALLed
import com.ationet.androidterminal.core.domain.hal.led.HALLedColor
import com.ationet.androidterminal.core.domain.hal.led.HALLedState
import com.newland.sdk.module.light.IndicatorLightModule
import com.newland.sdk.module.light.LightColor
import com.newland.sdk.module.light.LightState
import javax.inject.Inject
import kotlin.time.Duration

class HALLedImpl @Inject constructor(
    private val indicatorLightModule: IndicatorLightModule,
) : HALLed {
    override suspend fun blinkLed(color: HALLedColor, count: Int, delay: Duration): Boolean {
        return indicatorLightModule.blinkLight(
            arrayOf(color.toNewlandColor()),
            count,
            delay.inWholeMilliseconds.toInt()
        )
    }

    override suspend fun setLedState(color: HALLedColor, state: HALLedState): Boolean {
        return indicatorLightModule.operateLight(arrayOf(color.toNewlandColor()), state.toNewlandState())
    }

    private fun HALLedColor.toNewlandColor() : LightColor {
        return when (this) {
            HALLedColor.Blue -> LightColor.BLUE
            HALLedColor.Green -> LightColor.GREEN
            HALLedColor.Red -> LightColor.RED
            HALLedColor.Yellow -> LightColor.YELLOW
        }
    }

    private fun HALLedState.toNewlandState() : LightState {
        return when(this) {
            HALLedState.Off -> LightState.TURNOFF
            HALLedState.On -> LightState.TURNON
        }
    }
}