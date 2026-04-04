package com.ationet.androidterminal.standalone.completion.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object CompletionNavTypes {
    val double = object : NavType<Double>(
        isNullableAllowed = false
    ) {
        override fun parseValue(value: String): Double {
            return Uri.decode(value).toDouble()
        }

        override fun serializeAsValue(value: Double): String {
            return Uri.encode(value.toString())
        }

        override fun get(bundle: Bundle, key: String): Double {
            return bundle.getDouble(key)
        }

        override fun put(bundle: Bundle, key: String, value: Double) {
            bundle.putDouble(key, value)
        }
    }
    val preAuthorizationData = object : NavType<PreAuthorizationData>(
        isNullableAllowed = false
    ) {
        override fun parseValue(value: String): PreAuthorizationData {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun serializeAsValue(value: PreAuthorizationData): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun get(bundle: Bundle, key: String): PreAuthorizationData? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun put(bundle: Bundle, key: String, value: PreAuthorizationData) {
            bundle.putString(key, Json.encodeToString(value))
        }
    }

    val productData = object : NavType<PreAuthorizationProductData>(
        isNullableAllowed = false
    ) {
        override fun parseValue(value: String): PreAuthorizationProductData {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun serializeAsValue(value: PreAuthorizationProductData): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun get(bundle: Bundle, key: String): PreAuthorizationProductData? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun put(bundle: Bundle, key: String, value: PreAuthorizationProductData) {
            bundle.putString(key, Json.encodeToString(value))
        }
    }
}