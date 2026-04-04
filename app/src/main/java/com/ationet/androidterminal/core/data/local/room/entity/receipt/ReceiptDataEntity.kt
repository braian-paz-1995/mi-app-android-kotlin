package com.ationet.androidterminal.core.data.local.room.entity.receipt

import androidx.room.ColumnInfo

data class ReceiptDataEntity(
    @ColumnInfo("company_name")
    val companyName: String?,
    @ColumnInfo("customer_pan")
    val customerPan: String?,
    @ColumnInfo("customer_plate")
    val customerPlate: String?,
    @ColumnInfo("customer_driver_name")
    val customerDriverName: String?,
    @ColumnInfo("customer_driver_id")
    val customerDriverId: String?,
    @ColumnInfo("customer_vehicle_code")
    val customerVehicleCode: String?,
)