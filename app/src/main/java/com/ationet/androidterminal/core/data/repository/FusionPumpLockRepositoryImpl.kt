package com.ationet.androidterminal.core.data.repository

import com.ationet.androidterminal.core.data.local.room.FusionPumpLockDao
import com.ationet.androidterminal.core.data.local.room.entity.fleet.sale.FusionPumpLockEntity
import com.ationet.androidterminal.core.domain.repository.FusionPumpLockRepository
import com.ationet.androidterminal.fusion.core.domain.model.PumpLockStatus
import javax.inject.Inject

class FusionPumpLockRepositoryImpl @Inject constructor(
    private val dao: FusionPumpLockDao
) : FusionPumpLockRepository {

    override suspend fun updateStatus(
        fusionSaleId: Int,
        terminalId: String,
        status: PumpLockStatus
    ) {
        val existing = dao.getByFusionSaleId(fusionSaleId)

        if (existing == null) {
            dao.insert(
                FusionPumpLockEntity(
                    fusionSaleId = fusionSaleId,
                    terminalId = terminalId,
                    status = status,
                    updatedAt = System.currentTimeMillis()
                )
            )
        } else {
            dao.updateStatus(
                fusionSaleId = fusionSaleId,
                terminalId = terminalId,
                status = status,
                updatedAt = System.currentTimeMillis()
            )
        }
    }
    override suspend fun getLocked(): List<FusionPumpLockEntity> {
        return dao.getByStatus(PumpLockStatus.LOCKED)
    }
    override suspend fun deleteUnlock(): Int {
        return dao.deleteByStatus(PumpLockStatus.UNLOCKED)
    }
}
