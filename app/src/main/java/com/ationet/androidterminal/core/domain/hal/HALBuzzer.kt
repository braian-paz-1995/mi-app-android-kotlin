package com.ationet.androidterminal.core.domain.hal

import kotlin.time.Duration

interface HALBuzzer {
    fun beep(duration: Duration)
}