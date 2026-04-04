package com.ationet.androidterminal.hal.card_reader.nfc

import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.nfc.tech.NfcV
import android.nfc.tech.TagTechnology
import com.github.devnied.emvnfccard.parser.EmvTemplate
import com.github.devnied.emvnfccard.parser.IProvider

enum class NfcTechnologyType(val description: String) {
    NfcA("android.nfc.tech.NfcA"),
    NfcV("android.nfc.tech.NfcV"),
    IsoDep("android.nfc.tech.IsoDep"),
    NDef("android.nfc.tech.Ndef")
}

data class ParsedTag(
    val id: List<Byte>,
    val content: String,
    val technologyType: NfcTechnologyType,
    val technology: TagTechnology
)

object NfcTagParser {
    fun parse(tag: Tag): ParsedTag {
        return when {
            isTechnology(NfcTechnologyType.NDef.description, tag) -> {
                handleNdefTag(tag)
            }

            isTechnology(NfcTechnologyType.IsoDep.description, tag) -> {
                handleIsoDepTag(tag)
            }

            isTechnology(NfcTechnologyType.NfcA.description, tag) -> {
                handleNfcATag(tag)
            }

            isTechnology(NfcTechnologyType.NfcV.description, tag) -> {
                handleNfcVTag(tag)
            }

            else -> throw IllegalArgumentException("Unsupported technology")
        }
    }

    /**
     * Handles NDEF formatted tags
     * */
    private fun handleNdefTag(
        tag: Tag,
    ): ParsedTag {
        /* Ensure TAG actually is NDEF */
        check(isTechnology(NfcTechnologyType.NDef.description, tag)) {
            "TAG is not NDEF. This is likely a bug"
        }

        val ndef = Ndef.get(tag)
        checkNotNull(ndef) { "Failed to obtain NDEF tag" }

        if (!isTagConnected(ndef)) {
            ndef.connect()
        }

        return ParsedTag(
            id = tag.id.toList(),
            content = parseNdef(ndef),
            technologyType = NfcTechnologyType.NDef,
            technology = ndef
        )
    }

    private fun handleIsoDepTag(
        tag: Tag,
    ): ParsedTag {
        check(isTechnology(NfcTechnologyType.IsoDep.description, tag)) {
            "Tag is not IsoDep. This is likely a bug"
        }

        val isoDep = IsoDep.get(tag)
        checkNotNull(isoDep) { "Failed to obtain IsoDep tag" }

        if (!isTagConnected(isoDep)) {
            isoDep.connect()
        }

        return ParsedTag(
            id = tag.id.toList(),
            content = readIsoDepTag(isoDep),
            technologyType = NfcTechnologyType.IsoDep,
            technology = isoDep
        )
    }

    private fun handleNfcATag(
        tag: Tag,
    ): ParsedTag {
        check(isTechnology(NfcTechnologyType.NfcA.description, tag)) {
            "Tag is not NFC-A. This is likely a bug"
        }

        val nfcA = NfcA.get(tag)
        checkNotNull(nfcA) { "Failed to obtain nfc-a tag" }

//        if (!isTagConnected(nfcA)) {
//            nfcA.connect()
//        }

        return ParsedTag(
            id = tag.id.toList(),
            content = readTagId(tag),
            technologyType = NfcTechnologyType.NfcA,
            technology = nfcA
        )
    }

    private fun handleNfcVTag(
        tag: Tag,
    ): ParsedTag {
        check(isTechnology(NfcTechnologyType.NfcV.description, tag)) {
            "Tag is not NFC-v. This is likely a bug"
        }

        val nfcV = NfcV.get(tag)
        checkNotNull(nfcV) { "Failed to obtain nfc-v tag" }

        if (!isTagConnected(nfcV)) {
            nfcV.connect()
        }

        return ParsedTag(
            id = tag.id.toList(),
            content = readTagId(tag),
            technologyType = NfcTechnologyType.NfcV,
            technology = nfcV
        )
    }

    private fun isTechnology(technology: String, tag: Tag): Boolean {
        return tag.techList.contains(technology)
    }

    // region NDEF tags
    @OptIn(ExperimentalStdlibApi::class)
    private fun parseNdef(tag: Ndef): String {
        val message = tag.ndefMessage
        check(message.records.isNotEmpty()) { "NDEF formatted tag is empty" }

        val content = message.records[0].payload

        return parseTextRecord(content.toList())
    }
    // endregion

    // region IsoDEP tags
    private val config = EmvTemplate.Config()
        .setContactLess(true)
        .setReadAllAids(false)
        .setReadTransactions(false)
        .setReadAt(false)

    private fun readIsoDepTag(tag: IsoDep): String {
        val provider = object : IProvider {
            override fun transceive(pCommand: ByteArray?): ByteArray {
                if (pCommand == null) {
                    return byteArrayOf()
                }

                return tag.transceive(pCommand)
            }

            override fun getAt(): ByteArray {
                throw NotImplementedError("This method should not be called")
            }
        }

        val parser = EmvTemplate.Builder()
            .setConfig(config)
            .setProvider(provider)
            .build()

        val card = parser.readEmvCard()
        val track2 = card?.cardNumber
        return track2.orEmpty()
    }
    // endregion
}