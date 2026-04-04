package com.ationet.androidterminal

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ationet.androidterminal.hal.card_reader.nfc.NfcCommand
import com.ationet.androidterminal.hal.card_reader.nfc.NfcCommandReceiver
import com.ationet.androidterminal.hal.card_reader.nfc.NfcEvent
import com.ationet.androidterminal.hal.card_reader.nfc.NfcTagParser
import com.ationet.androidterminal.hal.card_reader.nfc.waitForNfcTagRemoval
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import com.ationet.androidterminal.hal.card_reader.nfc.Tag as NfcTag

/**
 * Start class for Urovo's activity.
 * */
@AndroidEntryPoint
class MainActivity : BaseMainActivity() {
    /**
     * Publisher for NFC events/receiver of commands.
     * */
    @Inject
    lateinit var nfcCommandReceiver: NfcCommandReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                for(command in nfcCommandReceiver.commands) {
                    Log.d(TAG, "NFC: Received command - $command")

                    when (command) {
                        NfcCommand.Read -> {
                            if(isReadingNfcTags) {
                                Log.w(TAG, "NFC: Ignoring read command - already reading. This might be a bug!")
                                continue
                            }

                            beginTagRead()
                        }
                        NfcCommand.Cancel -> {
                            if(!isReadingNfcTags) {
                                Log.w(TAG, "NFC: Ignoring cancel command - not reading.")
                                continue
                            }

                            cancelReading()

                            /* After requesting the cancellation, we're done reading. */
                            isReadingNfcTags = false
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

        if (isReadingNfcTags) {
            Log.d(TAG, "NFC: Disable read onPause()")

            cancelReading()
        }
    }

    override fun onResume() {
        super.onResume()

        if(isReadingNfcTags) {
            Log.d(TAG, "NFC: Enable read onResume()")
            beginTagRead()
        }
    }

    // region NFC
    private var nfcReadJob: Job? = null
    private var isReadingNfcTags: Boolean = false

    private fun cancelReading() {
        val job = nfcReadJob
        if(job != null) {
            val nfcAdapter = NfcAdapter.getDefaultAdapter(this)!!
            nfcAdapter.disableReaderMode(this)
            Log.d(TAG, "NFC: Disabled NFC reader")

            job.cancel()
            Log.d(TAG, "NFC: Requested read cancellation")

            /* Note: don't set isReadingNfcTags to false here, as it might be cancelled during onPause() */
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun beginTagRead() {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)!!
        if (!nfcAdapter.isEnabled) {
            Log.w(TAG, "NFC: adapter disabled. Enable it in configuration, then proceed again")
            return
        }

        /* Flag this operation as started */
        isReadingNfcTags = true

        Log.d(TAG, "NFC: Starting read...")

        val readJob = lifecycleScope.launch {
            /* Blocks until a tag is detected. */
            val tag = waitForTag(nfcAdapter)

            val id = tag.id.toHexString(HexFormat.UpperCase)
            val technologies = tag.techList.toList()

            Log.d(TAG, "NFC: Got tag - ID: $id - Technologies: $technologies")

            /* Report tag presented */
            nfcCommandReceiver.sendEvent(NfcEvent.TagPresented)

            val parsedTag = try {
                NfcTagParser.parse(tag)
            } catch (e: Throwable) {
                currentCoroutineContext().ensureActive()

                Log.e(TAG, "NFC: Error parsing tag", e)

                nfcCommandReceiver.sendEvent(NfcEvent.Error(e))

                /* Flag this operation as completed (with error) */
                isReadingNfcTags = false

                return@launch
            }

            val nfcTag = NfcTag(
                id = parsedTag.id,
                content = id,
                technology = parsedTag.technologyType
            )

            nfcCommandReceiver.sendEvent(NfcEvent.TagRead(nfcTag))

            waitForNfcTagRemoval(parsedTag.technology)
            runCatching {
                parsedTag.technology.close()
            }

            Log.d(TAG, "NFC: Tag read complete")

            nfcCommandReceiver.sendEvent(NfcEvent.TagRemoved)

            /* Flag this operation as completed (normally) */
            isReadingNfcTags = false
        }

        readJob.invokeOnCompletion {
            nfcReadJob = null
            Log.d(TAG, "NFC: Read job completed")
        }

        nfcReadJob = readJob
    }

    private suspend fun waitForTag(nfcAdapter: NfcAdapter): Tag {
        return suspendCancellableCoroutine { continuation ->
            val callback = NfcAdapter.ReaderCallback { tag ->
                continuation.resume(tag)
            }

            val options = NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_V or
                    NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS

            nfcAdapter.enableReaderMode(this@MainActivity, callback, options, null)

            Log.d(TAG, "NFC: Enabled NFC reader")
        }
    }
    // endregion

    private companion object {
        private const val TAG = "MainActivity"
    }
}