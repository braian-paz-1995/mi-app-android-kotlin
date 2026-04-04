package com.ationet.androidterminal.core.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration

object LocalDateTimeUtils {
    val LocalTimeFormat = LocalTime.Format {
        hour()
        minute()
        second()
    }

    val LocalDateFormat = LocalDate.Format {
        year()
        monthNumber()
        dayOfMonth()
    }

    val LocalDateTimeFormat = LocalDateTime.Format {
        year()
        monthNumber()
        dayOfMonth()
        hour()
        minute()
        second()
    }

    fun convertToDateTimeFormat(pattern: String) =
        LocalDateTime.Format { byUnicodePattern(pattern) }
}

fun LocalDateTime.toUTC() = toInstant(TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.UTC)
fun LocalDate.toLocalDateTime() = atStartOfDayIn(TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.UTC)
fun LocalTime.toLocalDateTime() = LocalDateTime(LocalDate(0, 1, 1), this).toUTC()

operator fun LocalDateTime.minus(duration: Duration): LocalDateTime {
    val timeZone = TimeZone.currentSystemDefault()
    return toInstant(timeZone)
        .minus(duration)
        .toLocalDateTime(timeZone)
}