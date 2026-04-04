package com.ationet.androidterminal.hal.card_reader

import android.util.Log
import com.ationet.androidterminal.core.di.DefaultDispatcher
import com.ationet.androidterminal.core.domain.hal.card_reader.CardReader
import com.ationet.androidterminal.core.domain.hal.card_reader.CardReaderEvent
import com.ationet.androidterminal.core.domain.hal.card_reader.HALCardReader
import com.ationet.androidterminal.hal.card_reader.ic.IcReader
import com.ationet.androidterminal.hal.card_reader.msr.MagneticCardReader
import com.ationet.androidterminal.hal.card_reader.nfc.NfcReader
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class HALCardReaderImpl @Inject constructor(
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    private val magneticStripeCardReader: MagneticCardReader,
    private val icCardReader: IcReader,
    private val nfcCardReader: NfcReader,
) : HALCardReader {

    override suspend fun startRead(
        cardTypes: List<CardReader>,
        timeout: Duration
    ): Flow<CardReaderEvent> {
        check(cardTypes.isNotEmpty()) { "No card types specified" }

        /* Make timeout of at least 1 second */
        val minimumTimeout = timeout.coerceAtLeast(1.seconds)
        val readerFlows = buildList {
            if (CardReader.MagneticStripeCard in cardTypes) {
                val job = magneticStripeCardReader.read()
                add(job)
            }

            if (CardReader.SmartCard in cardTypes) {
                val job = icCardReader.read()
                add(job)
            }

            if (CardReader.RfCard in cardTypes) {
                val job = nfcCardReader.read()
                add(job)
            }
        }

        return raceReaders(minimumTimeout, readerFlows).onEach {
            Log.v(TAG, "Forwarding event: $it")
        }
    }

    private fun raceReaders(
        timeout: Duration,
        flows: List<Flow<CardReaderEvent>>
    ): Flow<CardReaderEvent> = channelFlow {
        /* Copy <this> reference for expressiveness */
        val producer = this

        val selectedFlowCompletable = CompletableDeferred<Flow<CardReaderEvent>>()
        val jobs = mutableListOf<Job>()
        /* Create a job that will complete when timeout happens */
        val timeoutJob = launch(dispatcher) {
            /* Delay until A - Timeout has passed, or B - Scope is cancelled */
            delay(timeout)

            val cancellationException = CancellationException("Timed out waiting for flows")

            /* Complete with exception to signal cancellation. */
            if (selectedFlowCompletable.completeExceptionally(cancellationException)) {
                /* Cancel flow jobs */
                jobs.forEach { it.cancel() }

                /* When this happens, send timeout event to consumer. Using trySend to avoid */
                producer.trySend(CardReaderEvent.ReaderTimeout)
            }

            /* Close the channel to avoid other jobs to send data over it */
            producer.close()
        }

        for (flow in flows) {
            /* Convert received cold flow into a hot one. */
            val sharedFlow = flow.shareIn(
                scope = this,
                started =  SharingStarted.Lazily,
                replay = 1 // Replay has to be something else than Rendezvous(0, or default) to avoid loosing events while we're cancelling other readers
            )

            val job = launch(dispatcher) {
                sharedFlow.collect {
                    /* This flow has collected a value, so try completing the deferred to signal this flow won the race */
                    if (selectedFlowCompletable.complete(sharedFlow)) {
                        producer.trySend(it)
                    }
                }
            }

            jobs += job
        }

        /* Now, await for the winner */
        val winningFlow = try {
            selectedFlowCompletable.await()
        } catch (_: CancellationException) {
            return@channelFlow
        }

        /* Cancel timeout job and not completed flows */
        jobs.forEach { if (it.isActive) it.cancel() }
        timeoutJob.cancel()

        // Continue collecting from winner
        winningFlow.collect {
            trySend(it)
        }

        awaitClose {
            jobs.forEach { it.cancel() }
            timeoutJob.cancel()
        }
    }

    private companion object {
        private const val TAG: String = "CardReader"
    }
}