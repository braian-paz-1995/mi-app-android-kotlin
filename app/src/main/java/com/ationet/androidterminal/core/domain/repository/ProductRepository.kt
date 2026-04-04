package com.ationet.androidterminal.core.domain.repository

import com.ationet.androidterminal.maintenance.product.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(): List<Product>
    suspend fun getProductById(productCode: String): Product?
    suspend fun getProductByName(productName: String): Product?
    suspend fun insertProduct(product: Product): Long
    suspend fun updateProduct(product: Product): Int
    suspend fun updateProduct(productCode: String, product: Product): Int
    suspend fun updateOrderProduct(productCode: String, order: Int): Int
    suspend fun deleteProduct(productCode: String): Int
    suspend fun deleteProduct(product: Product): Int
}