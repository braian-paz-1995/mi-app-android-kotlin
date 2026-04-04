package com.ationet.androidterminal.core.data.local.room.entity.fleet.pre_authorization

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(
    tableName = "pre_authorization",
    indices = [
        Index(value = ["authorization_code", "batch_id", "local_datetime"]),
        Index(value = ["controller_type"])
    ]
)
data class PreAuthorizationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime,
    @Embedded val identificationData: IdentificationData,
    @Embedded val authorizationData: AuthorizationData,
    @Embedded val originalData: OriginalData,
    @Embedded val customerData: CustomerData,
    @ColumnInfo(name = "batch_id") val batchId: Int,
    @ColumnInfo(name = "controller_type", defaultValue = "STAND_ALONE") val controllerType: String
)

data class CustomerData(
    @ColumnInfo("odometer") val odometer: String?,
    @ColumnInfo("engine_hours") val engineHours: String?,
    @ColumnInfo("driver_id") val driverId: String?,
    @ColumnInfo("vehicle_id") val vehicleId: String?,
    @ColumnInfo("AtionetVisionVehicleId") val AtionetVisionVehicleId: String?,
    @ColumnInfo("miscellaneous") val miscellaneous: String?,
    @ColumnInfo("trailer_number") val trailerNumber: String?,
    @ColumnInfo("truck_unit_number") val truckUnitNumber: String?,
    @ColumnInfo("attendant_id") val attendantId: String?,
    @ColumnInfo("primary_pin") val primaryPin: String?,
    @ColumnInfo("secondary_pin") val secondaryPin: String?,
)