package com.ationet.androidterminal.core.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateTimeUtils {

    fun getActualTime(): String {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("HHmmss", Locale.getDefault())
        return format.format(calendar.time)
    }

    fun getActualDate(): String {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return format.format(calendar.time)
    }
}
