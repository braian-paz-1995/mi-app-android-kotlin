package com.ationet.androidterminal.core.data.local.room.fleet

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.ationet.androidterminal.core.data.local.room.entity.fleet.sale.FusionSaleEntity
import com.ationet.androidterminal.core.data.local.room.entity.fleet.sale.SaleEntity
import com.ationet.androidterminal.core.data.local.room.entity.fleet.sale.SaleWithFusion

@Dao
interface SaleDao {
    @Insert
    suspend fun create(sale: SaleEntity): Long

    @Insert
    suspend fun create(fusionSale: FusionSaleEntity)

    @Transaction
    suspend fun create(sale: SaleEntity, fusionSale: FusionSaleEntity): SaleWithFusion {
        val rowId = create(sale).toInt()

        create(fusionSale.copy(saleId = rowId))

        return SaleWithFusion(
            sale = sale.copy(id = rowId),
            fusion = fusionSale.copy(saleId = rowId)
        )
    }


    @Transaction
    @Query("SELECT sale.*,fusion_sale.fusion_sale_id,fusion_sale.sale_id FROM sale INNER JOIN fusion_sale ON sale.id = fusion_sale.sale_id WHERE sale.id=:id")
    suspend fun getFusionSale(id: Int): SaleWithFusion?

    @Query("SELECT * FROM sale WHERE id=:id")
    suspend fun get(id: Int): SaleEntity?

    @Query("SELECT * FROM sale")
    suspend fun getAll(): List<SaleEntity>

    @Query("DELETE FROM sale WHERE id=:id")
    suspend fun delete(id: Int): Int
}