package com.ationet.androidterminal.core.domain.hal.card_reader

data class NdefRecord(
    val id: List<Byte>,
    val typeNameFormat: NdefTypeNameFormat,
    val type : List<Byte>,
    val payload: List<Byte>
)