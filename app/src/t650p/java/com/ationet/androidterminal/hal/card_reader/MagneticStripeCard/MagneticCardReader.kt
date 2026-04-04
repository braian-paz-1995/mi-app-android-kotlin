package com.ationet.androidterminal.hal.card_reader.MagneticStripeCard

import com.ationet.androidterminal.core.domain.hal.HALBuzzer
import com.ationet.androidterminal.hal.card_reader.EmvUtils
import com.ationet.androidterminal.hal.card_reader.config.Crypto
import com.ationet.androidterminal.hal.card_reader.config.EmvContactConfig
import com.verifone.payment_sdk.SdiManager
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class MagneticCardReader @Inject constructor(
    private val sdiManager: SdiManager,
    private val ctConfigData: EmvContactConfig,
    private val buzzer: HALBuzzer
) {

    private val crypto = Crypto(sdiManager)

    fun read(): String {
        crypto.getSensitiveEncryptedData(ctConfigData.sensitiveTags)
        val track2Info = EmvUtils.retrieveTagsUsingApi(sdiManager, ctConfigData.fetchTags)
        playBeep();
        return track2Info
    }
    fun playBeep(){
        buzzer.beep(1.seconds)
    }
}
