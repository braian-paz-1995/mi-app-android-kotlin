package com.example.app.menu.nfc

import android.content.Context

object NfcPayloadStore {
    private const val PREF_NAME = "nfc_prefs"
    private const val KEY_PAYLOAD = "nfc_payload"
    private const val DEFAULT_PAYLOAD = "MI_APP_NFC_EMISOR"

    fun getPayload(context: Context): String =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_PAYLOAD, DEFAULT_PAYLOAD)
            ?.take(MAX_CHARS)
            .orEmpty()
            .ifBlank { DEFAULT_PAYLOAD }

    fun setPayload(context: Context, payload: String) {
        val value = payload.trim().take(MAX_CHARS).ifBlank { DEFAULT_PAYLOAD }
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PAYLOAD, value)
            .apply()
    }

    private const val MAX_CHARS = 200
}
