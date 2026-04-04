package com.ationet.androidterminal.hal

import android.media.ToneGenerator
import com.ationet.androidterminal.core.domain.hal.HALBuzzer
import javax.inject.Inject
import kotlin.time.Duration

class HALBuzzerImpl @Inject constructor(
    private val toneGenerator: ToneGenerator,
) : HALBuzzer {
    override fun beep(duration: Duration) {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ONE_MIN_BEEP, duration.inWholeMilliseconds.toInt())
    }
}