package com.ationet.androidterminal.core.domain.hal.card_reader

enum class NdefTypeNameFormat(val value: Byte) {
    Empty(0x00.toByte()),
    WellKnown(0x01.toByte()),
    MimeMedia(0x02.toByte()),
    AbsoluteUri(0x03.toByte()),
    ExternalType(0x04.toByte()),
    Unknown(0x05.toByte()),
    Unchanged(0x06.toByte()),
    Reserved(0x07.toByte()),
}