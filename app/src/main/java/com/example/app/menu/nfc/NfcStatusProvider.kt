package com.example.app.menu.nfc

import android.content.Context
import android.nfc.NfcAdapter

class NfcStatusProvider(private val context: Context) {
    fun isNfcAvailable(): Boolean = NfcAdapter.getDefaultAdapter(context) != null
}
