package com.ationet.androidterminal.core.data.local.room.entity.task.reverseCC

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fusion_reversecc",
    foreignKeys = [
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["reversecc_id"],
            entity = ReverseCCEntity::class,
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("reversecc_id")]
)
data class FusionReverseCCEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "fusion_reversecc_id")
    val fusionReverseCCId: Int,
    @ColumnInfo(name = "reversecc_id")
    val reverseCCId: Int,
)

