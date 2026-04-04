package com.ationet.androidterminal.standalone.preauthorization.domain.use_case

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.atio.log.Logger
import com.atio.log.util.error
import com.ationet.androidterminal.core.data.local.preauthorization.PreAuthorizationStandAloneRepository
import com.ationet.androidterminal.core.data.remote.ationet.CurrencyCodeARS
import com.ationet.androidterminal.core.data.remote.ationet.UnitCodeLiters
import com.ationet.androidterminal.core.data.remote.ationet.model.DealerData
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.data.remote.ationet.model.PromptKey
import com.ationet.androidterminal.core.data.remote.ationet.model.PromptRequestKey
import com.ationet.androidterminal.core.data.remote.ationet.model.ResponseCodes
import com.ationet.androidterminal.core.domain.exception.HostUrlNotConfiguredException
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.model.preauthorization.Authorization
import com.ationet.androidterminal.core.domain.model.preauthorization.CustomerData
import com.ationet.androidterminal.core.domain.model.preauthorization.Identification
import com.ationet.androidterminal.core.domain.model.preauthorization.InputType
import com.ationet.androidterminal.core.domain.model.preauthorization.OriginalData
import com.ationet.androidterminal.core.domain.model.preauthorization.PreAuthorization
import com.ationet.androidterminal.core.domain.model.preauthorization.PreAuthorizationStandalone
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptData
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptModifierClass
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptModifierType
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptProductInputType
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionTypeName
import com.ationet.androidterminal.core.domain.model.receipt.TransactionModifier
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestPreAuthorization
import com.ationet.androidterminal.core.domain.use_case.batch.GetLastOpenBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.core.domain.util.createCommunicationErrorReceipt
import com.ationet.androidterminal.core.domain.util.createErrorReceipt
import com.ationet.androidterminal.core.domain.util.createReceipt
import com.ationet.androidterminal.core.domain.util.displayResponseText
import com.ationet.androidterminal.core.util.LocalDateTimeUtils
import com.ationet.androidterminal.core.util.receiptData
import com.ationet.androidterminal.standalone.preauthorization.data.local.PreAuthorizationOperationStateRepository
import com.ationet.androidterminal.standalone.preauthorization.domain.model.PreAuthorizationOperationState
import com.ationet.androidterminal.standalone.preauthorization.domain.model.ProductStandAlone
import com.ationet.androidterminal.standalone.preauthorization.domain.model.Prompt
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
class SendPreAuthorization @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getConfiguration: GetConfiguration,
    private val operationStateRepository: PreAuthorizationOperationStateRepository,
    private val requestPreAuthorization: RequestPreAuthorization,
    private val preAuthorizationStandAloneRepository: PreAuthorizationStandAloneRepository,
    private val getLastOpenBatchUseCase: GetLastOpenBatchUseCase
) {
    val configuration = getConfiguration()

    private companion object {
        private const val TAG: String = "SendPreAuthorizationStandAlone"
        val logger = Logger(TAG)
    }

    @SuppressLint("LongLogTag")
    suspend operator fun invoke(): TransactionResult {
        val operationState = operationStateRepository.getState()
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

        val product = operationState.product as ProductStandAlone
        val currentInstant = Clock.System.now()

        val response = try {
            requestPreAuthorization.invoke(
                productCode = product.code,
                unitPrice = product.unitPrice,
                productAmount = if (operationState.quantity.inputType == InputType.Amount) operationState.quantity.value else null,
                productQuantity = if (operationState.quantity.inputType == InputType.Quantity) operationState.quantity.value else null,
                primaryTrack = operationState.identifier.primaryTrack,
                primaryPin = operationState.prompts.find { it.key == PromptKey.PrimaryPin }?.value,
                secondaryTrack = operationState.prompts.find { it.key == PromptKey.SecondaryTrack }?.value,
                secondaryPin = operationState.prompts.find { it.key == PromptKey.SecondaryPin }?.value,
                manualEntry = false,
                customerData = customerData(operationState),
                localDateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
                unitCode = configuration.fuelMeasureUnit.ifEmpty { UnitCodeLiters },
                currencyCode = configuration.currencyCode.ifEmpty { CurrencyCodeARS },
                dealerData = dealerData(operationState)
            )
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            logger.error("Error requesting pre-authorization", e)

            val receipt = createCommunicationErrorReceipt(
                transactionName = ReceiptTransactionTypeName.PreAuthorization,
                context = context,
                configuration = configuration,
                currentInstant = currentInstant,
                primaryTrack = operationState.identifier.primaryTrack,
                secondaryTrack = operationState.prompts.find { it.key == PromptKey.SecondaryTrack }?.value,
                inputType = when (operationState.quantity.inputType) {
                    InputType.Quantity -> ReceiptProductInputType.Quantity
                    InputType.Amount -> ReceiptProductInputType.Amount
                    InputType.FillUp -> ReceiptProductInputType.FillUp
                },
                productName = operationState.product.name,
                productCode = operationState.product.code,
                productUnitPrice = operationState.product.unitPrice,
                quantity = if (operationState.quantity.inputType == InputType.Amount) operationState.quantity.value else null,
                amount = if (operationState.quantity.inputType == InputType.Amount) operationState.quantity.value else null,
                batchId = batchId
            )

            operationStateRepository.updateState {
                it.copy(
                    receipt = receipt
                )
            }

            return TransactionResult.CommunicationError
        }

        if (response.isFailure) {
            val exception = response.exceptionOrNull()
            if (exception is TerminalIdNotConfiguredException || exception is HostUrlNotConfiguredException) {
                val receipt = createErrorReceipt(
                    transactionName = ReceiptTransactionTypeName.PreAuthorization,
                    configuration = configuration,
                    currentInstant = currentInstant,
                    primaryTrack = operationState.identifier.primaryTrack,
                    secondaryTrack = operationState.prompts.find { it.key == PromptKey.SecondaryTrack }?.value,
                    inputType = when (operationState.quantity.inputType) {
                        InputType.Quantity -> ReceiptProductInputType.Quantity
                        InputType.Amount -> ReceiptProductInputType.Amount
                        InputType.FillUp -> ReceiptProductInputType.FillUp
                    },
                    productName = operationState.product.name,
                    productCode = operationState.product.code,
                    productUnitPrice = operationState.product.unitPrice,
                    quantity = if (operationState.quantity.inputType == InputType.Amount) operationState.quantity.value else null,
                    amount = if (operationState.quantity.inputType == InputType.Amount) operationState.quantity.value else null,
                    responseCode = null,
                    responseText = exception.message.orEmpty(),
                    receiptData = ReceiptData(),
                    batchId = batchId,
                    authorizationCode = null,
                    invoiceNumber = null
                )

                operationStateRepository.updateState {
                    it.copy(
                        receipt = receipt
                    )
                }

                logger.error(exception.message.orEmpty())

                return TransactionResult.Error(message = exception.message.orEmpty())
            }

            val receipt = createCommunicationErrorReceipt(
                transactionName = ReceiptTransactionTypeName.PreAuthorization,
                context = context,
                configuration = configuration,
                currentInstant = currentInstant,
                primaryTrack = operationState.identifier.primaryTrack,
                secondaryTrack = operationState.prompts.find { it.key == PromptKey.SecondaryTrack }?.value,
                inputType = when (operationState.quantity.inputType) {
                    InputType.Quantity -> ReceiptProductInputType.Quantity
                    InputType.Amount -> ReceiptProductInputType.Amount
                    InputType.FillUp -> ReceiptProductInputType.FillUp
                },
                productName = operationState.product.name,
                productCode = operationState.product.code,
                productUnitPrice = operationState.product.unitPrice,
                quantity = if (operationState.quantity.inputType == InputType.Amount) operationState.quantity.value else null,
                amount = if (operationState.quantity.inputType == InputType.Amount) operationState.quantity.value else null,
                batchId = batchId
            )

            operationStateRepository.updateState {
                it.copy(
                    receipt = receipt
                )
            }

            TransactionResult.CommunicationError
        }

        val nativeResponse = response.getOrNull()

        if (nativeResponse == null) {
            val receipt = createCommunicationErrorReceipt(
                transactionName = ReceiptTransactionTypeName.PreAuthorization,
                context = context,
                configuration = configuration,
                currentInstant = currentInstant,
                primaryTrack = operationState.identifier.primaryTrack,
                secondaryTrack = operationState.prompts.find { it.key == PromptKey.SecondaryTrack }?.value,
                inputType = when (operationState.quantity.inputType) {
                    InputType.Quantity -> ReceiptProductInputType.Quantity
                    InputType.Amount -> ReceiptProductInputType.Amount
                    InputType.FillUp -> ReceiptProductInputType.FillUp
                },
                productName = operationState.product.name,
                productCode = operationState.product.code,
                productUnitPrice = operationState.product.unitPrice,
                quantity = if (operationState.quantity.inputType == InputType.Amount) operationState.quantity.value else null,
                amount = if (operationState.quantity.inputType == InputType.Amount) operationState.quantity.value else null,
                batchId = batchId
            )

            operationStateRepository.updateState {
                it.copy(
                    receipt = receipt
                )
            }

            return TransactionResult.CommunicationError
        }

        val result = evaluatePromptsAtionetResponse(operationState, nativeResponse)

        if (result is PromptsResult.IncompletePrompts) {
            return TransactionResult.RequiredPromptsAtionet(result.prompts)
        }

        val productState = operationState.product

        val productUnitPrice = if (configuration.ticket.transactionDetails) nativeResponse.companyPrice?.productUnitPriceBase ?: nativeResponse.companyPrice?.productUnitPrice else nativeResponse.companyPrice?.productUnitPrice

        val productQuantity = if (operationState.quantity.inputType == InputType.Quantity) {
            operationState.quantity.value
        } else {
            operationState.quantity.value / productState.unitPrice
        }

        val productAmount = if (operationState.quantity.inputType == InputType.Amount) {
            operationState.quantity.value
        } else {
            productState.unitPrice * operationState.quantity.value
        }

        if (nativeResponse.responseCode != ResponseCodes.Authorized) {
            val receipt = createReceipt(
                transactionName = ReceiptTransactionTypeName.PreAuthorization,
                authorizationCode = nativeResponse.authorizationCode,
                invoiceNumber = nativeResponse.invoiceNumber,
                transactionSequenceNumber = nativeResponse.transactionSequenceNumber,
                responseText = nativeResponse.displayResponseText,
                responseCode = nativeResponse.responseCode,
                configuration = configuration,
                currentInstant = currentInstant,
                primaryTrack = operationState.identifier.primaryTrack,
                secondaryTrack = operationState.prompts.find { it.key == PromptKey.SecondaryTrack }?.value,
                inputType = when (operationState.quantity.inputType) {
                    InputType.Quantity -> ReceiptProductInputType.Quantity
                    InputType.Amount -> ReceiptProductInputType.Amount
                    InputType.FillUp -> ReceiptProductInputType.FillUp
                },
                productName = operationState.product.name,
                productCode = operationState.product.code,
                productUnitPrice = productUnitPrice,
                quantity = nativeResponse.productQuantity?.toDoubleOrNull(),
                amount = nativeResponse.productAmount?.toDoubleOrNull(),
                receiptData = try {
                    receiptData(nativeResponse.receiptData)
                } catch (e: Throwable) {
                    Log.w(TAG, "Standalone pre-authorization: failed to decode receipt data. Defaulting to empty!!")
                    ReceiptData()
                },
                modifiers = nativeResponse.companyPrice?.modifiers?.map { priceModifier ->
                    TransactionModifier(
                        type = when (priceModifier.type) {
                            0 -> ReceiptModifierType.Percentage
                            1 -> ReceiptModifierType.FixedTransaction
                            else -> ReceiptModifierType.FixedUnit
                        },
                        modifierClass = when (priceModifier.modifierClass) {
                            0 -> ReceiptModifierClass.Discount
                            else -> ReceiptModifierClass.Surcharge
                        },
                        value = priceModifier.value,
                        total = priceModifier.total,
                        base = priceModifier.base
                    )
                } ?: emptyList(),
                batchId = batchId
            )

            operationStateRepository.updateState {
                it.copy(
                    receipt = receipt
                )
            }

            return TransactionResult.Error(
                code = nativeResponse.responseCode.orEmpty(),
                message = nativeResponse.displayResponseText
            )
        }

        val preAuthorization = PreAuthorization(
            createAt = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
            updatedAt = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
            identification = Identification(
                primaryTrack = operationState.identifier.primaryTrack,
                secondaryTrack = operationState.prompts.find { it.key == PromptKey.SecondaryTrack }?.value
            ),
            authorization = Authorization(
                authorizationCode = nativeResponse.authorizationCode.orEmpty(),
                transactionSequenceNumber = nativeResponse.transactionSequenceNumber?.toLong() ?: 0,
                invoice = nativeResponse.invoiceNumber.orEmpty(),
                localDateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
                authorizedAmount = nativeResponse.productAmount?.toDoubleOrNull(),
                authorizedQuantity = nativeResponse.productQuantity?.toDoubleOrNull(),
                authorizedPrice = nativeResponse.productUnitPrice?.toDoubleOrNull(),
            ),
            originalData = OriginalData(
                transactionSequenceNumber = nativeResponse.originalData?.transactionSequenceNumber?.toLong()
                    ?: 0L,
                authorizationCode = nativeResponse.originalData?.authorizationCode.orEmpty(),
                transactionCode = nativeResponse.originalData?.transactionCode.orEmpty(),
                localTransactionDate = try {
                    LocalDateTimeUtils.LocalDateFormat.parse(nativeResponse.localTransactionDate.orEmpty())
                } catch (e: Exception) {
                    null
                },
                localTransactionTime = try {
                    LocalDateTimeUtils.LocalTimeFormat.parse(nativeResponse.localTransactionTime.orEmpty())
                } catch (e: Exception) {
                    null
                },
            ),
            customerData = CustomerData(
                odometer = operationState.prompts.find { it.key == PromptKey.Odometer }?.value,
                engineHours = operationState.prompts.find { it.key == PromptKey.EngineHours }?.value,
                driverId = operationState.prompts.find { it.key == PromptKey.EngineHours }?.value,
                vehicleId = operationState.prompts.find { it.key == PromptKey.VehicleId }?.value,
                AtionetVisionVehicleId = operationState.prompts.find { it.key == PromptKey.AtionetVisionVehicleId }?.value,
                miscellaneous = operationState.prompts.find { it.key == PromptKey.Miscellaneous }?.value,
                trailerNumber = operationState.prompts.find { it.key == PromptKey.TrailerNumber }?.value,
                truckUnitNumber = operationState.prompts.find { it.key == PromptKey.TruckUnitNumber }?.value,
                attendantId = operationState.prompts.find { it.key == PromptKey.AttendantId }?.value,
                primaryPin = operationState.prompts.find { it.key == PromptKey.PrimaryPin }?.value,
                secondaryPin = operationState.prompts.find { it.key == PromptKey.SecondaryPin }?.value
            ),
            batchId = batchId,
            controllerType = configuration.controllerType
        )

        savePreAuthorizationStandAlone(
            preAuthorization = preAuthorization,
            product = operationState.product,
            inputType = operationState.quantity.inputType
        )

        val receipt = createReceipt(
            transactionName = ReceiptTransactionTypeName.PreAuthorization,
            authorizationCode = nativeResponse.authorizationCode,
            invoiceNumber = nativeResponse.invoiceNumber,
            transactionSequenceNumber = nativeResponse.transactionSequenceNumber,
            responseText = nativeResponse.displayResponseText,
            responseCode = nativeResponse.responseCode,
            configuration = configuration,
            currentInstant = currentInstant,
            primaryTrack = operationState.identifier.primaryTrack,
            secondaryTrack = operationState.prompts.find { it.key == PromptKey.SecondaryTrack }?.value,
            inputType = when (operationState.quantity.inputType) {
                InputType.Quantity -> ReceiptProductInputType.Quantity
                InputType.Amount -> ReceiptProductInputType.Amount
                InputType.FillUp -> ReceiptProductInputType.FillUp
            },
            productName = operationState.product.name,
            productCode = operationState.product.code,
            productUnitPrice = nativeResponse.productUnitPrice?.toDoubleOrNull(),
            quantity = nativeResponse.productQuantity?.toDoubleOrNull(),
            amount = nativeResponse.productAmount?.toDoubleOrNull(),
            receiptData = try {
                receiptData(nativeResponse.receiptData)
            } catch (e: Throwable) {
                Log.w(TAG, "Standalone pre-authorization: failed to decode receipt data. Defaulting to empty!!")
                ReceiptData()
            },
            modifiers = nativeResponse.companyPrice?.modifiers?.map { priceModifier ->
                TransactionModifier(
                    type = when (priceModifier.type) {
                        0 -> ReceiptModifierType.Percentage
                        1 -> ReceiptModifierType.FixedTransaction
                        else -> ReceiptModifierType.FixedUnit
                    },
                    modifierClass = when (priceModifier.modifierClass) {
                        0 -> ReceiptModifierClass.Discount
                        else -> ReceiptModifierClass.Surcharge
                    },
                    value = priceModifier.value,
                    total = priceModifier.total,
                    base = priceModifier.base
                )
            } ?: emptyList(),
            batchId = batchId
        )

        operationStateRepository.updateState {
            it.copy(
                authorizationData = it.authorizationData.copy(
                    authorizationCode = nativeResponse.authorizationCode.orEmpty(),
                    amount = nativeResponse.productAmount?.toDoubleOrNull(),
                    quantity = nativeResponse.productQuantity?.toDoubleOrNull(),
                    unitPrice = nativeResponse.productUnitPrice?.toDoubleOrNull(),
                ),
                receipt = receipt
            )
        }

        return TransactionResult.Success(nativeResponse.authorizationCode.orEmpty())

    }

    private fun dealerData(operationState: PreAuthorizationOperationState): DealerData? {
        return operationState.prompts.find { it.key == PromptKey.AttendantId }?.let {
            return DealerData(
                attendantCode = it.value.orEmpty()
            )
        }
    }

    private suspend fun savePreAuthorizationStandAlone(
        preAuthorization: PreAuthorization,
        product: ProductStandAlone,
        inputType: InputType,
    ) {
        preAuthorizationStandAloneRepository.create(
            PreAuthorizationStandalone(
                preAuthorization = preAuthorization,
                productCode = product.code,
                productName = product.name,
                productUnitPrice = preAuthorization.authorization.authorizedPrice ?: product.unitPrice,
                inputType = inputType,
                controllerType = configuration.controllerType
            )
        )
    }

    private fun evaluatePromptsAtionetFixed(
        operationState: PreAuthorizationOperationState,
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
        operationState: PreAuthorizationOperationState,
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

    fun customerData(operationState: PreAuthorizationOperationState): Map<String, String?>? {
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


    sealed interface PromptsResult {
        data class IncompletePrompts(val prompts: List<Prompt>) : PromptsResult
        data object AllPromptsCompleted : PromptsResult
    }
}