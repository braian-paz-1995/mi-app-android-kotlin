package com.ationet.androidterminal.core.change_pin.presentation.pin_prompt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atio.log.Logger
import com.ationet.androidterminal.core.change_pin.data.local.ChangePinOperationStateRepository
import com.ationet.androidterminal.core.change_pin.domain.error.ChangePinError
import com.ationet.androidterminal.core.change_pin.domain.use_case.ChangePinResult
import com.ationet.androidterminal.core.change_pin.domain.use_case.ExecuteChangePinUseCase
import com.ationet.androidterminal.core.change_pin.domain.use_case.ValidatePin
import com.ationet.androidterminal.core.change_pin.domain.util.ValidatePinResult
import com.ationet.androidterminal.core.domain.model.LoadingState
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

sealed interface PinPromptEntryState {
    data object CurrentPin : PinPromptEntryState
    data object ConfirmPin : PinPromptEntryState
    data object ReConfirmPin : PinPromptEntryState
    data object Loading : PinPromptEntryState
    data class TransactionProcessOk(
        val authorizationCode: String
    ) : PinPromptEntryState

    data class TransactionProcessError(
        val message: String,
    ) : PinPromptEntryState

    data object CommunicationError : PinPromptEntryState
}

data class PinPromptState(
    val currentPin: String = "",
    val currentPinVisibility: Boolean = false,
    val currentPinError: ChangePinError? = null,
    val confirmPin: String = "",
    val confirmPinVisibility: Boolean = false,
    val confirmPinError: ChangePinError? = null,
    val reConfirmPin: String = "",
    val reConfirmPinVisibility: Boolean = false,
    val reConfirmPinError: ChangePinError? = null,
    val goToReConfirmPin: Boolean = false,
    val loadingState: LoadingState? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class PinPromptViewModel @Inject constructor(
    private val validatePin: ValidatePin,
    private val executeChangePinUseCase: ExecuteChangePinUseCase,
    private val operationStateRepository: ChangePinOperationStateRepository,
    private val configurationUseCase: ConfigurationUseCase
) : ViewModel() {
    private val _navigationState =
        MutableStateFlow<PinPromptEntryState>(PinPromptEntryState.CurrentPin)
    val navigationState: StateFlow<PinPromptEntryState> = _navigationState

    private val _state = MutableStateFlow(PinPromptState())
    val state: StateFlow<PinPromptState> = _state

    val configuration = configurationUseCase.getConfiguration()

    fun onAction(action: PinPromptAction) {

        when (action) {
            is PinPromptAction.OnCurrentPinChange -> _state.update { currentState ->
                currentState.copy(
                    currentPin = action.pin,
                    confirmPinError = null
                )
            }

            is PinPromptAction.OnConfirmPinChange -> _state.update { currentState ->
                currentState.copy(
                    confirmPin = action.pin,
                    confirmPinError = null
                )
            }

            is PinPromptAction.OnReConfirmPinChange -> _state.update { currentState ->
                currentState.copy(
                    reConfirmPin = action.pin,
                    reConfirmPinError = null
                )
            }

            PinPromptAction.OnToggleVisibilityCurrentPin -> _state.update { currentState ->
                currentState.copy(
                    currentPinVisibility = !currentState.currentPinVisibility
                )
            }

            PinPromptAction.OnToggleVisibilityConfirmPin -> _state.update { currentState ->
                currentState.copy(
                    confirmPinVisibility = !currentState.confirmPinVisibility
                )
            }

            PinPromptAction.OnToggleVisibilityReConfirmPin -> _state.update { currentState ->
                currentState.copy(
                    reConfirmPinVisibility = !currentState.reConfirmPinVisibility
                )
            }

            PinPromptAction.OnNext -> {
                when (navigationState.value) {
                    is PinPromptEntryState.CurrentPin -> {
                        _state.update { it.copy(
                            currentPinVisibility = false,
                            confirmPinVisibility = false,
                            reConfirmPinVisibility = false
                        ) }

                        validateCurrentPin(_state.value.currentPin)
                    }

                    is PinPromptEntryState.ConfirmPin -> {
                        _state.update { it.copy(
                            currentPinVisibility = false,
                            confirmPinVisibility = false,
                            reConfirmPinVisibility = false
                        ) }

                        validateConfirmPin(_state.value.confirmPin)
                    }

                    else -> throw IllegalStateException("Unhandled navigation state: ${navigationState.value}")
                }
            }

            PinPromptAction.OnSubmit -> {
                _state.update { it.copy(reConfirmPinVisibility = false) }

                validateNewPin(
                    confirmPin = _state.value.confirmPin,
                    reConfirmPin = _state.value.reConfirmPin
                )
            }

            PinPromptAction.OnBack -> {
                _navigationState.update { currentState ->
                    when (currentState) {
                        is PinPromptEntryState.ConfirmPin -> PinPromptEntryState.CurrentPin
                        is PinPromptEntryState.ReConfirmPin -> PinPromptEntryState.ConfirmPin
                        else -> currentState
                    }
                }
            }

            else -> {}
        }
    }


    private fun validateCurrentPin(
        currentPin: String
    ) {
        when (val result =
            validatePin(currentPin = currentPin, confirmPin = null, reConfirmPin = null)) {
            is ValidatePinResult.Failure -> _state.update { currentState ->
                currentState.copy(currentPinError = result.error)
            }

            ValidatePinResult.Success -> _navigationState.update {
                PinPromptEntryState.ConfirmPin
            }
        }
    }

    private fun validateConfirmPin(
        confirmPin: String
    ) {
        when (val result =
            validatePin(
                currentPin = _state.value.currentPin,
                confirmPin = confirmPin,
                reConfirmPin = null
            )) {
            is ValidatePinResult.Failure -> _state.update { currentState ->
                currentState.copy(confirmPinError = result.error)
            }

            ValidatePinResult.Success -> _navigationState.update {
                PinPromptEntryState.ReConfirmPin
            }
        }
    }

    private fun validateNewPin(
        confirmPin: String,
        reConfirmPin: String?
    ) {
        when (val result =
            validatePin(currentPin = null, confirmPin = confirmPin, reConfirmPin = reConfirmPin)) {
            is ValidatePinResult.Failure -> _state.update { currentState ->
                currentState.copy(reConfirmPinError = result.error)
            }

            ValidatePinResult.Success -> {
                _navigationState.update { PinPromptEntryState.Loading }

                try {
                    viewModelScope.launch {
                        _state.update { currentState ->
                            currentState.copy(loadingState = LoadingState.Loading)
                        }
                        delay(3.seconds)
                        changePinRequest(confirmPin)
                    }
                } catch (e: Exception) {
                    Logger.error(TAG, e) { append("Error changing pin") }
                    _state.update { currentState ->
                        currentState.copy(loadingState = LoadingState.Failure)
                    }
                }
            }
        }
    }

    private fun changePinRequest(newPin: String) {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(loadingState = LoadingState.Loading)
            }

            val currentInstant = Clock.System.now()

            val result = executeChangePinUseCase.invoke(
                primaryPin = _state.value.currentPin,
                newPin = newPin,
                confirmationPin = newPin,
                currentInstant = currentInstant
            )

            when (result) {
                ChangePinResult.CommunicationError -> {
                    _state.update {
                        it.copy(loadingState = LoadingState.Failure)
                    }

                    delay(2.seconds)

                    /* Emit navigation request */
                    _navigationState.update { PinPromptEntryState.CommunicationError }
                }

                is ChangePinResult.Failure -> {
                    _state.update {
                        it.copy(loadingState = LoadingState.Failure)
                    }

                    delay(2.seconds)

                    val localDateTime = getOperationLocalDateTime(currentInstant, result.date, result.time)

                    operationStateRepository.updateState {
                        it.copy(
                            dateTime = localDateTime,
                            authorizationCode = result.authorizationCode.orEmpty(),
                            code = result.code,
                            message = result.message.orEmpty(),
                        )
                    }

                    /* Emit navigation request */
                    _navigationState.update {
                        PinPromptEntryState.TransactionProcessError(
                            message = result.message.orEmpty(),
                        )
                    }
                }

                is ChangePinResult.Success -> {
                    _state.update {
                        it.copy(loadingState = LoadingState.Success)
                    }
                    delay(2.seconds)

                    val localDateTime = getOperationLocalDateTime(currentInstant, result.date, result.time)

                    /* Save result on operation state repository */
                    operationStateRepository.updateState {
                        it.copy(
                            dateTime = localDateTime,
                            authorizationCode = result.authorizationCode.orEmpty(),
                        )
                    }

                    /* Emit navigation request */
                    _navigationState.update {
                        PinPromptEntryState.TransactionProcessOk(
                            authorizationCode = result.authorizationCode.orEmpty()
                        )
                    }
                }
            }
        }
    }

    private fun getOperationLocalDateTime(instant: Instant, date: String?, time: String?): LocalDateTime {
        if(date.isNullOrBlank() || time.isNullOrBlank()) {
            return instant.toLocalDateTime(TimeZone.currentSystemDefault())
        }

        val coercedDate = date.padStart(8, '0')
        val coercedTime = time.padStart(6, '0')

        val localDate = LocalDate.Format {
            /* Year in four digit format, padded with zeroes */
            year()
            /* Month in two digit format, padded with zeroes */
            monthNumber()
            /* Day of month in two digit format, padded with zeroes */
            dayOfMonth()
        }.parse(coercedDate)

        val localTime = LocalTime.Format {
            /* Hour in two digit format, padded with zeroes */
            hour()
            /* Minute in two digit format, padded with zeroes */
            minute()
            /* Second in two digit format, padded with zeroes */
            second()
        }.parse(coercedTime)

        return LocalDateTime(localDate, localTime)
    }


    companion object {
        private const val TAG = "PinPromptViewModel"
    }
}