package com.ationet.androidterminal.core.domain.use_case.product

import com.ationet.androidterminal.core.domain.repository.ProductRepository
import com.ationet.androidterminal.maintenance.product.domain.model.Product

class GetProduct(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(code: String): Product? {
        return productRepository.getProductById(code)
    }
}