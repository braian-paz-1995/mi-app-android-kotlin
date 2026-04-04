package com.ationet.androidterminal.core.change_pin.domain.error

import com.ationet.androidterminal.R

sealed class ChangePinError(val message: Int) {
    object PinEmpty : ChangePinError(R.string.pin_is_empty)
    object PinsDoNotMatch : ChangePinError(R.string.pins_must_match)
    object PinTooShort : ChangePinError(R.string.pin_is_too_short)
    object PinTooLong : ChangePinError(R.string.pin_is_too_long)
    object PinMustContainOnlyNumbers : ChangePinError(R.string.pin_must_cointain_only_numbers)
    object PinSameAsPrevious : ChangePinError(R.string.pin_same_as_previous)
}