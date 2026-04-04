package com.ationet.androidterminal.standalone.balance_enquiry.presentation.product_selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atio.log.Logger
import com.atio.log.util.debug
import com.atio.log.util.error
import com.ationet.androidterminal.core.domain.use_case.product.GetAllProducts
import com.ationet.androidterminal.standalone.balance_enquiry.data.local.BalanceEnquiryStateRepository
import com.ationet.androidterminal.standalone.sale.domain.model.ProductStandAlone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProductSelectionState {
    data class ListProductSelection(val products: List<ProductStandAlone>) : ProductSelectionState

}

@HiltViewModel
class ProductSelectionViewModel @Inject constructor(
    private val getAllProducts: GetAllProducts,
    private val operationStateRepository: BalanceEnquiryStateRepository
) : ViewModel() {
    private var products = mutableListOf<ProductStandAlone>()
    private val _state = MutableStateFlow<ProductSelectionState>(ProductSelectionState.ListProductSelection(emptyList()))
    val state = _state.asStateFlow()

    private companion object {
        const val TAG = "ProductSelectionViewModel"
        val logger = Logger(TAG)
    }

    init {
        getProducts()
    }

    private fun getProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                products = getAllProducts()
                    .map { product ->
                        ProductStandAlone(
                            id = product.id,
                            code = product.code,
                            name = product.name,
                            unitPrice = product.unitPrice,
                        )
                    }.toMutableList()
                _state.update { ProductSelectionState.ListProductSelection(products) }
            } catch (e: Throwable) {
                logger.error("Failed to get products", e)
                _state.update { ProductSelectionState.ListProductSelection(emptyList()) }
            }
        }
    }

    fun setProduct(product: ProductStandAlone) {
        logger.debug("Product selected: $product")
        operationStateRepository.updateState { state ->
            state.copy(product = product)
        }
    }
}