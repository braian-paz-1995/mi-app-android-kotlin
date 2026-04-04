package com.ationet.androidterminal.core.domain.hal.card_reader

interface NfcReader {
    fun getUid() : ByteArray?
    fun read(offset: Int, quantity: Int = 1): ByteArray?
}