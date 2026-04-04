package com.ationet.androidterminal.core.util

fun String.mask(mask: String = "*", visibleDigits: Int = 4): String {

    if (this.length <= visibleDigits) return this

    return if (this.length > 19) {
        mask.repeat(15) + this.takeLast(visibleDigits)
    } else {
        mask.repeat(this.length - visibleDigits) + this.takeLast(visibleDigits)
    }
}