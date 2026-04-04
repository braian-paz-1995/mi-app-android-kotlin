package com.ationet.androidterminal.core.data.local.room.entity.receipt

import androidx.room.Embedded
import androidx.room.Relation

data class ReceiptWithModifiers(
    @Embedded
    val receipt: ReceiptEntity,
    @Relation(
        entity = ReceiptProductModifierEntity::class,
        entityColumn = "receipt_id",
        parentColumn = "id"
    )
    val modifiers: List<ReceiptProductModifierEntity>
)
