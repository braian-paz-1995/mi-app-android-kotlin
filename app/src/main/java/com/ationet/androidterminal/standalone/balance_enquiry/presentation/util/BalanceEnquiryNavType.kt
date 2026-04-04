package com.ationet.androidterminal.standalone.balance_enquiry.presentation.util

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.ationet.androidterminal.standalone.balance_enquiry.presentation.summary.SummaryData
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object BalanceEnquiryNavType {
    val Double = object : NavType<Double>(
        isNullableAllowed = false
    ) {
        override fun get(bundle: Bundle, key: String): Double? {
            return bundle.getDouble(key)
        }

        override fun parseValue(value: String): Double {
            return value.toDouble()
        }

        override fun put(bundle: Bundle, key: String, value: Double) {
            bundle.putDouble(key, value)
        }

        override fun serializeAsValue(value: Double): String {
            return Uri.encode(Json.encodeToString(value))
        }
    }
    val SummaryData = object : NavType<SummaryData>(
        isNullableAllowed = false
    ) {
        override fun get(bundle: Bundle, key: String): SummaryData? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): SummaryData {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun put(bundle: Bundle, key: String, value: SummaryData) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun serializeAsValue(value: SummaryData): String {
            return Uri.encode(Json.encodeToString(value))
        }
    }

    val LocalDateTime = object : NavType<LocalDateTime>(
        isNullableAllowed = false
    ) {
        override fun get(bundle: Bundle, key: String): LocalDateTime? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): LocalDateTime {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun put(bundle: Bundle, key: String, value: LocalDateTime) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun serializeAsValue(value: LocalDateTime): String {
            return Uri.encode(Json.encodeToString(value))
        }
    }
}