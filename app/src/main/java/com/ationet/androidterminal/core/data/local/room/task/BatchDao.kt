package com.ationet.androidterminal.core.data.local.room.task

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ationet.androidterminal.core.data.local.room.entity.task.batch.BatchEntity
import com.ationet.androidterminal.core.data.local.room.entity.task.batch.BatchTransactionData

@Dao
interface BatchDao {
    @Insert
    suspend fun openBatch(batchEntity: BatchEntity): Long

    @Query("UPDATE batch SET state = 'CLOSED' WHERE id = :id")
    suspend fun closeBatch(id: Long)

    @Query("SELECT * FROM batch WHERE state = 'OPEN' ORDER BY datetime(transaction_date_time) DESC LIMIT 1")
    suspend fun getLastOpenBatch(): BatchEntity?

    @Query("SELECT  transaction_with_batch.id,\n" +
            "       transaction_with_batch.transaction_product_amount\n," +
            "       transaction_with_batch.transaction_product_quantity\n," +
            "       transaction_with_batch.transaction_product_unit_price\n" +
            "FROM (\n" +
            "    SELECT *\n" +
            "    FROM sale\n" +
            "    WHERE batch_id = :batchId\n AND controller_type = :controllerType" +
            "       UNION ALL\n" +
            "    SELECT *\n" +
            "    FROM completion\n" +
            "    WHERE batch_id = :batchId AND completion.transaction_product_amount != 0\n" +
            ") AS transaction_with_batch\n" +
            "LEFT JOIN void_transaction v1\n" +
            "    ON transaction_with_batch.authorization_code = v1.authorization_code\n" +
            "    OR transaction_with_batch.transaction_sequence_number = v1.transaction_sequence_number\n" +
            "WHERE v1.authorization_code IS NULL AND v1.transaction_sequence_number IS NULL;"
    )
    suspend fun getTransactionsWithBatchId(batchId: Long, controllerType: String): List<BatchTransactionData>

    @Query("SELECT  transaction_view.id,\n" +
            "       coalesce(transaction_view.transaction_product_amount, 0) as transaction_product_amount,\n" +
            "       coalesce(transaction_view.transaction_product_quantity, 0) as transaction_product_quantity,\n" +
            "       coalesce(transaction_view.transaction_product_unit_price, 0) as transaction_product_unit_price\n" +
            "FROM transaction_view\n" +
            "INNER JOIN void_transaction ON\n" +
            "           transaction_view.id = void_transaction.transaction_id\n" +
            "           AND transaction_view.authorization_code = void_transaction.authorization_code\n" +
            "WHERE transaction_view.batch_id = :batchId"
    )
    suspend fun getVoidTransactionsWithBatchId(batchId: Long): List<BatchTransactionData>
    @Query("""
    SELECT r.id,
           r.transaction_product_amount,
           r.transaction_product_quantity,
           r.transaction_product_unit_price,
           r.transaction_payment_method
    FROM rechargecc r
    WHERE r.batch_id = :batchId
      AND r.controller_type = :controllerType
""")
    suspend fun getTransactionsRechargeCCWithBatchId(batchId: Long, controllerType: String): List<BatchTransactionData>
    @Query("""
    SELECT r.id,
           r.transaction_product_amount,
           r.transaction_product_quantity,
           r.transaction_product_unit_price,
           r.transaction_payment_method
    FROM reversecc r
    WHERE r.batch_id = :batchId
      AND r.controller_type = :controllerType
""")
    suspend fun getTransactionsReverseCCWithBatchId(batchId: Long, controllerType: String): List<BatchTransactionData>

}