package com.ationet.androidterminal.core.data.local.room.loyalty

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_batch.LoyaltyBatchEntity
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_batch.LoyaltyBatchTransactionData

@Dao
interface LoyaltyBatchDao {
    @Insert
    suspend fun openLoyaltyBatch(batchEntity: LoyaltyBatchEntity): Long

    @Query("UPDATE loyalty_batch SET state = 'CLOSED' WHERE id = :id")
    suspend fun closeLoyaltyBatch(id: Long)

    @Query("SELECT * FROM loyalty_batch WHERE state = 'OPEN' ORDER BY datetime(transaction_date_time) DESC LIMIT 1")
    suspend fun getLastOpenLoyaltyBatch(): LoyaltyBatchEntity?

    @Query("SELECT  transaction_with_batch.id,\n" +
            "       transaction_with_batch.transaction_product_amount\n," +
            "       transaction_with_batch.transaction_product_quantity\n," +
            "       transaction_with_batch.transaction_product_unit_price\n" +
            "FROM (\n" +
            "    SELECT *\n" +
            "    FROM loyaltyAccumulation\n" +
            "    WHERE batch_id = :batchId\n AND controller_type = :controllerType" +
            ") AS transaction_with_batch\n" +
            "LEFT JOIN loyalty_void_transaction v1\n" +
            "    ON transaction_with_batch.authorization_code = v1.authorization_code\n" +
            "    OR transaction_with_batch.transaction_sequence_number = v1.transaction_sequence_number\n" +
            "WHERE v1.authorization_code IS NULL AND v1.transaction_sequence_number IS NULL;"
    )
    suspend fun getTransactionsWithLoyaltyBatchId(batchId: Long, controllerType: String): List<LoyaltyBatchTransactionData>

    @Query("SELECT  transaction_view.id,\n" +
            "       coalesce(transaction_view.transaction_product_amount, 0) as transaction_product_amount,\n" +
            "       coalesce(transaction_view.transaction_product_quantity, 0) as transaction_product_quantity,\n" +
            "       coalesce(transaction_view.transaction_product_unit_price, 0) as transaction_product_unit_price\n" +
            "FROM transaction_view\n" +
            "INNER JOIN loyalty_void_transaction ON\n" +
            "           transaction_view.id = loyalty_void_transaction.transaction_id\n" +
            "           AND transaction_view.authorization_code = loyalty_void_transaction.old_Authorization_Code\n" +
            "WHERE transaction_view.batch_id = :batchId"
    )
    suspend fun getVoidTransactionsWithLoyaltyBatchId(batchId: Long): List<LoyaltyBatchTransactionData>
    @Query("""
    SELECT r.id,
           r.transaction_product_amount,
           r.transaction_product_quantity,
           r.transaction_product_unit_price,
           r.transaction_payment_method
    FROM loyaltyPointsRedemption r
    WHERE r.batch_id = :batchId
      AND r.controller_type = :controllerType
""")
    suspend fun getTransactionsRedemptionPointsWithLoyaltyBatchId(batchId: Long, controllerType: String): List<LoyaltyBatchTransactionData>
    @Query("""
    SELECT r.id,
           r.transaction_product_amount,
           r.transaction_product_quantity,
           r.transaction_product_unit_price,
           r.transaction_payment_method
    FROM loyaltyRewardsRedemption r
    WHERE r.batch_id = :batchId
      AND r.controller_type = :controllerType
""")
    suspend fun getTransactionsRedemptionRewardsWithLoyaltyBatchId(batchId: Long, controllerType: String): List<LoyaltyBatchTransactionData>

}