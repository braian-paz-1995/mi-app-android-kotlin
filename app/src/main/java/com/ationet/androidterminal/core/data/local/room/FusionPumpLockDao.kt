package com.ationet.androidterminal.core.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ationet.androidterminal.core.data.local.room.entity.fleet.sale.FusionPumpLockEntity
import com.ationet.androidterminal.core.data.local.room.entity.transaction.TransactionView
import com.ationet.androidterminal.fusion.core.domain.model.PumpLockStatus

@Dao
interface FusionPumpLockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FusionPumpLockEntity)

    @Query("""
        UPDATE fusion_pump_lock
        SET status = :status,
            updated_at = :updatedAt
        WHERE fusion_sale_id = :fusionSaleId
        AND terminal_id = :terminalId
    """)
    suspend fun updateStatus(
        fusionSaleId: Int,
        terminalId: String,
        status: PumpLockStatus,
        updatedAt: Long
    )

    @Query("""
        SELECT * FROM fusion_pump_lock
        WHERE fusion_sale_id = :fusionSaleId
        LIMIT 1
    """)
    suspend fun getByFusionSaleId(fusionSaleId: Int): FusionPumpLockEntity?

    @Query("SELECT * FROM fusion_pump_lock")
    suspend fun getAll(): List<FusionPumpLockEntity>

    @Query("""
    SELECT * FROM fusion_pump_lock
    WHERE status = :status
""")
    suspend fun getByStatus(status: PumpLockStatus): List<FusionPumpLockEntity>

    @Query("""
    DELETE FROM fusion_pump_lock
    WHERE status = :status
""")
    suspend fun deleteByStatus(status: PumpLockStatus): Int
}