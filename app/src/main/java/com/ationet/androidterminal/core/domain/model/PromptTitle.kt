package com.ationet.androidterminal.core.domain.model

import androidx.annotation.StringRes
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.data.remote.ationet.model.PromptKey

enum class PromptTitle(val key: String, @StringRes val title: Int, val maxLength: Int) {
    AtionetVisionVehicleId(PromptKey.AtionetVisionVehicleId, R.string.AtionetVisionVehicleId, 26),
    Odometer(PromptKey.Odometer, R.string.odometer, 9),
    EngineHours(PromptKey.EngineHours, R.string.engine_hours, 9),
    DriverId(PromptKey.DriverId, R.string.driver_identification, 12),
    VehicleId(PromptKey.VehicleId, R.string.vehicle_identification, 12),
    Miscellaneous(PromptKey.Miscellaneous, R.string.miscellaneous, 10),
    TrailerNumber(PromptKey.TrailerNumber, R.string.trailer, 10),
    TruckUnitNumber(PromptKey.TruckUnitNumber, R.string.truck_unit, 10),
    PrimaryPin(PromptKey.PrimaryPin, R.string.primary_pin, 8),
    SecondaryTrack(PromptKey.SecondaryTrack, R.string.secondary_track, 19),
    SecondaryPin(PromptKey.SecondaryPin, R.string.secondary_pin, 8),
    AttendantId(PromptKey.AttendantId, R.string.attendant_identification, 12),

}