package com.ationet.androidterminal.core.data.local.room.entity.task.rechargeCC

import androidx.room.Embedded
import androidx.room.Relation

data class RechargeCCWithFusion(
    @Embedded val rechargecc: RechargeCCEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "rechargecc_id"
    )
    val fusion: FusionRechargeCCEntity
)