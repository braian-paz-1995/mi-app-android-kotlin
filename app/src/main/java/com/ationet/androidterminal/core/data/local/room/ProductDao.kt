package com.ationet.androidterminal.core.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ationet.androidterminal.core.data.local.room.entity.product.ProductEntity

@Dao
interface ProductDao {
    @Insert
    fun insertProduct(product: ProductEntity): Long

    @Update
    fun updateProduct(product: ProductEntity): Int

    @Query("UPDATE products SET code = :productCodeNew, name = :productName, unit_price = :productUnitPrice, is_fuel = :isFuel, is_sku = :isSku WHERE code = :productCodeOld")
    fun updateProduct(productCodeOld: String, productCodeNew: String, productName: String, productUnitPrice: Double, isFuel: Boolean, isSku: Boolean): Int

    @Query("UPDATE products SET `order` = :order WHERE code = :productCode")
    fun updateOrderProduct(productCode: String, order: Int): Int

    @Query("DELETE FROM products WHERE code = :productCode")
    fun deleteProduct(productCode: String): Int

    @Delete
    fun deleteProduct(product: ProductEntity): Int

    @Query("SELECT * FROM products WHERE code = :productCode")
    fun getProductByCode(productCode: String): ProductEntity?

    @Query("SELECT * FROM products WHERE name = :productName")
    fun getProductByName(productName: String): ProductEntity?

    @Query("SELECT * FROM products ORDER BY `order`, datetime(updated_at) DESC")
    fun getAllProducts(): List<ProductEntity>
}