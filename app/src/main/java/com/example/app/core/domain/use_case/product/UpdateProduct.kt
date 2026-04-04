package com.ationet.androidterminal.core.domain.use_case.product

import com.ationet.androidterminal.core.domain.repository.ProductRepository
import com.ationet.androidterminal.maintenance.product.domain.model.Product

class UpdateProduct(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(product: Product): Int {
        return productRepository.updateProduct(product)
    }

    suspend operator fun invoke(productCode: String, product: Product): Int {
        return productRepository.updateProduct(productCode, product)
    }
}