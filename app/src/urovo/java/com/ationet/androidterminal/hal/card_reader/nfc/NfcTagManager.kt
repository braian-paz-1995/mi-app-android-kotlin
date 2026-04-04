package com.ationet.androidterminal.hal.card_reader.nfc

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed interface NfcCommand {
    data object Read : NfcCommand
    data object Cancel : NfcCommand
}

sealed interface NfcEvent {
    data object TagPresented : NfcEvent
    data class TagRead(val tag: Tag) : NfcEvent
    data object TagRemoved : NfcEvent
    data class Error(val exception: Throwable?) : NfcEvent
}

sealed interface NfcCommandReceiver {
    val commands : ReceiveChannel<NfcCommand>

    suspend fun sendEvent(event: NfcEvent)
}

sealed interface NfcEventReceiver {
    val events : SharedFlow<NfcEvent>

    suspend fun sendCommand(command: NfcCommand)
}

/**
 * Mediates the read of Nfc tags, notifying new clients and waiting for tags.
 * */
@Singleton
class NfcMediator @Inject constructor() : NfcCommandReceiver, NfcEventReceiver {
    private val _commands = Channel<NfcCommand>()
    override val commands: ReceiveChannel<NfcCommand> = _commands

    override suspend fun sendCommand(command: NfcCommand) {
        _commands.send(command)
    }

    private val _events = MutableSharedFlow<NfcEvent>()
    override val events: SharedFlow<NfcEvent> = _events.asSharedFlow()

    override suspend fun sendEvent(event: NfcEvent) {
        _events.emit(event)
    }
}
