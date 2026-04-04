package com.ationet.androidterminal.standalone.sale.presentation.identification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atio.log.Logger
import com.atio.log.util.debug
import com.ationet.androidterminal.core.domain.hal.HALBuzzer
import com.ationet.androidterminal.core.domain.hal.card_reader.CardReader
import com.ationet.androidterminal.core.domain.hal.card_reader.CardReaderEvent
import com.ationet.androidterminal.core.domain.use_case.hardware.ReadCardUseCase
import com.ationet.androidterminal.core.presentation.util.IdentificationType
import com.ationet.androidterminal.standalone.sale.data.local.SaleOperationStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

sealed interface IdentificationEntryState {
    data object SwipeInsertScanIdentification : IdentificationEntryState
    data class SwipeInsertScanInProgress(
        val identificationType: IdentificationType
    ) : IdentificationEntryState

    data class SwipeInsertScanDone(
        val identificationType: IdentificationType,
    ) : IdentificationEntryState

    data object SwipeInsertScanError : IdentificationEntryState
    data class SwipeInsertScanEmpty(val isQrScan:Boolean) : IdentificationEntryState
    data object SwipeInsertScanTimeout : IdentificationEntryState

    data class IdentifierPresented(val navigate: Boolean) : IdentificationEntryState

    data object CameraScanIdentification : IdentificationEntryState
    data object ManualIdentification : IdentificationEntryState

    data class ScanCompleted(
        val identificationType: IdentificationType,
        val identifier: String,
    ) : IdentificationEntryState
}

@HiltViewModel
class IdentificationViewModel @Inject constructor(
    private val readCardUseCase: ReadCardUseCase,
    private val operationStateRepository: SaleOperationStateRepository,
    private val buzzer: HALBuzzer
) : ViewModel() {
    private val _state: MutableStateFlow<IdentificationEntryState> = MutableStateFlow(IdentificationEntryState.SwipeInsertScanIdentification)
    val state: StateFlow<IdentificationEntryState> get() = _state.asStateFlow()

    private companion object {
        private const val TAG: String = "PreAuthIdentificationViewModel"
        private val logger = Logger(TAG)
    }

    init {
        scanCards()
    }

    private fun scanCards() {
        _state.update { IdentificationEntryState.SwipeInsertScanIdentification }

        viewModelScope.launch {
            logger.debug("Started card read")
            readCardUseCase.invoke()
                .onEach { cardReaderEvent ->
                    logger.debug("Received card reader event: $cardReaderEvent")
                }
                .collect { cardReaderEvent ->
                    when (cardReaderEvent) {
                        is CardReaderEvent.CardReadBegin -> {
                            _state.update {
                                IdentificationEntryState.SwipeInsertScanInProgress(
                                    identificationType = identificationTypeByCardReader(
                                        cardReaderEvent.reader
                                    )
                                )
                            }
                        }

                        is CardReaderEvent.CardWaitingToRemove -> {
                            _state.update {
                                playBeep()
                                IdentificationEntryState.SwipeInsertScanDone(
                                    identificationType = identificationTypeByCardReader(
                                        cardReaderEvent.reader
                                    )
                                )
                            }
                        }

                        is CardReaderEvent.CardRead -> {
                            _state.update {
                                IdentificationEntryState.ScanCompleted(
                                    identifier = cardReaderEvent.card,
                                    identificationType = identificationTypeByCardReader(
                                        cardReaderEvent.reader
                                    )
                                )
                            }
                        }

                        CardReaderEvent.ReaderTimeout -> {
                            _state.update {
                                IdentificationEntryState.SwipeInsertScanTimeout
                            }
                        }

                        is CardReaderEvent.CardReadError -> {
                            _state.update {
                                IdentificationEntryState.SwipeInsertScanError
                            }
                        }
                    }
                }
        }
    }

    private fun cancelCardRead() {
        logger.debug("Cancelled card read from HAL")
        viewModelScope.coroutineContext.cancelChildren()
    }

    fun requestManualEntry() {
        logger.debug("Requesting card manual entry")
        cancelCardRead()

        _state.update { IdentificationEntryState.ManualIdentification }
    }

    fun requestCameraScan() {
        logger.debug("Requesting card camera scan")
        cancelCardRead()

        _state.update { IdentificationEntryState.CameraScanIdentification }
    }

    fun retryCardRead() {
        logger.debug("Retrying card read...")
        scanCards()
    }

    fun resetState() {
        _state.update { IdentificationEntryState.SwipeInsertScanIdentification }
    }

    private fun identificationTypeByCardReader(reader: CardReader) =
        when (reader) {
            CardReader.MagneticStripeCard -> IdentificationType.Magnetic
            CardReader.SmartCard -> IdentificationType.Chip
            CardReader.RfCard -> IdentificationType.Nfc
        }

    fun setIdentification(identifier: String) {
        if(_state.value is IdentificationEntryState.CameraScanIdentification){
            playBeep()
        }
        if (identifier.isBlank()) {
            if (_state.value is IdentificationEntryState.CameraScanIdentification) {
                _state.update { IdentificationEntryState.SwipeInsertScanEmpty(true) }
            } else {
                _state.update { IdentificationEntryState.SwipeInsertScanEmpty(false) }
            }
        } else {
            operationStateRepository.updateState {
                it.copy(
                    identifier = it.identifier.copy(
                        primaryTrack = identifier
                    )
                )
            }
            _state.update { IdentificationEntryState.IdentifierPresented(true) }
        }
    }

    fun setNewStateOperation() {
        operationStateRepository.clear()
    }

    fun playBeep(){
        buzzer.beep(1.seconds)
    }
}