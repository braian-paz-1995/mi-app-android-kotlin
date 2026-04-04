package com.ationet.androidterminal.core.data.local.room.entity.fleet.completion

import androidx.room.Embedded
import androidx.room.Relation

data class CompletionWithFusion(
    @Embedded val completion: CompletionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "completion_id"
    )
    val fusion: FusionCompletionEntity
)