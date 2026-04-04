package com.ationet.androidterminal.core.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object LocaleFormatter {

    /**
     * Formats the given local date string from "yyyyMMdd" to a more readable format.
     *
     * @param localDate The date in the format "yyyyMMdd".
     * @return A formatted date string.
     */
    fun formatLocalDate(localDate: String, context: Context): String {
        val inputDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val outputDateFormat =
            SimpleDateFormat(ContextCompat.getContextForLanguage(context).getString(R.string.date), Locale.getDefault())
        return try {
            val date = inputDateFormat.parse(localDate)
            outputDateFormat.format(date)
        } catch (e: Exception) {
            "Invalid date"
        }
    }

    /**
     * Formats the given local time string from "HHmmss" to a more readable format.
     *
     * @param localTime The time in the format "HHmmss".
     * @return A formatted time string.
     */
    fun formatLocalTime(localTime: String, context: Context): String {
        val inputTimeFormat = SimpleDateFormat("HHmmss", Locale.getDefault())
        val outputTimeFormat =
            SimpleDateFormat(ContextCompat.getContextForLanguage(context).getString(R.string.time), Locale.getDefault())
        return try {
            val time = inputTimeFormat.parse(localTime)
            outputTimeFormat.format(time)
        } catch (e: Exception) {
            "Invalid time"
        }
    }

    /**
     * Combines and formats the given local date and time strings.
     *
     * @param localDate The date in the format "yyyyMMdd".
     * @param localTime The time in the format "HHmmss".
     * @param context The application context for fetching localized strings.
     * @return A formatted date-time string.
     */
    fun formatDate(
        localDate: String,
        localTime: String,
        context: Context
    ): String {
        val combinedDateTime = "$localDate$localTime"
        val inputFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val outputFormat = SimpleDateFormat(
            ContextCompat.getContextForLanguage(context).getString(R.string.date) + ContextCompat.getContextForLanguage(context).getString(R.string.time),
            Locale.getDefault()
        )

        return try {
            val date = inputFormat.parse(combinedDateTime)
            outputFormat.format(date)
        } catch (e: Exception) {
            "Invalid date"
        }
    }

    fun formatDate(
        dateTime: String,
        context: Context,
        showSeconds: Boolean = true
    ): String {
        val time = if (showSeconds) {
            R.string.time
        } else {
            R.string.time_hour_minutes
        }
        val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat(
            ContextCompat.getContextForLanguage(context).getString(R.string.date) + " " + ContextCompat.getContextForLanguage(context).getString(time),
            Locale.getDefault()
        )

        return try {
            val date = inputFormat.parse(dateTime)
            outputFormat.format(date)
        } catch (e: Exception) {
            "Invalid date"
        }
    }


    /**
     * Formats date time object into a human readable version.
     *
     * @param dateTime      Date-time to be formatted
     * @param context       Context to retrieve locales
     * @return A formatted date-time string.
     * */
    fun formatDateTime(
        dateTime: LocalDateTime,
        context: Context,
        showSeconds: Boolean = true
    ): String {

        val time = if (showSeconds) {
            R.string.time
        } else {
            R.string.time_hour_minutes
        }

        val pattern = ContextCompat.getContextForLanguage(context).getString(R.string.date) + " " +
                ContextCompat.getContextForLanguage(context).getString(time)

        val format = SimpleDateFormat(pattern, Locale.getDefault())

        val date = Date(
            dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        )

        return format.format(date)
    }

    fun formatDate(dateTime: LocalDateTime, context: Context): String {
        val pattern = ContextCompat.getContextForLanguage(context).getString(R.string.date)
        val format = SimpleDateFormat(pattern, Locale.getDefault())

        val epochMilliseconds = dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        val javaDate = Date(epochMilliseconds)

        return format.format(javaDate)
    }

    fun formatDate(date: LocalDate, context: Context): String {
        return formatDate(date.toLocalDateTime(), context)
    }

    fun formatTime(dateTime: LocalDateTime, context: Context): String {
        val pattern = ContextCompat.getContextForLanguage(context)
            .getString(R.string.time_24_hours)

        val format = SimpleDateFormat(pattern, Locale.getDefault())

        val date = Date(
            dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        )

        return format.format(date)
    }

    private fun getNumberFormatter(
        language: Configuration.LanguageType,
        decimals: Int = 2
    ): NumberFormat {
        val locale = when (language) {
            Configuration.LanguageType.ES -> Locale("es", "ES")
            Configuration.LanguageType.EN -> Locale("en", "US")
        }
        val formatter = NumberFormat.getNumberInstance(locale).apply {
            minimumFractionDigits = decimals
            maximumFractionDigits = decimals
        }
        return formatter
    }

    fun formatNumber(
        number: String,
        decimals: Int = 2,
        language: Configuration.LanguageType
    ): String {
        var formattedNumber = number
        if (language == Configuration.LanguageType.ES) {
            formattedNumber = replaceSubstring(number, ",", ".")
        }

        return try {
            val parsedNumber = formattedNumber.toDouble()
            val formatter = getNumberFormatter(language, decimals)

            formatter.format(parsedNumber)
        } catch (e: NumberFormatException) {
            "Invalid number"
        }
    }

    fun formatNumber(
        number: String?,
        decimals: Int = 2,
        context: Context
    ): String {
        val pattern = buildString {
            append("#,##0.")
            repeat(decimals) {
                append("0")
            }
        }

        val customFormat = DecimalFormat(pattern, DecimalFormatSymbols().apply {
            groupingSeparator = ContextCompat.getContextForLanguage(context).getString(R.string.grouping_separator).first()
            decimalSeparator = ContextCompat.getContextForLanguage(context).getString(R.string.decimal_separator).first()
        })

        return customFormat.format(number?.toDoubleOrNull() ?: 0.0)
    }

    fun filterDot(input: String): String {
        return replaceSubstring(input, ".", "")
    }

    fun replaceSubstring(
        originalText: String,
        targetSubstring: String,
        replacement: String
    ): String {
        return originalText.replace(targetSubstring, replacement)
    }


    fun filterEmptyOrSpaces(input: String): String {
        return if (input.trim().isEmpty()) "" else input.replace(Regex("\\s{2,}"), " ")
    }

    fun removeTrailingAndLeadingSpace(input: String): String {
        return input.trim()
    }

    fun formatMoney(
        amount: BigDecimal,
        currency: String?,
        context: Context,
        decimals: Int = 2
    ): String {
        val pattern = buildString {
            append("#,##0")
            if (decimals > 0) {
                append(".")
                repeat(decimals) { append("0") }
            }
        }

        val symbols = DecimalFormatSymbols().apply {
            groupingSeparator = ContextCompat.getContextForLanguage(context)
                .getString(R.string.grouping_separator).first()
            decimalSeparator = ContextCompat.getContextForLanguage(context)
                .getString(R.string.decimal_separator).first()
        }

        val df = DecimalFormat(pattern, symbols).apply {
            minimumFractionDigits = decimals
            maximumFractionDigits = decimals
            isGroupingUsed = true
        }

        val body = df.format(amount)
        return if (!currency.isNullOrBlank()) "$currency $body" else body
    }


    fun formatMoney(
        amount: Double,
        currency: String?,
        context: Context,
        decimals: Int = 2
    ): String = formatMoney(BigDecimal.valueOf(amount), currency, context, decimals)
}