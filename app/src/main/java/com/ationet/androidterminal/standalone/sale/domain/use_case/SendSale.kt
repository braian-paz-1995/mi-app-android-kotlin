package com.ationet.androidterminal.standalone.sale.domain.use_case

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.data.remote.ationet.model.DealerData
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.data.remote.ationet.model.PromptKey
import com.ationet.androidterminal.core.data.remote.ationet.model.PromptRequestKey
import com.ationet.androidterminal.core.data.remote.ationet.model.ResponseCodes
import com.ationet.androidterminal.core.domain.exception.HostUrlNotConfiguredException
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptData
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptProductInputType
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionTypeName
import com.ationet.androidterminal.core.domain.repository.ProductData
import com.ationet.androidterminal.core.domain.repository.ReceiptRepository
import com.ationet.androidterminal.core.domain.repository.Sale
import com.ationet.androidterminal.core.domain.repository.SaleRepository
import com.ationet.androidterminal.core.domain.repository.TransactionData
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestSale
import com.ationet.androidterminal.core.domain.use_case.batch.GetLastOpenBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.core.domain.util.createCommunicationErrorReceipt
import com.ationet.androidterminal.core.domain.util.createErrorReceipt
import com.ationet.androidterminal.core.domain.util.displayResponseText
import com.ationet.androidterminal.core.util.LocalDateTimeUtils
import com.ationet.androidterminal.core.util.receiptData
import com.ationet.androidterminal.standalone.preauthorization.domain.model.Prompt
import com.ationet.androidterminal.standalone.sale.data.local.SaleOperationStateRepository
import com.ationet.androidterminal.standalone.sale.domain.model.OperationType
import com.ationet.androidterminal.standalone.sale.domain.model.ProductStandAlone
import com.ationet.androidterminal.standalone.sale.domain.model.Quantity
import com.ationet.androidterminal.standalone.sale.domain.model.SaleOperationState
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

sealed interface TransactionResult {
    data class Success(val authorizationCode: String) : TransactionResult
    data class Error(val code: String = "", val message: String) : TransactionResult
    data object CommunicationError : TransactionResult
    data class RequiredPromptsAtionet(val prompts: List<Prompt>) : TransactionResult
    data class RequiredPromptsFixed(val prompts: List<Prompt>) : TransactionResult
}

@ViewModelScoped
class SendSale @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getConfiguration: GetConfiguration,
    private val operationStateRepository: SaleOperationStateRepository,
    private val requestSale: RequestSale,
    private val saleRepository: SaleRepository,
    private val createSaleReceipt: CreateSaleReceiptUseCase,
    private val getLastOpenBatchUseCase: GetLastOpenBatchUseCase,
    private val receiptRepository: ReceiptRepository
) {
    private companion object {
        private const val TAG: String = "SendSale"
    }
    
    suspend operator fun invoke(): TransactionResult {
        val configuration = getConfiguration()
        val operationState = operationStateRepository.getState()
        Log.d(TAG, "operationState=$operationState")
        val currentInstant = Clock.System.now()

        val batchId = getLastOpenBatchUseCase.invoke()?.id ?: 0

        if (configuration.controllerType == Configuration.ControllerType.STAND_ALONE) {
            if (configuration.ationet.promptsDefault) {
                if (operationState.prompts.isEmpty() || !operationState.prompts.all { it.state == Prompt.PromptState.Completed }) {
                    val result = evaluatePromptsAtionetFixed(operationState, configuration)

                    if (result is PromptsResult.IncompletePrompts) {
                        return TransactionResult.RequiredPromptsFixed(result.prompts)
                    }
                }
            }
        }

        val productState = operationState.product
        if (productState !is ProductStandAlone) {
            Log.w(TAG, "Standalone sale: Operation has no product")

            val receipt = createErrorReceipt(
                transactionName = ReceiptTransactionTypeName.Sale,
                configuration = configuration,
                currentInstant = currentInstant,
                primaryTrack = operationState.identifier.primaryTrack,
                secondaryTrack = operationState.prompts.find { it.key == PromptKey.SecondaryTrack }?.value,
                inputType = when (operationState.quantity.inputType) {
                    Quantity.InputType.Quantity -> ReceiptProductInputType.Quantity
                    Quantity.InputType.Amount -> ReceiptProductInputType.Amount
                },
                productName = "",
                productCode = "",
                productUnitPrice = 0.0,
                quantity = null,
                amount = null,
                responseCode = null,
                responseText = ContextCompat.getContextForLanguage(context).getString(R.string.product_not_found),
                receiptData = ReceiptData(),
                batchId = batchId,
                authorizationCode = null,
                invoiceNumber = null
            )

            operationStateRepository.updateState { it.copy(receipt = receipt) }

            return TransactionResult.Error(message = ContextCompat.getContextForLanguage(context).getString(R.string.product_not_found))
        }

        val productQuantity = if (operationState.quantity.inputType == Quantity.InputType.Quantity) {
            operationState.quantity.value
        } else {
            operationState.quantity.value / productState.unitPrice
        }

        val productAmount = if (operationState.quantity.inputType == Quantity.InputType.Amount) {
            operationState.quantity.value
        } else {
            productState.unitPrice * operationState.quantity.value
        }
        val isFuel = operationState.operationType == OperationType.Fuel
        val response = try {
            requestSale.invoke(
                manualEntry = false,
                localDateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
                productCode = productState.code,
                productAmount = productAmount,
                productQuantity = productQuantity,
                unitPrice = productState.unitPrice,
                transactionAmount = productAmount,
                primaryTrack = operationState.identifier.primaryTrack,
                primaryPin = operationState.prompts.find { it.key == PromptKey.PrimaryPin }?.value,
                secondaryPin = operationState.prompts.find { it.key == PromptKey.SecondaryPin }?.value,
                secondaryTrack = operationState.prompts.find { it.key == PromptKey.SecondaryTrack }?.value,
                customerData = customerData(operationState),
                dealerData = dealerData(operationState),
                operationType = isFuel,
            )
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            Log.e(TAG, "Standalone sale: Sale failed with exception", e)
            val receipt = createCommunicationErrorReceipt(
                transactionName = ReceiptTransactionTypeName.Sale,
                context = context,
                configuration = configuration,
                currentInstant = currentInstant,
                primaryTrack = operationState.identifier.primaryTrack,
                secondaryTrack = operationState.prompts.find { it.key == PromptKey.SecondaryTrack }?.value,
                inputType = when (operationState.quantity.inputType) {
                    Quantity.InputType.Quantity -> ReceiptProductInputType.Quantity
                    Quantity.InputType.Amount -> ReceiptProductInputType.Amount
                },
                productName = productState.name,
                productCode = productState.code,
                productUnitPrice = productState.unitPrice,
                quantity = productQuantity,
                amount = productAmount,
                batchId = batchId
            )
            operationStateRepository.updateState { it.copy(receipt = receipt) }

            return TransactionResult.CommunicationError
        }

        val nativeResponse = response.getOrNull()
        if (response.isFailure) {
            val exception = response.exceptionOrNull()
            if (exception != null) {
                Log.e(TAG, "Standalone sale: sale failed with exception", exception)
            } else {
                Log.w(TAG, "Standalone sale: sale failed")
            }

            return if (exception is TerminalIdNotConfiguredException || exception is HostUrlNotConfiguredException) {
                val receipt = createErrorReceipt(
                    transactionName = ReceiptTransactionTypeName.Sale,
                    configuration = configuration,
                    currentInstant = currentInstant,
                    primaryTrack = operationState.identifier.primaryTrack,
                    secondaryTrack = operationState.prompts.find { it.key == PromptKey.SecondaryTrack }?.value,
                    inputType = when (operationState.quantity.inputType) {
                        Quantity.InputType.Quantity -> ReceiptProductInputType.Quantity
                        Quantity.InputType.Amount -> ReceiptProductInputType.Amount
                    },
                    productName = (operationState.product as ProductStandAlone).name,
                    productCode = (operationState.product as ProductStandAlone).code,
                    productUnitPrice = (operationState.product as ProductStandAlone).unitPrice,
                    quantity = productQuantity,
                    amount = productAmount,
                    responseCode = null,
                    responseText = exception.message.orEmpty(),
                    receiptData = ReceiptData(),
                    batchId = batchId,
                    authorizationCode = null,
                    invoiceNumber = null
                )

                operationStateRepository.updateState { it.copy(receipt = receipt) }

                return TransactionResult.Error(message = exception.message.orEmpty())
            } else if (nativeResponse != null) {
                val receipt = createErrorReceipt(
                    transactionName = ReceiptTransactionTypeName.Sale,
                    configuration = configuration,
                    currentInstant = currentInstant,
                    primaryTrack = operationState.identifier.primaryTrack,
                    secondaryTrack = operationState.prompts.find { it.key == PromptKey.SecondaryTrack }?.value,
                    inputType = when (operationState.quantity.inputType) {
                        Quantity.InputType.Quantity -> ReceiptProductInputType.Quantity
                        Quantity.InputType.Amount -> ReceiptProductInputType.Amount
                    },
                    productName = (operationState.product as ProductStandAlone).name,
                    productCode = (operationState.product as ProductStandAlone).code,
                    productUnitPrice = (operationState.product as ProductStandAlone).unitPrice,
                    quantity = productQuantity,
                    amount = productAmount,
                    responseCode = nativeResponse.responseCode,
                    responseText = nativeResponse.displayResponseText,
                    receiptData = ReceiptData(),
                    batchId = batchId,
                    authorizationCode = nativeResponse.authorizationCode,
                    invoiceNumber = nativeResponse.invoiceNumber
                )

                operationStateRepository.updateState { it.copy(receipt = receipt) }

                TransactionResult.Error(
                    code = nativeResponse.responseCode.orEmpty(),
                    message = nativeResponse.displayResponseText
                )
            } else {
                val receipt = createCommunicationErrorReceipt(
                    transactionName = ReceiptTransactionTypeName.Sale,
                    context = context,
                    configuration = configuration,
                    currentInstant = currentInstant,
                    primaryTrack = operationState.identifier.primaryTrack,
                    secondaryTrack = operationState.prompts.find { it.key == PromptKey.SecondaryTrack }?.value,
                    inputType = when (operationState.quantity.inputType) {
                        Quantity.InputType.Quantity -> ReceiptProductInputType.Quantity
                        Quantity.InputType.Amount -> ReceiptProductInputType.Amount
                    },
                    productName = (operationState.product as ProductStandAlone).name,
                    productCode = (operationState.product as ProductStandAlone).code,
                    productUnitPrice = (operationState.product as ProductStandAlone).unitPrice,
                    quantity = productQuantity,
                    amount = productAmount,
                    batchId = batchId
                )

                operationStateRepository.updateState { it.copy(receipt = receipt) }

                TransactionResult.CommunicationError
            }
        }

        if (nativeResponse == null) {
            val receipt = createCommunicationErrorReceipt(
                transactionName = ReceiptTransactionTypeName.Sale,
                context = context,
                configuration = configuration,
                currentInstant = currentInstant,
                primaryTrack = operationState.identifier.primaryTrack,
                secondaryTrack = operationState.prompts.find { it.key == PromptKey.SecondaryTrack }?.value,
                inputType = when (operationState.quantity.inputType) {
                    Quantity.InputType.Quantity -> ReceiptProductInputType.Quantity
                    Quantity.InputType.Amount -> ReceiptProductInputType.Amount
                },
                productName = (operationState.product as ProductStandAlone).name,
                productCode = (operationState.product as ProductStandAlone).code,
                productUnitPrice = (operationState.product as ProductStandAlone).unitPrice,
                quantity = productQuantity,
                amount = productAmount,
                batchId = batchId
            )

            operationStateRepository.updateState { it.copy(receipt = receipt) }

            return TransactionResult.CommunicationError
        }

        val result = evaluatePromptsAtionetResponse(operationState, nativeResponse)

        if (result is PromptsResult.IncompletePrompts) {
            return TransactionResult.RequiredPromptsAtionet(result.prompts)
        }

        if (nativeResponse.responseCode != ResponseCodes.Authorized) {
            val receipt = createErrorReceipt(
                transactionName = ReceiptTransactionTypeName.Sale,
                configuration = configuration,
                currentInstant = currentInstant,
                primaryTrack = operationState.identifier.primaryTrack,
                secondaryTrack = operationState.prompts.find { it.key == PromptKey.SecondaryTrack }?.value,
                inputType = when (operationState.quantity.inputType) {
                    Quantity.InputType.Quantity -> ReceiptProductInputType.Quantity
                    Quantity.InputType.Amount -> ReceiptProductInputType.Amount
                },
                productName = productState.name,
                productCode = productState.code,
                productUnitPrice = productState.unitPrice,
                quantity = productQuantity,
                amount = productAmount,
                responseCode = nativeResponse.responseCode,
                responseText = nativeResponse.displayResponseText,
                receiptData = try {
                    receiptData(nativeResponse.receiptData)
                } catch (e: Throwable) {
                    Log.w(TAG, "Standalone sale: failed to decode receipt data. Defaulting to empty!!")
                    ReceiptData()
                },
                batchId = batchId,
                authorizationCode = nativeResponse.authorizationCode,
                invoiceNumber = nativeResponse.invoiceNumber
            )
            operationStateRepository.updateState { it.copy(receipt = receipt) }

            val receiptId = receiptRepository.saveReceipt(receipt)

            Log.i(TAG, "Sale receipt: Created error receipt #$receiptId")

            return TransactionResult.Error(
                code = nativeResponse.responseCode.orEmpty(),
                message = nativeResponse.displayResponseText
            )
        }

        // Calculamos el precio del producto
//        val productUnitPrice = nativeResponse.companyPrice?.productUnitPrice ?: nativeResponse.productUnitPrice?.toDoubleOrNull() ?: productState.unitPrice
//
//        val productAmountCalculated = nativeResponse.companyPrice?.productAmount ?: nativeResponse.productAmount?.toDoubleOrNull()
//        ?: if (operationState.quantity.inputType == Quantity.InputType.Amount) operationState.quantity.value else productUnitPrice * operationState.quantity.value
//
//        val productQuantityCalculated = (nativeResponse.companyPrice?.productAmount ?: nativeResponse.productAmount?.toDoubleOrNull()
//        ?: if (operationState.quantity.inputType == Quantity.InputType.Amount) operationState.quantity.value else productAmountCalculated) / productUnitPrice

        val productUnitPrice = productState.unitPrice
        val productAmountCalculated =
            if (operationState.quantity.inputType == Quantity.InputType.Amount) operationState.quantity.value else operationState.quantity.value * productUnitPrice
        val productQuantityCalculated =
            if (operationState.quantity.inputType == Quantity.InputType.Quantity) operationState.quantity.value else operationState.quantity.value / productUnitPrice

        val sale = with(nativeResponse) {
            Sale(
                authorizationCode = authorizationCode.orEmpty(),
                transactionDateTime = try {
                    LocalDateTimeUtils.LocalDateTimeFormat.parse(localTransactionDate + localTransactionTime)
                } catch (e: Exception) {
                    currentInstant.toLocalDateTime(TimeZone.currentSystemDefault())
                },
                transactionSequenceNumber = transactionSequenceNumber?.toLong() ?: 0,
                transactionData = TransactionData(
                    primaryTrack = primaryTrack.orEmpty(),
                    product = ProductData(
                        inputType = operationState.quantity.inputType.name,
                        name = productState.name,
                        code = productState.code,
                        unitPrice = productUnitPrice,
                        quantity = productQuantityCalculated,
                        amount = productAmountCalculated
                    )
                ),
                batchId = batchId,
                controllerType = configuration.controllerType
            )
        }

        saleRepository.create(sale)

        val productUnitPriceTicket = if (configuration.ticket.transactionDetails) nativeResponse.companyPrice?.productUnitPriceBase ?: productUnitPrice else productUnitPrice
        val productAmountCalculatedTicket =
            if (operationState.quantity.inputType == Quantity.InputType.Amount) productQuantityCalculated * productUnitPriceTicket else operationState.quantity.value * productUnitPriceTicket

        val standAloneProduct = operationState.product as ProductStandAlone
        val receipt = createSaleReceipt.invoke(
            requestDate = currentInstant,
            primaryTrack = operationState.identifier.primaryTrack,
            secondaryTrack = operationState.prompts.find { it.key == PromptKey.SecondaryTrack }?.value,
            productCode = productState.code,
            productName = standAloneProduct.name,
            productUnitPrice = productUnitPriceTicket,
            inputType = when (operationState.quantity.inputType) {
                Quantity.InputType.Quantity -> ReceiptProductInputType.Quantity
                Quantity.InputType.Amount -> ReceiptProductInputType.Amount
            },
            response = nativeResponse,
            quantity = productQuantityCalculated,
            amount = productAmountCalculatedTicket,
            batchId = batchId,
        )

        Log.i(TAG, "Standalone sale: created receipt #${receipt.id}")

        operationStateRepository.updateState {
            it.copy(
                product = (it.product as ProductStandAlone).copy(
                    unitPrice = productUnitPrice
                ),
                receipt = receipt
            )
        }

        return TransactionResult.Success(nativeResponse.authorizationCode.orEmpty())
    }

    private fun evaluatePromptsAtionetFixed(
        operationState: SaleOperationState,
        configuration: Configuration
    ): PromptsResult {
        val ationetPromptsAccepted = mutableListOf<Prompt>()

        with(configuration.ationet) {
            if (promptAttendantIdentification) {
                ationetPromptsAccepted.add(
                    Prompt(
                        key = PromptKey.AttendantId,
                        state = Prompt.PromptState.Pending,
                        type = Prompt.PromptType.Attendant
                    )
                )
            }
            if (promptOdometer) {
                ationetPromptsAccepted.add(
                    Prompt(
                        key = PromptKey.Odometer,
                        state = Prompt.PromptState.Pending,
                        type = Prompt.PromptType.Numeric
                    )
                )
            }
            if (promptEngineHours) {
                ationetPromptsAccepted.add(
                    Prompt(
                        key = PromptKey.EngineHours,
                        state = Prompt.PromptState.Pending,
                        type = Prompt.PromptType.Numeric
                    )
                )
            }
            if (promptDriverIdentification) {
                ationetPromptsAccepted.add(
                    Prompt(
                        key = PromptKey.DriverId,
                        state = Prompt.PromptState.Pending,
                        type = Prompt.PromptType.Alphanumeric
                    )
                )
            }
            if (promptVehicleIdentification) {
                ationetPromptsAccepted.add(
                    Prompt(
                        key = PromptKey.VehicleId,
                        state = Prompt.PromptState.Pending,
                        type = Prompt.PromptType.Alphanumeric
                    )
                )
            }
            if (ationetVisionVehicleId) {
                ationetPromptsAccepted.add(
                    Prompt(
                        key = PromptKey.AtionetVisionVehicleId,
                        state = Prompt.PromptState.Pending,
                        type = Prompt.PromptType.VisionRecognition
                    )
                )
            }
            if (promptMiscellaneous) {
                ationetPromptsAccepted.add(
                    Prompt(
                        key = PromptKey.Miscellaneous,
                        state = Prompt.PromptState.Pending,
                        type = Prompt.PromptType.Alphanumeric
                    )
                )
            }
            if (promptTrailer) {
                ationetPromptsAccepted.add(
                    Prompt(
                        key = PromptKey.TrailerNumber,
                        state = Prompt.PromptState.Pending,
                        type = Prompt.PromptType.Numeric
                    )
                )
            }
            if (promptTruckUnit) {
                ationetPromptsAccepted.add(
                    Prompt(
                        key = PromptKey.TruckUnitNumber,
                        state = Prompt.PromptState.Pending,
                        type = Prompt.PromptType.Numeric
                    )
                )
            }
            if (promptPrimaryPIN) {
                ationetPromptsAccepted.add(
                    Prompt(
                        key = PromptKey.PrimaryPin,
                        state = Prompt.PromptState.Pending,
                        type = Prompt.PromptType.Pin
                    )
                )
            }
            if (promptSecondaryTrack) {
                ationetPromptsAccepted.add(
                    Prompt(
                        key = PromptKey.SecondaryTrack,
                        state = Prompt.PromptState.Pending,
                        type = Prompt.PromptType.Identifier
                    )
                )
            }
            if (promptSecondaryPIN) {
                ationetPromptsAccepted.add(
                    Prompt(
                        key = PromptKey.SecondaryPin,
                        state = Prompt.PromptState.Pending,
                        type = Prompt.PromptType.Pin
                    )
                )
            }
        }

        val prompts = operationState.prompts

        if (ationetPromptsAccepted.isEmpty()) {
            return PromptsResult.AllPromptsCompleted
        }

        if (prompts.isEmpty()) {
            operationStateRepository.updateState {
                it.copy(prompts = ationetPromptsAccepted)
            }
            return PromptsResult.IncompletePrompts(ationetPromptsAccepted)
        }

        return if (prompts.all { it.state == Prompt.PromptState.Completed }) {
            PromptsResult.AllPromptsCompleted
        } else {
            PromptsResult.IncompletePrompts(prompts.filter { it.state == Prompt.PromptState.Pending })
        }
    }

    private fun evaluatePromptsAtionetResponse(
        operationState: SaleOperationState,
        response: NativeRequest
    ): PromptsResult {
        val ationetPrompts = mutableListOf<Prompt>()

        response.customerData?.let { customerData ->
            customerData.forEach { (key, value) ->
                when (key) {
                    PromptRequestKey.Odometer -> ationetPrompts.add(
                        Prompt(
                            key = PromptKey.Odometer,
                            value = value,
                            state = Prompt.PromptState.Pending,
                            type = Prompt.PromptType.Numeric
                        )
                    )

                    PromptRequestKey.EngineHours -> ationetPrompts.add(
                        Prompt(
                            key = PromptKey.EngineHours,
                            value = value,
                            state = Prompt.PromptState.Pending,
                            type = Prompt.PromptType.Numeric
                        )
                    )

                    PromptRequestKey.DriverId -> ationetPrompts.add(
                        Prompt(
                            key = PromptKey.DriverId,
                            value = value,
                            state = Prompt.PromptState.Pending,
                            type = Prompt.PromptType.Alphanumeric
                        )
                    )

                    PromptRequestKey.VehicleId -> ationetPrompts.add(
                        Prompt(
                            key = PromptKey.VehicleId,
                            value = value,
                            state = Prompt.PromptState.Pending,
                            type = Prompt.PromptType.Alphanumeric
                        )
                    )

                     PromptRequestKey.AtionetVisionVehicleId -> ationetPrompts.add(
                        Prompt(
                            key = PromptKey.AtionetVisionVehicleId,
                            value = value,
                            state = Prompt.PromptState.Pending,
                            type = Prompt.PromptType.VisionRecognition
                        )
                    )
                    PromptRequestKey.Miscellaneous -> ationetPrompts.add(
                        Prompt(
                            key = PromptKey.Miscellaneous,
                            value = value,
                            state = Prompt.PromptState.Pending,
                            type = Prompt.PromptType.Alphanumeric
                        )
                    )

                    PromptRequestKey.TrailerNumber -> ationetPrompts.add(
                        Prompt(
                            key = PromptKey.TrailerNumber,
                            value = value,
                            state = Prompt.PromptState.Pending,
                            type = Prompt.PromptType.Numeric
                        )
                    )

                    PromptRequestKey.TruckUnitNumber -> ationetPrompts.add(
                        Prompt(
                            key = PromptKey.TruckUnitNumber,
                            value = value,
                            state = Prompt.PromptState.Pending,
                            type = Prompt.PromptType.Numeric
                        )
                    )

                    PromptRequestKey.PrimaryPin -> ationetPrompts.add(
                        Prompt(
                            key = PromptKey.PrimaryPin,
                            value = value,
                            state = Prompt.PromptState.Pending,
                            type = Prompt.PromptType.Pin
                        )
                    )

                    PromptRequestKey.SecondaryTrack -> ationetPrompts.add(
                        Prompt(
                            key = PromptKey.SecondaryTrack,
                            value = value,
                            state = Prompt.PromptState.Pending,
                            type = Prompt.PromptType.Identifier
                        )
                    )

                    PromptRequestKey.SecondaryPin -> ationetPrompts.add(
                        Prompt(
                            key = PromptKey.SecondaryPin,
                            value = value,
                            state = Prompt.PromptState.Pending,
                            type = Prompt.PromptType.Pin
                        )
                    )
                }
            }
        }

        val promptsState = operationState.prompts
        val promptsActive = ationetPrompts.filter { it.value?.uppercase() == "TRUE" }
        val promptsPending =
            promptsActive.filterNot { promptsState.find { promptState -> promptState.key == it.key } != null }

        if (promptsPending.isEmpty() || promptsPending.all { it.state == Prompt.PromptState.Completed }) {
            return PromptsResult.AllPromptsCompleted
        } else {
            operationStateRepository.updateState {
                it.copy(prompts = (promptsState + promptsPending))
            }
            return PromptsResult.IncompletePrompts(promptsPending)
        }
    }

    fun customerData(operationState: SaleOperationState): Map<String, String?>? {
        val promptKeysFilter = listOf(
            PromptKey.PrimaryPin,
            PromptKey.SecondaryTrack,
            PromptKey.SecondaryPin,
            PromptKey.AttendantId
        )
        val prompts = operationState.prompts.filterNot { promptKeysFilter.contains(it.key) }

        return prompts.ifEmpty {
            return null
        }.associate {
            it.key to it.value
        }
    }

    private fun dealerData(operationState: SaleOperationState): DealerData? {
        return operationState.prompts.find { it.key == PromptKey.AttendantId }?.let {
            return DealerData(
                attendantCode = it.value.orEmpty()
            )
        }
    }

    sealed interface PromptsResult {
        data class IncompletePrompts(val prompts: List<Prompt>) : PromptsResult
        data object AllPromptsCompleted : PromptsResult
    }
}