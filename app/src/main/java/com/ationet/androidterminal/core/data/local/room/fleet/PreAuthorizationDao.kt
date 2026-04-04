package com.ationet.androidterminal.core.data.local.room.fleet

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.ationet.androidterminal.core.data.local.room.entity.fleet.pre_authorization.FusionPreAuthorizationEntity
import com.ationet.androidterminal.core.data.local.room.entity.fleet.pre_authorization.PreAuthorizationEntity
import com.ationet.androidterminal.core.data.local.room.entity.fleet.pre_authorization.PreAuthorizationWithFusion
import com.ationet.androidterminal.core.data.local.room.entity.fleet.pre_authorization.PreAuthorizationWithStandalone
import com.ationet.androidterminal.core.data.local.room.entity.fleet.pre_authorization.StandAlonePreAuthorizationEntity

@Dao
interface PreAuthorizationDao {
    @Insert
    suspend fun create(preAuthorization: PreAuthorizationEntity): Long

    @Insert
    suspend fun create(fusionPreAuthorizationEntity: FusionPreAuthorizationEntity)

    @Insert
    suspend fun create(standAlonePreAuthorizationEntity: StandAlonePreAuthorizationEntity)

    @Transaction
    suspend fun create(preAuthorization: PreAuthorizationEntity, fusionPreAuthorizationEntity: FusionPreAuthorizationEntity) {
        val rowId = create(preAuthorization).toInt()

        create(fusionPreAuthorizationEntity.copy(preAuthorizationId = rowId))
    }

    @Transaction
    suspend fun create(preAuthorization: PreAuthorizationEntity, standAlonePreAuthorizationEntity: StandAlonePreAuthorizationEntity) {
        val rowId = create(preAuthorization).toInt()

        create(standAlonePreAuthorizationEntity.copy(preAuthorizationId = rowId))
    }

    @Query("SELECT * FROM pre_authorization WHERE id = :id")
    @Transaction
    suspend fun getPreAuthorizationFusion(id: Int): PreAuthorizationWithFusion?

    @Query("SELECT * FROM pre_authorization WHERE id = :id")
    @Transaction
    suspend fun getPreAuthorization(id: Int): PreAuthorizationWithStandalone?

    @Query("""
        SELECT * FROM pre_authorization 
        WHERE EXISTS (
            SELECT 1 FROM fusion_pre_authorization 
            WHERE fusion_pre_authorization.pre_authorization_id = pre_authorization.id AND fusion_pre_authorization.state = :state 
        )
    """)
    @Transaction
    suspend fun getPreAuthorizationFusionWithState(state: String): List<PreAuthorizationWithFusion>

    @Query("""
        SELECT * FROM pre_authorization 
        WHERE EXISTS (
            SELECT 1 FROM fusion_pre_authorization 
            WHERE fusion_pre_authorization.pre_authorization_id = pre_authorization.id AND fusion_pre_authorization.state = :state 
        )
        ORDER BY datetime(updated_at) DESC LIMIT :limit OFFSET :offset
    """)
    @Transaction
    suspend fun getPreAuthorizationFusionWithState(offset: Int, limit: Int, state: String): List<PreAuthorizationWithFusion>

    @Query("UPDATE fusion_pre_authorization SET state = :state WHERE pre_authorization_id = :id")
    suspend fun updatePreAuthorizationFusionState(id: Int, state: String)

    @Query("UPDATE fusion_pre_authorization SET amount = :amount, quantity = :quantity, sale_id = :saleId, product_grade = :grade, product_name = :name, product_corporate_number = :corporateNumber, product_unit_price = :unitPrice WHERE pre_authorization_id = :id")
    suspend fun updateCompletionData(id: Int, amount: Double, quantity: Double, saleId: Int?, grade: String? = null, name: String? = null, corporateNumber: String? = null, unitPrice: Double? = null)

    @Query("SELECT * FROM pre_authorization WHERE authorization_code = :authorizationCode")
    @Transaction
    suspend fun getPreAuthorizationByAuthCodeFusion(authorizationCode: String): PreAuthorizationWithFusion?

    @Query("SELECT * FROM pre_authorization WHERE authorization_code = :authorizationCode")
    @Transaction
    suspend fun getPreAuthorizationByAuthCode(authorizationCode: String): PreAuthorizationWithStandalone?

    @Query("SELECT * FROM pre_authorization WHERE primary_track= :primaryTrack")
    @Transaction
    suspend fun getPreAuthorizationByPrimaryTrack(primaryTrack: String): PreAuthorizationWithStandalone?

    @Query("DELETE FROM pre_authorization WHERE id= :id")
    suspend fun deletePreAuthorization(id: Int): Int

    @Query("SELECT * FROM pre_authorization")
    @Transaction
    suspend fun getAllPreAuthorization(): List<PreAuthorizationWithStandalone>

    @Query("SELECT * FROM pre_authorization")
    @Transaction
    suspend fun getAllPreAuthorizationFusion(): List<PreAuthorizationWithFusion>
}