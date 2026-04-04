package com.ationet.androidterminal.core.data.local.room.tm

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ationet.androidterminal.core.data.local.room.entity.tm.payment_method.PaymentMethodEntity

@Dao
interface PaymentMethodDao {

    @Insert
    suspend fun insert(paymentMethod: PaymentMethodEntity): Long

    @Update
    suspend fun update(paymentMethod: PaymentMethodEntity): Int

    @Query("DELETE FROM paymentMethod WHERE id IN (:ids)")
    suspend fun delete(ids: List<Int>): Int


    @Query("SELECT * FROM paymentMethod ORDER BY order_value ASC")
    suspend fun getAll(): List<PaymentMethodEntity>
}