package com.ationet.androidterminal.core.domain.util

import android.os.Build

object OSUtils {
    val operatingSystem : String get() = getOs()

    private fun getOs() : String {
        return "Android OS " + Build.VERSION.RELEASE
    }
}