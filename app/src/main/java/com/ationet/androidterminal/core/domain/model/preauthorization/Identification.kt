package com.ationet.androidterminal.core.domain.model.preauthorization

import com.ationet.androidterminal.core.data.local.room.entity.fleet.pre_authorization.IdentificationData

data class Identification(
    val primaryTrack: String,
    val secondaryTrack: String? = null,
){
    fun toEntity(): IdentificationData = IdentificationData(
        primaryTrack = primaryTrack,
        secondaryTrack = secondaryTrack
    )
}