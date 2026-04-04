package com.ationet.androidterminal.core.domain.use_case.product

import com.ationet.androidterminal.core.domain.repository.ProductRepository

class GetAllProducts(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke() = productRepository.getProducts()
}