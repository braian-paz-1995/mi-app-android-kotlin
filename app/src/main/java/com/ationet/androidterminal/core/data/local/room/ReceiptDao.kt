package com.ationet.androidterminal.core.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ationet.androidterminal.core.data.local.room.entity.receipt.ReceiptEntity
import com.ationet.androidterminal.core.data.local.room.entity.receipt.ReceiptProductModifierEntity
import com.ationet.androidterminal.core.data.local.room.entity.receipt.ReceiptViewEntity
import com.ationet.androidterminal.core.data.local.room.entity.receipt.ReceiptWithModifiers

@Dao
interface ReceiptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createReceipt(receipt: ReceiptEntity): Long

    @Insert
    @Transaction
    suspend fun createReceiptModifiers(vararg modifiers: ReceiptProductModifierEntity): List<Long>

    @Transaction
    suspend fun createReceipt(receipt: ReceiptEntity, modifiers: List<ReceiptProductModifierEntity>): ReceiptWithModifiers {
        /**
         * NOTE: Be aware that we are converting the ROWID returned by sqlite as
         * the generated primary key. This should work only under the assumption that the primary
         * key is a INTEGER PRIMARY KEY, which is the case.
         * See: https://www.sqlite.org/rowidtable.html
         * */
        val rowId = createReceipt(receipt).toInt()

        if (modifiers.isEmpty()) {
            return ReceiptWithModifiers(
                receipt = receipt.copy(id = rowId),
                modifiers = emptyList()
            )
        }

        val modifiersWithReceiptId = modifiers.map {
            it.copy(receiptId = rowId)
        }

        val rowIds = createReceiptModifiers(*modifiersWithReceiptId.toTypedArray())

        return ReceiptWithModifiers(
            receipt = receipt.copy(id = rowId),
            modifiers = modifiersWithReceiptId.zip(rowIds) { modifier, modifierRowId ->
                modifier.copy(id = modifierRowId.toInt())
            }
        )
    }

    @Transaction
    @Query("SELECT * FROM receipt WHERE id = :id")
    suspend fun getReceipt(id: Int): ReceiptWithModifiers?


    @Query(
        "SELECT receipt_view.* " +
                "FROM receipt_view " +
                "WHERE receipt_view.batch_id = :batchId " +
                "AND (:pumpId IS NULL OR :pumpId = 0 OR receipt_view.pump_id = :pumpId) " +
                "AND receipt_view.controller_owner = :controllerOwner " +
                "AND NOT EXISTS (" +
                "    SELECT 1 " +
                "    FROM void_transaction " +
                "    WHERE void_transaction.transaction_sequence_number = receipt_view.tsn " +
                ")" +
                "AND NOT EXISTS (" +
                "    SELECT 1 " +
                "    FROM void_transaction " +
                "    WHERE void_transaction.authorization_code = receipt_view.authorization_code" +
                ") ORDER BY datetime(transaction_date_time) DESC LIMIT :limit OFFSET :offset"
    )
    suspend fun getReceiptHeaders(offset: Int, limit: Int, batchId: Int,pumpId: Int?, controllerOwner: String): List<ReceiptViewEntity>

    @Query("DELETE FROM receipt WHERE id = :id")
    suspend fun delete(id: Int): Int

    @Query("SELECT * FROM receipt")
    @Transaction
    suspend fun getAll(): List<ReceiptWithModifiers>
}