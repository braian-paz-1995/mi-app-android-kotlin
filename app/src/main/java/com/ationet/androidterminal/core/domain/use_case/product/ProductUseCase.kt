package com.ationet.androidterminal.core.domain.use_case.product

data class ProductUseCase(
    val createProduct: CreateProduct,
    val deleteProduct: DeleteProduct,
    val updateProduct: UpdateProduct,
    val getProduct: GetProduct,
    val getAllProducts: GetAllProducts,
    val updateOrderProducts: UpdateOrderProducts,
)