package com.ationet.androidterminal.hal.card_reader

import android.util.Log
import com.ationet.androidterminal.core.domain.hal.card_reader.CardReader
import com.ationet.androidterminal.core.domain.hal.card_reader.CardReaderEvent
import com.ationet.androidterminal.core.domain.hal.card_reader.HALCardReader
import com.ationet.androidterminal.core.domain.hal.card_reader.NDefCardReader
import com.ationet.androidterminal.hal.card_reader.ic_card.IcCardReader
import com.ationet.androidterminal.hal.card_reader.rf_card.M0NfcReader
import com.ationet.androidterminal.hal.card_reader.rf_card.M1NfcCardReader
import com.ationet.androidterminal.hal.card_reader.rf_card.RfEmvCardReader
import com.newland.sdk.ModuleManage
import com.newland.sdk.module.cardreader.CardReaderExtParams
import com.newland.sdk.module.cardreader.CardReaderListener
import com.newland.sdk.module.cardreader.RFCardInfo
import com.newland.sdk.module.cardreader.SearchCardRule
import com.newland.sdk.module.iccard.ICCardSlot
import com.newland.sdk.module.iccard.ICCardSlotState
import com.newland.sdk.module.rfcard.RFCardType
import com.newland.sdk.module.rfcard.RFResult
import com.newland.sdk.module.swiper.SwipResultCode
import com.newland.sdk.module.swiper.SwiperReadModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.experimental.and
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class HALCardReaderImpl @Inject constructor(
    private val moduleManage: ModuleManage
) : HALCardReader {
    /**
     * Used to start / stop readings
     * */
    private val cardReaderModule get() = moduleManage.cardReaderModule

    /**
     * Reads data from a Magnetic Stripe card
     * */
    private val magneticStripeModule get() = moduleManage.magStripeCardModule

    /**
     * Reads data from a NFC / RFID card
     * */
    private val rfCardModule get() = moduleManage.rfCardModule

    /**
     * Reads data from a Smart card
     * */
    private val icCardModule get() = moduleManage.icCardModule

    /**
     * Protects hardware against concurrent usage
     * */
    private val hardwareMutex: Mutex = Mutex(locked = false)

    override suspend fun startRead(
        readers: List<CardReader>,
        timeout: Duration
    ): Flow<CardReaderEvent> {
        /* Make timeout of at least 1 second */
        val minimumTimeout = timeout.coerceAtLeast(1.seconds)

        val childFlow  = getReadingFlow(readers)
            .cancellable()

        hardwareMutex.lock()
        return flow {
            val hasTimedOut = collectWithTimeout(minimumTimeout, childFlow) { event ->
                emit(event)
            }

            if(hasTimedOut) {
                Log.w(TAG, "Card reader timeout")
                emit(CardReaderEvent.ReaderTimeout)
            }
        }.onCompletion {
            cardReaderModule.cancelCardReader()
            hardwareMutex.unlock()
        }
    }

    private fun getReadingFlow(
        readers: List<CardReader>
    ): Flow<CardReaderEvent> {
        return flow {

            // Wait for card detected
            val detectedCardInfo: DetectedCardInfo? = try {
                waitForCardDetected(readers)
            } catch (_: CancellationException) {
                // Read was cancelled
                return@flow
            } catch (e: Throwable) {
                Log.e(TAG, "Detect failed", e)
                emit(
                    value = CardReaderEvent.CardReadError(
                        reader = CardReader.MagneticStripeCard // Assume MSR
                    )
                )

                return@flow
            }

            // Ensure this is never null.
            checkNotNull(detectedCardInfo)

            // emit Pending removal
            emit(
                value = CardReaderEvent.CardReadBegin(
                    reader = detectedCardInfo.reader
                )
            )

            // Process card by type
            val result = try {
                when (detectedCardInfo) {
                    DetectedCardInfo.MagneticStripeCard -> readMagneticCard()
                    DetectedCardInfo.SmartCard -> readIcCard()
                    is DetectedCardInfo.RfCard -> readRfCard(detectedCardInfo.cardType)
                }
            } catch (_: CancellationException) {
                // Read was cancelled
                return@flow
            }
            catch (e: Throwable) {
                Log.e(TAG, "Read failed", e)
                emit(
                    value = CardReaderEvent.CardWaitingToRemove(
                        reader = detectedCardInfo.reader
                    )
                )

                // Wait for card removed
                waitCardExpulsion(detectedCardInfo.reader)

                emit(
                    value = CardReaderEvent.CardReadError(
                        reader = detectedCardInfo.reader
                    )
                )

                return@flow
            }

            // Wait for card removed
            emit(
                value = CardReaderEvent.CardWaitingToRemove(
                    reader = detectedCardInfo.reader
                )
            )

            waitCardExpulsion(detectedCardInfo.reader)

            // emit result
            emit(
                value = CardReaderEvent.CardRead(
                    reader = detectedCardInfo.reader,
                    card = result.orEmpty()
                )
            )
        }
    }

    private suspend fun collectWithTimeout(
        timeout: Duration,
        flow: Flow<CardReaderEvent>,
        block: FlowCollector<CardReaderEvent>
    ): Boolean {
        return withTimeoutOrNull<Unit>(timeout) {
            flow.collect(block)
        } == null
    }

    private sealed class DetectedCardInfo(val reader: CardReader) {
        data object MagneticStripeCard : DetectedCardInfo(CardReader.MagneticStripeCard)
        data object SmartCard : DetectedCardInfo(CardReader.SmartCard)
        data class RfCard(val cardType: RFCardType?) : DetectedCardInfo(CardReader.RfCard)
    }

    private suspend fun waitForCardDetected(
        readers: List<CardReader>,
    ): DetectedCardInfo {
        do {
            currentCoroutineContext().ensureActive()

            val detectedCard = suspendCancellableCoroutine {
                val cardReaderListener: CardReaderListener = object : CardReaderListener {
                    override fun onTimeout() {
                        Log.w(TAG, "Card not presented")
                        it.resume(null)
                    }

                    override fun onCancel() {
                        Log.i(TAG, "Read cancelled")
                        it.resume(null)
                    }

                    override fun onError(p0: Int, p1: String?) {
                        Log.e(TAG, "Error reading: $p1 ($p0)")
                        it.resume(null)
                    }

                    override fun onFindMagCard(p0: Boolean) {
                        Log.i(TAG, "Detected magnetic card")
                        it.resume(DetectedCardInfo.MagneticStripeCard)
                    }

                    override fun onFindICCard() {
                        Log.i(TAG, "Detected IC card")
                        it.resume(DetectedCardInfo.SmartCard)
                    }

                    override fun onFindRFCard(rfCardType: RFCardType?, p1: RFCardInfo?) {
                        Log.i(TAG, "Detected RF/NFC card")
                        it.resume(DetectedCardInfo.RfCard(rfCardType))
                    }
                }

                startCardDetection(readers, cardReaderListener)
            }

            if(detectedCard != null) {
                return detectedCard
            }
        } while (true)
    }

    private fun startCardDetection(
        enabledCards: List<CardReader>,
        cardReaderListener: CardReaderListener,
    ) {
        val cardReaderExtParams = CardReaderExtParams().also {
            it.searchCardRule = SearchCardRule.NORMAL
        }

        val enabledReaders = enabledCards.map {
            when (it) {
                CardReader.MagneticStripeCard -> com.newland.sdk.module.cardreader.CardType.MSGCARD
                CardReader.SmartCard -> com.newland.sdk.module.cardreader.CardType.ICCARD
                CardReader.RfCard -> com.newland.sdk.module.cardreader.CardType.RFCARD
            }
        }

        cardReaderModule.openCardReader(
            enabledReaders.toTypedArray(),
            255.seconds.inWholeSeconds.toInt(),
            cardReaderListener,
            cardReaderExtParams
        )

        Log.i(TAG, "Started card read")
    }

    private suspend fun waitCardExpulsion(
        cardReader: CardReader
    ) {
        when (cardReader) {
            CardReader.MagneticStripeCard -> return
            CardReader.SmartCard -> {
                /* Sit waiting until IC1 slot becomes free again */
                while (icCardModule.checkSlotsState()[ICCardSlot.IC1] != ICCardSlotState.NO_CARD) {
                    delay(10.milliseconds)
                }
            }

            CardReader.RfCard -> {
                /* Sut waiting until RF card removed */
                while (rfCardModule.isCardExist) {
                    delay(10.milliseconds)
                }

                rfCardModule.powerOff()
            }
        }
    }

    private fun readMagneticCard(): String? {
        val tracksToRead = arrayOf(
            SwiperReadModel.FIRST_TRACK,
            SwiperReadModel.SECOND_TRACK,
            SwiperReadModel.THIRD_TRACK
        )
        val result = magneticStripeModule.readPlainResult(tracksToRead)
        if (result == null) {
            Log.w(TAG, "Failed to read magnetic card. No results")
            return null
        }

        if (result.rsltCode != SwipResultCode.SUCCESS) {
            Log.w(TAG, "Failed to read magnetic card. Failed to read plain data")
            return null
        }

        return result.secondTrackData?.decodeToString()
    }

    private fun readIcCard(): String {
        val selectedSlot = icCardModule.checkSlotsState()
            .filterValues { state -> state == ICCardSlotState.CARD_INSERTED }
            .firstNotNullOfOrNull { it.key }

        if (selectedSlot == null) {
            // No card inserted
            throw IllegalStateException("IC Card detected but no slot activated. Hardware failure?")
        }

        Log.d(TAG, "Detected IC card on slot $selectedSlot")
        val reader = IcCardReader(
            icCardModule = icCardModule
        )

        val atioCardNumber = reader.readCardAtioFile()

        if (atioCardNumber != null) {
            Log.d(TAG, "Read IC card id: $atioCardNumber")
            return atioCardNumber
        }

        val emvPan = reader.readEmvCard()

        if (emvPan == null) {
            throw IllegalStateException("Failed to read EMV Primary Account Number")
        } else {
            Log.d(TAG, "Read IC card EMV PAN: $emvPan")
        }

        return emvPan
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun readRfCard(rfCardType: RFCardType?): String {
        if (rfCardType == null) {
            throw IllegalArgumentException("Missing card type")
        }

        val cardInfo = rfCardModule.powerOn(arrayOf(rfCardType), 0, null)
            ?: throw IllegalStateException("RF failed to power on")

        Log.d(TAG, "RF type: $rfCardType - SAK=${cardInfo.sak.toHexString(HexFormat.UpperCase)}")
        val card = when (rfCardType) {
            RFCardType.ACARD,
            RFCardType.BCARD -> {
                handleEmvCards(cardInfo)
            }

            RFCardType.M1CARD,
            RFCardType.M0CARD -> {
                handleMifareCards(cardInfo)
            }

            else -> {
                handleSerialNumber(cardInfo)
            }
        }

        return card
    }

    private fun handleEmvCards(cardInfo: RFResult): String {
        val emvCardReader = RfEmvCardReader(rfCardModule)

        val track2 = emvCardReader.readCard()
        if (track2.isNullOrBlank()) {
            Log.d(TAG, "No EMV data detected. Switching to card UID")
            return handleSerialNumber(cardInfo)
        }

        Log.d(TAG, "Read EMV card with track '$track2'")
        return track2
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun handleMifareCards(cardInfo: RFResult): String {
        val nfcReader = when (cardInfo.sak) {
            MIFARE_ULTRA_SAK -> M0NfcReader(rfCardModule)
            MIFARE_CLASSIC_SAK -> M1NfcCardReader(cardInfo.snr, rfCardModule)
            else -> {
                Log.d(TAG, "Incompatible card SAK '${cardInfo.sak}'")
                null
            }
        }

        if (nfcReader == null) {
            Log.d(TAG, "No EMV reader available, switching to card UID")
            return handleSerialNumber(cardInfo)
        }

        val card = NDefCardReader.readCard(nfcReader)
        if (card != null) {
            val fullPayload = card.message?.fullPayload
            val track2 = if (fullPayload != null) {
                if (fullPayload.isEmpty()) {
                    Log.d(TAG, "Payload missing. Using NFC serial number")

                    card.uid
                        .toByteArray()
                        .toHexString(format = HexFormat.UpperCase)
                } else {
                    Log.d(TAG, "Full payload: '${fullPayload.toHexString(HexFormat.UpperCase)}'")
                    val statusByte = fullPayload[0]

                    /* Bit 7:
                    *  - 0 : UTF8
                    *  - 1 : UTF16-BE
                    * */
                    val encoding = if (statusByte and 0x80.toByte() == 0x80.toByte()) {
                        Charsets.UTF_16BE
                    } else {
                        Charsets.UTF_8
                    }
                    /*
                    * Bits 5 .. 0:
                    *   IANA language code length
                    * */
                    val codeLength = (statusByte and 0x3F.toByte()).toInt()

                    val startIndex = minOf(1 + codeLength, fullPayload.size)

                    val endIndexExclusive = if (fullPayload.last() == 0xFE.toByte()) {
                        fullPayload.lastIndex - 1
                    } else {
                        fullPayload.lastIndex
                    } + 1 // Include last byte

                    fullPayload.copyOfRange(
                        fromIndex = startIndex,
                        toIndex = endIndexExclusive
                    ).toString(encoding)
                }
            } else {
                Log.d(TAG, "Payload missing. Using NFC serial number")
                cardInfo.getSerialNumber()
            }

            Log.d(TAG, "Read NFC card with record '$track2'")
            return track2
        }
        Log.d(TAG, "Card failed to be read. Switching to card UID")
        return handleSerialNumber(cardInfo)
    }

    private fun handleSerialNumber(cardInfo: RFResult): String {
        val uid = cardInfo.getSerialNumber()
        Log.d(TAG, "Read NFC card with serial number '$uid'")

        return uid
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun RFResult.getSerialNumber(): String {
        return snr.sliceArray(0..3)
            .toHexString(format = HexFormat.UpperCase)
    }

    companion object {
        private const val TAG: String = "CardReader"

        private const val MIFARE_ULTRA_SAK: Byte = 0x00
        private const val MIFARE_CLASSIC_SAK: Byte = 0x08
    }
}