package com.ationet.androidterminal.core.domain.hal.card_reader

data class NfcCard(
    val uid: List<Byte>,
    val message: NdefMessage?,
)

interface NfcCardReader {
    fun readCard(reader: NfcReader): NfcCard?
}