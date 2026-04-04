package com.ationet.androidterminal.core.domain.use_case.product

import com.ationet.androidterminal.core.domain.repository.ProductRepository
import com.ationet.androidterminal.maintenance.product.domain.model.Product

class DeleteProduct(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productCode: String): Int {
        return productRepository.deleteProduct(productCode)
    }

    suspend operator fun invoke(product: Product): Int {
        return productRepository.deleteProduct(product)
    }
}