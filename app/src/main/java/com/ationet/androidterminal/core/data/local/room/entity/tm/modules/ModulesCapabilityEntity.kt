package com.ationet.androidterminal.core.data.local.room.entity.tm.modules

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "modules_capability")
data class ModulesCapabilityEntity(
    @PrimaryKey val id: String = SINGLETON_ID,
    val terminalId: String,
    val supportsCardsB2C: Boolean,
    val supportsGiftCard: Boolean,
    val supportsFleet: Boolean,
    val supportsLoyalty: Boolean,
    val supportsOffline: Boolean,
    val lastVersion: Int,
    val fetchedAtMillis: Long,
    val expiresAtMillis: Long,
    val rawRequestJson: String?,
    val nativeUrl: String,
    val rawResponseJson: String?,
    val lastAttemptOk: Boolean? = null,
    val lastHttpStatus: Int? = null,
    val lastErrorMessage: String? = null,
    val responseCode: String? = null,
    val responseText: String? = null
) {
    companion object { const val SINGLETON_ID = "GLOBAL" }
}
