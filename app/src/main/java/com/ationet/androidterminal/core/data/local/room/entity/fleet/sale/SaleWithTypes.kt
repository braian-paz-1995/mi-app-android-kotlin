package com.ationet.androidterminal.core.data.local.room.entity.fleet.sale

import androidx.room.Embedded
import androidx.room.Relation

data class SaleWithFusion(
    @Embedded val sale: SaleEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "sale_id"
    )
    val fusion: FusionSaleEntity
)