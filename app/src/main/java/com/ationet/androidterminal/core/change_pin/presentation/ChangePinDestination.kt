package com.ationet.androidterminal.core.change_pin.presentation

import kotlinx.serialization.Serializable

sealed interface ChangePinDestination {
    @Serializable
    data object Identification : ChangePinDestination

    @Serializable
    data class PinPrompt(
        val identification: String
    ) : ChangePinDestination

    @Serializable
    data object ChangePinSummary : ChangePinDestination

    @Serializable
    data object Print : ChangePinDestination
    @Serializable
    data object Ticket : ChangePinDestination
}