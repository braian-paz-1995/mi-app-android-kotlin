package com.ationet.androidterminal.standalone.sale.presentation.operationType

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.standalone.sale.data.local.SaleOperationStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.ationet.androidterminal.standalone.sale.domain.model.OperationType

sealed interface OperationTypeState {
    data object ListOperationType : OperationTypeState
}

@HiltViewModel
class OperationTypeViewModel @Inject constructor(
    private val operationStateRepository: SaleOperationStateRepository,
    getConfiguration: GetConfiguration
) : ViewModel() {
    private val configuration = getConfiguration()
    val controllerType = mutableStateOf(configuration.controllerType)
    private val _state = MutableStateFlow<OperationTypeState>(OperationTypeState.ListOperationType)
    val state = _state.asStateFlow()
    fun selectFuel() = operationStateRepository.setOperationType(OperationType.Fuel)
    fun selectSku() = operationStateRepository.setOperationType(OperationType.Sku)
}