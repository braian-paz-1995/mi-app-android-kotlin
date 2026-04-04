package com.ationet.androidterminal.core.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

/**
 * Gets the amount formatted for requests
 * */
val Double?.formattedAmount: String?
    get() = if (this != null) {

        val customFormat = DecimalFormat("0.##", DecimalFormatSymbols().apply {
            decimalSeparator = '.'
        })

        customFormat.format(this)
    }
    else null

/**
 * Gets the volume formatted for requests
 * */
val Double?.formattedVolume: String?
    get() = if (this != null) {

        val customFormat = DecimalFormat("0.###", DecimalFormatSymbols().apply {
            decimalSeparator = '.'
        })

        customFormat.format(this)
    }
    else null

/**
 * Gets the unit price formatted
 * */
val Double?.formattedUnitPrice: String?
    get() = this?.toString()

fun Double?.limitDigits(limit: Int = 4) : String {
    if(this == null) {
        return ""
    }

    val parts = this.toString().split('.')
    val intPart = parts[0]

    return if (intPart.length >= limit) {
        intPart.take(limit) // Cut off if integer part is too long
    } else {
        val remainingDigits = limit - intPart.length
        val decimalPart = if (parts.size > 1) parts[1] else ""
        val paddedDecimal = (decimalPart + "0".repeat(remainingDigits)).take(remainingDigits)

        "$intPart.$paddedDecimal"
    }
}