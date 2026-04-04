package com.ationet.androidterminal.core.util

import com.ationet.androidterminal.core.data.remote.ationet.model.IdentityData

fun parserIdentityCardReader(data: String?): String? {
    if (data.isNullOrBlank()) return data

    if (!data.contains("@")) {
        return if (data.all { it.isDigit() }) data else null
    }

    val parts = data.split("@")

    val dni = parts.firstOrNull { part ->
        part.all { it.isDigit() } && part.length in 7..8
    }

    return dni ?: data
}
fun extractIdentityData(data: String?): IdentityData? {
    if (data.isNullOrEmpty()) return null
    if (!data.contains("@")) return null

    val parts = data.split("@")
    return when {

        // DNI NUEVO
        parts.size >= 8 && parts[4].all { it.isDigit() } -> {
            IdentityData(
                entryMethod = "S",
                procedureNumber = parts[0],
                lastName = parts[1],
                firstName = parts[2],
                sex = parts[3],
                identityNumber = parts[4],
                copy = parts[5],
                birthDate = parts[6],
                issueDate = parts[7]
            )
        }
        // DNI VIEJO
        parts.size >= 11 && parts[10].all { it.isDigit() } -> {
            IdentityData(
                entryMethod = "S",
                procedureNumber = parts[1],
                lastName = parts[5],
                firstName = parts[4],
                sex = parts[8],
                identityNumber = parts[10],
                copy = "",
                birthDate = parts[7],
                issueDate = parts[9]
            )
        }

        else -> null
    }
}