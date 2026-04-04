package com.ationet.androidterminal.standalone.completion.presentation.identification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ationet.androidterminal.core.domain.hal.HALBuzzer
import com.ationet.androidterminal.core.domain.hal.card_reader.CardReader
import com.ationet.androidterminal.core.domain.hal.card_reader.CardReaderEvent
import com.ationet.androidterminal.core.domain.use_case.hardware.ReadCardUseCase
import com.ationet.androidterminal.core.presentation.util.IdentificationType
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

@HiltViewModel
class CompletionIdentificationViewModel @Inject constructor(
    private val readCardUseCase: ReadCardUseCase,
    private val buzzer: HALBuzzer
) : ViewModel() {
    private val _state: MutableStateFlow<IdentificationEntryState> = MutableStateFlow(
        IdentificationEntryState.SwipeInsertScanIdentification
    )

    val state: StateFlow<IdentificationEntryState> get() = _state.asStateFlow()

    init {
        scanCards()
    }

    private fun scanCards() {
        _state.update { IdentificationEntryState.SwipeInsertScanIdentification }

        viewModelScope.launch {
            Log.d(TAG, "Started card read")
            readCardUseCase.invoke()
                .onEach { cardReaderEvent ->
                    Log.d(TAG, "Received card reader event: $cardReaderEvent")
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
        Log.d(TAG, "Cancelled card read from HAL")
        viewModelScope.coroutineContext.cancelChildren()
    }

    fun requestManualEntry() {
        Log.d(TAG, "Requesting card manual entry")
        cancelCardRead()

        _state.update { IdentificationEntryState.ManualIdentification }
    }

    fun requestCameraScan() {
        Log.d(TAG, "Requesting card camera scan")
        cancelCardRead()

        _state.update { IdentificationEntryState.CameraScanIdentification }
    }

    fun retryCardRead() {
        Log.d(TAG, "Retrying card read...")
        scanCards()
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
            _state.update { IdentificationEntryState.IdentifierPresented(navigate = true, identifier = identifier) }
        }
    }

    fun playBeep(){
        buzzer.beep(1.seconds)
    }

    private companion object {
        private const val TAG: String = "CompletionIdentificationVM"
    }
}

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

    data class IdentifierPresented(
        val navigate: Boolean,
        val identifier: String
        ) : IdentificationEntryState

    data object CameraScanIdentification : IdentificationEntryState
    data object ManualIdentification : IdentificationEntryState

    data class ScanCompleted(
        val identificationType: IdentificationType,
        val identifier: String,
    ) : IdentificationEntryState
}