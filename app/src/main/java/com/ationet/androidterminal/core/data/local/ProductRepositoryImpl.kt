package com.ationet.androidterminal.core.data.local

import androidx.room.withTransaction
import com.ationet.androidterminal.core.data.local.room.AATDatabase
import com.ationet.androidterminal.core.domain.repository.ProductRepository
import com.ationet.androidterminal.maintenance.product.domain.model.Product
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val database: AATDatabase
) : ProductRepository {
    override suspend fun getProducts(): List<Product> {
        return database.productDao.getAllProducts().map { it.toDomain() }
    }

    override suspend fun getProductById(productCode: String): Product? {
        return database.productDao.getProductByCode(productCode)?.toDomain()
    }

    override suspend fun getProductByName(productName: String): Product? {
        return database.productDao.getProductByName(productName)?.toDomain()
    }

    override suspend fun insertProduct(product: Product): Long {
        return database.withTransaction {
            /* Order set to 0 to appear at the top of the products screen */
            database.productDao.insertProduct(product.toEntity().copy(order = 0))
            return@withTransaction product.code.toLong()
        }
    }

    override suspend fun updateProduct(product: Product): Int {
        return database.withTransaction {
            val rowsUpdated = database.productDao.updateProduct(product.toEntity())
            return@withTransaction rowsUpdated
        }
    }

    override suspend fun updateProduct(productCode: String, product: Product): Int {
        return database.withTransaction {
            val rowsUpdated = database.productDao.updateProduct(
                productCodeOld = productCode, productCodeNew = product.code, productName = product.name,
                productUnitPrice = product.unitPrice, isFuel = product.isFuel, isSku = product.isSku
            )
            return@withTransaction rowsUpdated
        }
    }

    override suspend fun updateOrderProduct(productCode: String, order: Int): Int {
        return database.withTransaction {
            val rowsUpdated = database.productDao.updateOrderProduct(productCode, order)
            return@withTransaction rowsUpdated
        }
    }

    override suspend fun deleteProduct(productCode: String): Int {
        return database.withTransaction {
            val rowsDeleted = database.productDao.deleteProduct(productCode)
            return@withTransaction rowsDeleted
        }
    }

    override suspend fun deleteProduct(product: Product): Int {
        return database.withTransaction {
            val rowsDeleted = database.productDao.deleteProduct(product.toEntity())
            return@withTransaction rowsDeleted
        }
    }
}