package com.atio.log.domain.model

import com.atio.log.util.KByte

data class LogcatConfiguration(
    val packageName: String = "",
    val enabledDebuggable: Boolean = false,
    val enabledWarnings: Boolean = false,
    val enabledErrors: Boolean = false,
    val enabledInfo: Boolean = false,
    val fileSize: Int = 30.KByte,
    val fileQuantity: Int = 3,
    val timesDelay: Int = 5,
)