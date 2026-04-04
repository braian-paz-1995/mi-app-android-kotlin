package com.ationet.androidterminal.core.domain.hal.printer

data class ImageFormat(
    val width : Int? = null,
    val height: Int? = null,
    val alignment: Alignment = Alignment.Center,
)
