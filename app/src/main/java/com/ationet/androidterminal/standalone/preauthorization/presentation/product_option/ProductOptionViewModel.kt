package com.ationet.androidterminal.standalone.preauthorization.presentation.product_option

import androidx.lifecycle.ViewModel
import com.ationet.androidterminal.core.domain.model.preauthorization.InputType
import com.ationet.androidterminal.standalone.preauthorization.data.local.PreAuthorizationOperationStateRepository
import com.ationet.androidterminal.standalone.preauthorization.domain.model.Quantity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed interface ProductOptionState {
    data object ListProductOption : ProductOptionState
}

@HiltViewModel
class ProductOptionViewModel @Inject constructor(
    private val operationStateRepository: PreAuthorizationOperationStateRepository
) : ViewModel() {
    private val _state = MutableStateFlow<ProductOptionState>(ProductOptionState.ListProductOption)
    val state = _state.asStateFlow()

    fun resetQuantityState() {
        operationStateRepository.updateState {
            it.copy(
                quantity = Quantity(inputType = InputType.FillUp)
            )
        }
    }
}