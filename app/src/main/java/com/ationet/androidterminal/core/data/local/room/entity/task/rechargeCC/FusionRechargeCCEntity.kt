package com.ationet.androidterminal.core.data.local.room.entity.task.rechargeCC

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fusion_rechargecc",
    foreignKeys = [
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["rechargecc_id"],
            entity = RechargeCCEntity::class,
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("rechargecc_id")]
)
data class FusionRechargeCCEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "fusion_rechargecc_id")
    val fusionRechargeCCId: Int,
    @ColumnInfo(name = "rechargecc_id")
    val rechargeCCId: Int,
)

