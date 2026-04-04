package com.ationet.androidterminal.core.data.local.room.entity.fleet.completion

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fusion_completion",
    foreignKeys = [
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["completion_id"],
            entity = CompletionEntity::class,
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("completion_id")]
)
data class FusionCompletionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "sale_id")
    val saleId: Int?,
    @ColumnInfo(name = "completion_id")
    val completionId: Int,
)

