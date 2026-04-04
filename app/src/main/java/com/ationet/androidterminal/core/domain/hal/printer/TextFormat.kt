package com.ationet.androidterminal.core.domain.hal.printer

data class TextFormat(
    val textSize: TextSize? = null,
    val alignment: Alignment? = null,
    val linefeed: Boolean? = null,
    val underline: Boolean? = null,
    val textWeight: TextWeight? = null,
)