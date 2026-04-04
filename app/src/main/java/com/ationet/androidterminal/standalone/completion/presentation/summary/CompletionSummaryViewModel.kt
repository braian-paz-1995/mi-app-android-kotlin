package com.ationet.androidterminal.standalone.completion.presentation.summary

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavType
import androidx.navigation.toRoute
import com.ationet.androidterminal.core.domain.model.LoadingState
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.presentation.util.DisplayType
import com.ationet.androidterminal.standalone.completion.domain.use_cases.ClearPendingResult
import com.ationet.androidterminal.standalone.completion.domain.use_cases.ExecuteCompletionUseCase
import com.ationet.androidterminal.standalone.completion.navigation.CompletionDestination
import com.ationet.androidterminal.standalone.completion.navigation.CompletionNavTypes
import com.ationet.androidterminal.standalone.completion.navigation.PreAuthorizationData
import com.ationet.androidterminal.standalone.completion.navigation.PreAuthorizationProductData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject
import kotlin.reflect.typeOf
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class CompletionSummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val executeCompletionUseCase: ExecuteCompletionUseCase,

) : ViewModel() {
    private val arguments = savedStateHandle.toRoute<CompletionDestination.CompletionSummary>(
        typeMap = mapOf(
            typeOf<Double>() to CompletionNavTypes.double,
            typeOf<PreAuthorizationData>() to CompletionNavTypes.preAuthorizationData,
            typeOf<PreAuthorizationProductData>() to CompletionNavTypes.productData,
            typeOf<DisplayType>() to NavType.EnumType(DisplayType::class.java)
        )
    )

    private val _state: MutableStateFlow<CompletionSummaryState> =
        MutableStateFlow(CompletionSummaryState.InProgress(loadingState = LoadingState.Loading))
    val state: StateFlow<CompletionSummaryState> get() = _state.asStateFlow()

    init {
        viewModelScope.launch CompletionProcess@{
            val result = try {
                executeCompletionUseCase.invoke(
                    preAuthorizationId = arguments.preAuthorization.id,
                    unitPrice = arguments.preAuthorization.product.unitPrice,
                    completionAmount = arguments.requestedAmount,
                    completionQuantity = arguments.requestedQuantity,
                    productName = arguments.preAuthorization.product.name,
                    productCode = arguments.preAuthorization.product.code
                )
            } catch (e: CancellationException) {
                /* Forward cancellation */
                throw e
            } catch (e: Throwable) {
                Log.e(TAG, "Completion error", e)
                _state.update {
                    CompletionSummaryState.Error(
                        errorCode = null,
                        message = ""
                    )
                }
                return@CompletionProcess
            }

            when (result) {
                is ClearPendingResult.Error -> {
                    _state.update { CompletionSummaryState.InProgress(LoadingState.Failure) }

                    delay(2.seconds)

                    _state.update {
                        CompletionSummaryState.Error(
                            errorCode = result.errorCode,
                            message = result.message,
                        )
                    }
                }

                is ClearPendingResult.Success -> {
                    _state.update { CompletionSummaryState.InProgress(LoadingState.Success) }

                    delay(1.seconds)

                    _state.update {
                        CompletionSummaryState.Success(
                            authorizationCode = result.authorizationCode
                        )
                    }
                    delay(2.seconds)

                    _state.update {
                        CompletionSummaryState.Summary(
                            authorizationCode = result.authorizationCode,
                            date = result.date,
                            product = result.product,
                            quantity = arguments.requestedQuantity,
                            amount = result.amount,
                            currencySymbol = arguments.preAuthorization.currencySymbol,
                            quantityUnit = arguments.preAuthorization.quantityUnit,
                            receiptId = result.receiptId,
                            language = arguments.preAuthorization.language,
                            completionType = arguments.preAuthorization.preAuthorizationType
                        )
                    }
                }

                ClearPendingResult.CommunicationError -> {
                    _state.update { CompletionSummaryState.InProgress(LoadingState.Failure) }

                    delay(2.seconds)

                    _state.update { CompletionSummaryState.CommunicationError }
                }
            }
        }
    }

    private companion object {
        private const val TAG: String = "CompletionSummaryVM"
    }
}

sealed interface CompletionSummaryState {
    data class InProgress(
        val loadingState: LoadingState
    ) : CompletionSummaryState

    data class Success(
        val authorizationCode: String,
    ) : CompletionSummaryState

    data class Error(
        val errorCode: String? = null,
        val message: String,
    ) : CompletionSummaryState

    data object CommunicationError : CompletionSummaryState

    data class Summary(
        val date: LocalDateTime,
        val product: String,
        val authorizationCode: String,
        val quantity: Double,
        val amount: Double,
        val currencySymbol: String,
        val quantityUnit: String,
        val receiptId: Int,
        val language: Configuration.LanguageType,
        val completionType: DisplayType
    ) : CompletionSummaryState
}