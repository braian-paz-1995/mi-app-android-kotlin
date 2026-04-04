package com.ationet.androidterminal.standalone.receipts.presentation.receipts_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptView
import com.ationet.androidterminal.core.domain.repository.ReceiptRepository
import com.ationet.androidterminal.core.domain.use_case.batch.GetLastOpenBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import com.ationet.androidterminal.core.presentation.receipts.ReceiptsListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ReceiptListViewModel @Inject constructor(
    receiptRepository: ReceiptRepository,
    getLastOpenBatchUseCase: GetLastOpenBatchUseCase,
    private val configurationUseCase: ConfigurationUseCase
) : ViewModel() {
    val state: StateFlow<ReceiptListState> get() = _state.asStateFlow()
    private val _state = MutableStateFlow<ReceiptListState>(ReceiptListState.Loading)
    val batchId = runBlocking { getLastOpenBatchUseCase.invoke()?.id ?: 0 }

    val controllerType = configurationUseCase.getConfiguration().controllerType

    val receipts: Flow<PagingData<ReceiptsListItem>> = receiptRepository
        .getReceiptHeaders(batchId, controllerType.toString())
        .mapToUIModel()
        .cachedIn(viewModelScope)

    private fun Flow<PagingData<ReceiptView>>.mapToUIModel(): Flow<PagingData<ReceiptsListItem>> {
        return map { pagingData ->
            pagingData
                .mapItem()
                .insertDateSeparator()
        }
    }

    private fun PagingData<ReceiptView>.mapItem(): PagingData<ReceiptsListItem.Item> {
        return map {
            ReceiptsListItem.Item(
                typeOfOperation = it.transactionName,
                localTransactionDateTime = it.transactionDateTime,
                authCode = it.authorizationCode,
                vehicle = it.vehicle,
                driver = it.driver,
                quantity = it.quantity,
                amount = it.amount,
                receiptId = it.receiptId,
                unitOfMeasure = it.unitOfMeasure,
                currencySymbol = it.currencySymbol,
                responseCode = it.responseCode,
                responseMessage = it.responseMessage
            )
        }
    }

    private fun PagingData<ReceiptsListItem.Item>.insertDateSeparator(): PagingData<ReceiptsListItem> {
        return insertSeparators { item: ReceiptsListItem.Item?, item2: ReceiptsListItem.Item? ->
            if (item2 != null && item?.localTransactionDateTime?.date != item2.localTransactionDateTime.date) {
                ReceiptsListItem.Separator(
                    dateTime = item2.localTransactionDateTime,
                )
            } else {
                null
            }
        }
    }
}

sealed interface ReceiptListState {
    data object Loading : ReceiptListState
}