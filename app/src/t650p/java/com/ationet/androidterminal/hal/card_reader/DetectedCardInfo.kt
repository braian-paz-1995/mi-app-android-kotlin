package com.ationet.androidterminal.hal.card_reader

import com.ationet.androidterminal.core.domain.hal.card_reader.CardReader

sealed class DetectedCardInfo(val reader: CardReader) {
    object MagneticStripeCard : DetectedCardInfo(CardReader.MagneticStripeCard)
    object RfCard : DetectedCardInfo(CardReader.RfCard)
    object SmartCard : DetectedCardInfo(CardReader.SmartCard)
    data class NfcCard(val data: String) : DetectedCardInfo(CardReader.SmartCard)
}
