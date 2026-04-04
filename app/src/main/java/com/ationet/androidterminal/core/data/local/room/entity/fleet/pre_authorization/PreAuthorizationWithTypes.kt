package com.ationet.androidterminal.core.data.local.room.entity.fleet.pre_authorization

import androidx.room.Embedded
import androidx.room.Relation

data class PreAuthorizationWithFusion(
    @Embedded val preAuthorization: PreAuthorizationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "pre_authorization_id"
    )
    val fusion: FusionPreAuthorizationEntity
)

data class PreAuthorizationWithStandalone(
    @Embedded val preAuthorization: PreAuthorizationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "pre_authorization_id"
    )
    val standalone: StandAlonePreAuthorizationEntity
)
