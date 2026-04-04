package com.ationet.androidterminal.core.change_pin.domain.use_case

import com.ationet.androidterminal.core.change_pin.domain.error.ChangePinError
import com.ationet.androidterminal.core.change_pin.domain.util.ValidatePinResult
import javax.inject.Inject

class ValidatePin @Inject constructor() {
    operator fun invoke(
        currentPin: String?,
        confirmPin: String?,
        reConfirmPin: String?
    ): ValidatePinResult {

        if (currentPin != null && currentPin.isEmpty() || confirmPin != null && confirmPin.isEmpty()) {
            return ValidatePinResult.Failure(ChangePinError.PinEmpty)
        }

        if (currentPin != null && confirmPin != null && currentPin == confirmPin) {
            return  ValidatePinResult.Failure(ChangePinError.PinSameAsPrevious)
        }

        if (currentPin != null && currentPin.isEmpty() || confirmPin != null && confirmPin.length < 4) {
            return ValidatePinResult.Failure(ChangePinError.PinTooShort)
        }

        if (currentPin != null && currentPin.isEmpty() || confirmPin != null && confirmPin.length > 8) {
            return ValidatePinResult.Failure(ChangePinError.PinTooLong)
        }

        if (reConfirmPin != null && reConfirmPin != confirmPin) {
            return ValidatePinResult.Failure(ChangePinError.PinsDoNotMatch)
        }

        if (currentPin != null && !currentPin.all { it.isDigit() }
            || confirmPin != null && !confirmPin.all { it.isDigit() }
            || reConfirmPin != null && !reConfirmPin.all { it.isDigit() }
        ) {
            return ValidatePinResult.Failure(ChangePinError.PinMustContainOnlyNumbers)
        }

        return ValidatePinResult.Success
    }
}