package com.ationet.androidterminal.hal.card_reader

import android.annotation.SuppressLint
import android.util.Log
import com.ationet.androidterminal.core.domain.hal.HALBuzzer
import com.ationet.androidterminal.core.domain.hal.card_reader.CardReader
import com.ationet.androidterminal.core.domain.hal.card_reader.CardReaderEvent
import com.ationet.androidterminal.core.domain.hal.card_reader.HALCardReader
import com.ationet.androidterminal.hal.card_reader.MagneticStripeCard.MagneticCardReader
import com.ationet.androidterminal.hal.card_reader.RfCard.RfCardReader
import com.ationet.androidterminal.hal.card_reader.SmartCard.SdiNfcCard
import com.ationet.androidterminal.hal.card_reader.SmartCard.SmartCardReader
import com.ationet.androidterminal.hal.card_reader.config.EmvContactConfig
import com.ationet.androidterminal.hal.card_reader.config.EmvCtlsConfig
import com.verifone.payment_sdk.PaymentSdk
import com.verifone.payment_sdk.SdiEmvCtReaderOptions
import com.verifone.payment_sdk.SdiResultCode
import com.verifone.payment_sdk.SdiTecOptions
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withTimeoutOrNull
import java.util.EnumSet
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class HALCardReaderImpl @Inject constructor(
    private val paymentSdk: PaymentSdk,
    private val ctConfigData: EmvContactConfig,
    private val ctlsConfigData: EmvCtlsConfig,
    private val buzzer: HALBuzzer
) : HALCardReader {

    val TEC_CT: Short = 1
    val TEC_MSR: Short = 2
    val TEC_CTLS: Short = 4
    val TEC_ALL: Short = 0x0F
    val TEC: Short = 0
    val TIMEOUT_CARD_DETECT = 3000
    val TIMEOUT_CARD = 3000

    private val sdiManager = paymentSdk.sdiManager
    private val hardwareMutex = Mutex(locked = false)
    private val smartCardReader = SmartCardReader(sdiManager, ctlsConfigData, buzzer)
    private val rfCardReader = RfCardReader(sdiManager, ctConfigData, buzzer)
    private val magneticCardReader = MagneticCardReader(sdiManager, ctConfigData, buzzer)
    private val sdiNfcCard = SdiNfcCard(sdiManager)

    init {
        sdiNfcCard.getVersion()
    }

    @SuppressLint("SuspiciousIndentation")
    override suspend fun startRead(
        readers: List<CardReader>,
        timeout: Duration
    ): Flow<CardReaderEvent> {
        val minimumTimeout = timeout.coerceAtLeast(15.seconds)

        return flow {
            hardwareMutex.lock()
            try {
                val timedOut = withTimeoutOrNull(minimumTimeout) {
                    val detectedCardInfo = try {
                        waitForCardDetected(readers)
                    } catch (e: CancellationException) {
                        return@withTimeoutOrNull
                    } catch (e: Throwable) {
                        Log.e(TAG, "Error en la detección de tarjeta", e)
                        emit(CardReaderEvent.CardReadError(CardReader.MagneticStripeCard)) // Fallback
                        return@withTimeoutOrNull
                    }

                    emit(CardReaderEvent.CardReadBegin(detectedCardInfo.reader))

                    val result = try {
                        when (detectedCardInfo) {
                            is DetectedCardInfo.MagneticStripeCard -> magneticCardReader.read()
                            is DetectedCardInfo.SmartCard -> smartCardReader.read()
                            is DetectedCardInfo.RfCard -> rfCardReader.read()
                            is DetectedCardInfo.NfcCard -> smartCardReader.readNFC(detectedCardInfo.data)

                        }
                    } catch (e: CancellationException) {
                        return@withTimeoutOrNull
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al leer la tarjeta", e)
                        emit(CardReaderEvent.CardReadError(detectedCardInfo.reader))
                        return@withTimeoutOrNull
                    }

                    if (result != null) {
                        emit(CardReaderEvent.CardWaitingToRemove(detectedCardInfo.reader))
                        waitForCardRemoval(detectedCardInfo.reader)
                        emit(CardReaderEvent.CardRead(detectedCardInfo.reader, result))
                    } else {
                        emit(CardReaderEvent.CardReadError(detectedCardInfo.reader))
                    }
                }

                if (timedOut == null) {
                    emit(CardReaderEvent.ReaderTimeout)
                }

            } catch (e: CancellationException) {
                Log.d(TAG, "Lectura cancelada por el flujo")
            } finally {
                hardwareMutex.unlock()
            }
        }
    }

    private suspend fun waitForCardDetected(readers: List<CardReader>): DetectedCardInfo =
        withContext(Dispatchers.IO) {

            val ctOptions = EnumSet.of(SdiEmvCtReaderOptions.DETECT_WRONG_ATR)

            smartCardReader.startContactlessTransaction()

            var detectResp = sdiManager.cardDetect.detect(
                TEC_ALL,
                SdiTecOptions(
                    null, ctOptions, null,
                    null, null, null, null
                ),
                true,
                TIMEOUT_CARD,
                null, null, null, null
            )

            Log.d(TAG, "Card detection result:0 ${detectResp.result}, tecOut: ${detectResp.tecOut}")
            for (i in 0 until 20) {

                if (detectResp.tecOut == TEC) {

                    val mifareUid = smartCardReader.startNFCTransaction()
                    if (mifareUid != null) {
                        Log.d(TAG, "Tarjeta NFC (MIFARE o UID) detectada directamente.")
                        return@withContext DetectedCardInfo.NfcCard(mifareUid)
                    }

                    detectResp = sdiManager.cardDetect.detect(
                        TEC_ALL,
                        SdiTecOptions(
                            null, ctOptions, null,
                            null, null, null, null
                        ),
                        true,
                        TIMEOUT_CARD_DETECT,
                        null, null, null, null
                    )
                }
            }
            Log.d(TAG, "Card detection result: ${detectResp.result}, tecOut: ${detectResp.tecOut}")
            if (detectResp.result == SdiResultCode.OK) {
                return@withContext when (detectResp.tecOut) {
                    TEC_CT -> DetectedCardInfo.RfCard
                    TEC_CTLS -> DetectedCardInfo.SmartCard
                    TEC_MSR -> DetectedCardInfo.MagneticStripeCard
                    else -> throw IllegalStateException("Tipo de tarjeta desconocido: ${detectResp.tecOut}")
                }
            } else {
                throw IllegalStateException("Falló la detección: ${detectResp.result}")
            }
        }

    private suspend fun waitForCardRemoval(reader: CardReader) = withContext(Dispatchers.IO) {
        val maxAttempts = 50
        val delayBetweenChecks = 100L
        var attempt = 0

        while (attempt < maxAttempts) {
            val options = SdiTecOptions(
                null,
                EnumSet.of(SdiEmvCtReaderOptions.DETECT_WRONG_ATR),
                null,
                null,
                null,
                null,
                null
            )

            val response = sdiManager.cardDetect.detectWith(
                TEC_ALL,
                options,
                true,
                1000,
                null,
                null,
                null,
                null,
                1500
            )

            if (response.result == SdiResultCode.ERR_CARD_REMOVED ||
                response.result == SdiResultCode.EMVSTATUS_NO_CARD ||
                response.result == SdiResultCode.ERR_TIMEOUT
            ) {
                Log.d(TAG, "Tarjeta retirada.")
                return@withContext
            } else {
                Log.d(TAG, "Resultado de la detección: ${response.result}")
            }

            delay(delayBetweenChecks)
            attempt++
        }

        Log.w(TAG, "Timeout esperando retiro de tarjeta.")
    }

    companion object {
        private const val TAG = "HALCardReaderImpl"
    }
}
