package com.ationet.androidterminal.core.data.local.room.entity.task.reverseCC

import androidx.room.Embedded
import androidx.room.Relation

data class ReverseCCWithFusion(
    @Embedded val reversecc: ReverseCCEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "reversecc_id"
    )
    val fusion: FusionReverseCCEntity
)