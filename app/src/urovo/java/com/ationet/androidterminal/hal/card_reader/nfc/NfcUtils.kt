package com.ationet.androidterminal.hal.card_reader.nfc

import android.nfc.Tag
import android.nfc.tech.TagTechnology
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.collections.sliceArray
import kotlin.time.Duration.Companion.milliseconds

fun isTagConnected(tag: TagTechnology): Boolean {
    val result = runCatching {
        tag.isConnected
    }

    return result.getOrElse { false }
}

@OptIn(ExperimentalStdlibApi::class)
fun readTagId(tag: Tag): String {
    return tag.id
        .sliceArray(0..3)
        .toHexString(HexFormat.UpperCase)
}

suspend fun waitForNfcTagRemoval(tag: TagTechnology) {
    val coroutineContext = currentCoroutineContext()
    while (coroutineContext.isActive && isTagConnected(tag)) {
        delay(250.milliseconds)
    }
}