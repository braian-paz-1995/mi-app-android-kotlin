package com.ationet.androidterminal.hal.card_reader.nfc

data class Tag(
    val id: List<Byte>,
    val content: String,
    val technology: NfcTechnologyType,
)