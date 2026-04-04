package com.ationet.androidterminal.hal

import com.ationet.androidterminal.core.domain.hal.HALBuzzer
import com.newland.sdk.module.buzzer.BuzzerModule
import javax.inject.Inject
import kotlin.time.Duration

class HALBuzzerImpl @Inject constructor(
    private val buzzerModule: BuzzerModule
): HALBuzzer {
    override fun beep(duration: Duration) {
        buzzerModule.play(1, duration.inWholeSeconds.toInt(), duration.inWholeSeconds.toInt())
    }
}