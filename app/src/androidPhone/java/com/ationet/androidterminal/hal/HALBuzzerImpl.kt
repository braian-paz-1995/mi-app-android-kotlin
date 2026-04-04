package com.ationet.androidterminal.hal

import com.ationet.androidterminal.core.domain.hal.HALBuzzer
import javax.inject.Inject
import kotlin.time.Duration

class HALBuzzerImpl @Inject constructor() : HALBuzzer {

    override fun beep(duration: Duration) {
        // No hace nada
    }
}