package com.ationet.androidterminal.core.domain.model.preauthorization

import com.ationet.androidterminal.core.data.local.room.entity.fleet.pre_authorization.CustomerData

data class CustomerData(
    val odometer: String? = null,
    val engineHours: String? = null,
    val driverId: String? = null,
    val vehicleId: String? = null,
    val AtionetVisionVehicleId: String? = null,
    val miscellaneous: String? = null,
    val trailerNumber: String? = null,
    val truckUnitNumber: String? = null,
    val attendantId: String? = null,
    val primaryPin: String? = null,
    val secondaryPin: String? = null,
) {
    fun toEntity(): CustomerData = CustomerData(
        odometer = this.odometer,
        engineHours = this.engineHours,
        driverId = this.driverId,
        vehicleId = this.vehicleId,
        AtionetVisionVehicleId = this.AtionetVisionVehicleId,
        miscellaneous = this.miscellaneous,
        trailerNumber = this.trailerNumber,
        truckUnitNumber = this.truckUnitNumber,
        attendantId = this.attendantId,
        primaryPin = this.primaryPin,
        secondaryPin = this.secondaryPin
    )
}