package com.ationet.androidterminal.core.data.remote.ationet.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DealerData(
    @SerialName("AttendantCode") val attendantCode: String
)