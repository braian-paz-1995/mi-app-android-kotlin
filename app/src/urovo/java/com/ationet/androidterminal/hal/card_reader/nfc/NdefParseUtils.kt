package com.ationet.androidterminal.hal.card_reader.nfc

import kotlin.experimental.and

fun parseTextRecord(record: List<Byte>): String {
    if(record.isEmpty()) {
        throw IllegalArgumentException("Invalid record")
    }

    val statusByte = record[0]

    /* Bit 7:
    *  - 0 : UTF8
    *  - 1 : UTF16-BE
    * */
    val encoding = if (statusByte and 0x80.toByte() == 0x80.toByte()) {
        Charsets.UTF_16BE
    } else {
        Charsets.UTF_8
    }
    /*
    * Bits 5 .. 0:
    *   IANA language code length
    * */
    val codeLength = (statusByte and 0x3F.toByte()).toInt()

    val startIndex = minOf(1 + codeLength, record.size)

    val endIndex = if (record.last() == 0xFE.toByte()) {
        record.lastIndex - 1
    } else {
        record.lastIndex
    }

    return record.subList(
        fromIndex = startIndex,
        toIndex = minOf(endIndex + 1, record.size) // Because toIndex doesn't include the last byte.
    ).toByteArray().toString(encoding)
}