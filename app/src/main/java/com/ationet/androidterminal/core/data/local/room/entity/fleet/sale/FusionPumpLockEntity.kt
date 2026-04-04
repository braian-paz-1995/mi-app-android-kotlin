package com.ationet.androidterminal.core.data.local.room.entity.fleet.sale

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ationet.androidterminal.fusion.core.domain.model.PumpLockStatus

@Entity(
    tableName = "fusion_pump_lock",
    indices = [
        Index("fusion_sale_id"),
        Index("terminal_id")
    ]
)
data class FusionPumpLockEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "fusion_sale_id")
    val fusionSaleId: Int,

    @ColumnInfo(name = "terminal_id")
    val terminalId: String,

    @ColumnInfo(name = "status")
    val status: PumpLockStatus,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long // System.currentTimeMillis()
)