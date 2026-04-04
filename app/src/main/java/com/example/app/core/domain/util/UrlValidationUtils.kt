package com.ationet.androidterminal.core.domain.util

import android.util.Patterns

/**
 *  Validates that the given URL is a well formed URL string.
 *
 *  */
fun validateUrlString(url: String): Boolean {
    return Patterns.WEB_URL.matcher(url).matches()
}

/**
 * Validates that the given port string is a well formed port string.
 * */
fun validatePortString(port: String): Boolean {
    val portInt = port.toIntOrNull()
    if(portInt == null) {
        return false
    }

    return validatePort(portInt)
}

/**
 * Validates that the given port is a valid port.
 * */
fun validatePort(port: Int): Boolean {
    return port in 0 .. 65535
}