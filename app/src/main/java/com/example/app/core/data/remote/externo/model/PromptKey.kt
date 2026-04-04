package com.ationet.androidterminal.core.data.remote.ationet.model

object PromptKey {
    const val Odometer: String = "Odometer"
    const val EngineHours: String = "EngineHours"
    const val DriverId: String = "DriverId"
    const val VehicleId: String = "VehicleId"
    const val AtionetVisionVehicleId: String = "AtionetVisionVehicleId"
    const val Miscellaneous: String = "Miscellaneous"
    const val TrailerNumber: String = "TrailerNumber"
    const val TruckUnitNumber: String = "TruckUnitNumber"
    const val AttendantId: String = "AttendantId"

    /**
     * Primary pin. Not reported as prompt,
     * but instead as a identifier pin.
     * */
    const val PrimaryPin: String = "PrimaryPin"

    /**
     * Secondary track. Not reported as prompt,
     * but instead as a second identifier.
     * */
    const val SecondaryTrack: String = "SecondaryTrack"

    /**
     * Secondary pin. Not reported as prompt,
     * but instead as a second identifier pin.
     * */
    const val SecondaryPin: String = "SecondaryPin"
    const val NewPin: String = "NewPin"
    const val ConfirmationPin: String = "ConfirmationPin"
}