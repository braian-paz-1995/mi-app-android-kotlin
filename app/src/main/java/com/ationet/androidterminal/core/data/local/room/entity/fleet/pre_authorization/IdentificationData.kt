package com.ationet.androidterminal.core.data.local.room.entity.fleet.pre_authorization

import androidx.room.ColumnInfo

data class IdentificationData(
    @ColumnInfo(name = "primary_track") val primaryTrack: String,
    @ColumnInfo(name = "secondary_track") val secondaryTrack: String?,
)