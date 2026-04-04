package com.ationet.androidterminal.core.domain.repository

import com.ationet.androidterminal.core.data.local.room.entity.fleet.sale.FusionPumpLockEntity
import com.ationet.androidterminal.fusion.core.domain.model.PumpLockStatus


interface FusionPumpLockRepository {
    suspend fun updateStatus(
        fusionSaleId: Int,
        terminalId: String,
        status: PumpLockStatus
    )
    suspend fun getLocked(): List<FusionPumpLockEntity>
    suspend fun deleteUnlock(): Int
}