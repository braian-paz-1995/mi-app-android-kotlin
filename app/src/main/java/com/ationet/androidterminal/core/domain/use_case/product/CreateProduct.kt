package com.ationet.androidterminal.core.domain.use_case.product

import com.ationet.androidterminal.core.domain.repository.ProductRepository
import com.ationet.androidterminal.maintenance.product.domain.model.Product

class CreateProduct(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(product: Product): Long {
        return productRepository.insertProduct(product)
    }
}