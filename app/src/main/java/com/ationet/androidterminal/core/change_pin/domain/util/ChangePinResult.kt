package com.ationet.androidterminal.core.change_pin.domain.util

import com.ationet.androidterminal.core.change_pin.domain.error.ChangePinError

sealed interface ValidatePinResult {
    object Success : ValidatePinResult
    data class Failure(val error: ChangePinError) : ValidatePinResult
}