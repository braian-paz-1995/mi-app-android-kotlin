package com.ationet.androidterminal.core.domain.use_case.product

import com.ationet.androidterminal.core.domain.repository.ProductRepository
import com.ationet.androidterminal.maintenance.product.domain.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class UpdateOrderProducts(
    private val productRepository: ProductRepository
) {
    operator fun invoke(products: List<Product>) {
        runBlocking {
            withContext(Dispatchers.IO) {
                val deferredList = products.map { product ->
                    async {
                        productRepository.updateOrderProduct(product.code, product.order)
                    }
                }
                deferredList.joinAll()
            }
        }
    }
}