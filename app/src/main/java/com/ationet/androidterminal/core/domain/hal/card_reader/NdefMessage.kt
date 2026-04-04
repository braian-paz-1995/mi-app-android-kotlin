package com.ationet.androidterminal.core.domain.hal.card_reader

import androidx.annotation.IntRange
import kotlin.experimental.and

data class NdefMessage(
    val records: List<NdefRecord>,
) {
    val fullPayload : ByteArray = records.flatMap { record -> record.payload }.toByteArray()

    companion object {
        private const val MESSAGE_END_FLAG_OFFSET : Int = 6
        private const val SHORT_RECORD_FLAG_OFFSET : Int = 4
        private const val ID_LENGTH_FLAG_OFFSET : Int = 3

        fun parse(data: ByteArray, @IntRange(from = 0) size: Int): NdefMessage {
            var index = 0
            val records = mutableListOf<NdefRecord>()

            // Log empty input
            if(size == 0) {
                return NdefMessage(emptyList())
            }

            if(data.isEmpty()) {
                return NdefMessage(emptyList())
            }

            val actualSize = minOf(size, data.size)

            while (index <= actualSize) {
                /*
                * Parse record flags
                * */
                val tnf = data[index].parseTnf()
                val messageEnd = data[index].isSet(MESSAGE_END_FLAG_OFFSET)
                val shortRecord = data[index].isSet(SHORT_RECORD_FLAG_OFFSET)
                val il = data[index].isSet(ID_LENGTH_FLAG_OFFSET)
                index++

                /*
                * Parse type length
                * */
                val typeLength = data[index].toInt()
                index++

                /*
                * Parse payload size depending if this record is a short or long one
                * */
                val payloadLength: UInt = if(shortRecord) {
                    data[index++].toUInt()
                } else {
                    index += 4
                    data.toUInt(offset = index - 4)
                }

                /*
                * Try to get Id length if available
                * */
                val idLength = if(il) {
                    data[index++]
                } else {
                    0
                }


                /*
                * Get type
                * */
                val type = data.copyOfRange(
                    fromIndex = index,
                    toIndex = index + typeLength
                )
                index += typeLength

                /*
                * Get ID if available
                * */
                val id = if(il) {
                    data.copyOfRange(
                        fromIndex = index,
                        toIndex = index + idLength
                    )
                } else {
                    byteArrayOf()
                }
                index += idLength

                /* Get payload*/
                val payload = data.copyOfRange(
                    fromIndex = index,
                    toIndex = index + payloadLength.toInt()
                )
                index += payloadLength.toInt()

                val record = NdefRecord(
                    typeNameFormat = tnf,
                    id = id.toList(),
                    type = type.toList(),
                    payload = payload.toList()
                )

                records.add(record)

                if (messageEnd) {
                    break
                }
            }

            return NdefMessage(
                records = records
            )
        }

        private fun Byte.parseTnf(): NdefTypeNameFormat {
            return when (this and 0x07) {
                0x00.toByte() -> NdefTypeNameFormat.Empty
                0x01.toByte() -> NdefTypeNameFormat.WellKnown
                0x02.toByte() -> NdefTypeNameFormat.MimeMedia
                0x03.toByte() -> NdefTypeNameFormat.AbsoluteUri
                0x04.toByte() -> NdefTypeNameFormat.ExternalType
                0x06.toByte() -> NdefTypeNameFormat.Unchanged
                0x07.toByte() -> NdefTypeNameFormat.Reserved
                else -> NdefTypeNameFormat.Unknown
            }
        }

        private fun Byte.isSet(@IntRange(from = 0, to = 7) bit: Int): Boolean {
            val mask = (1 shl bit).toByte()

            return (this and mask) == mask
        }

        private fun ByteArray.toUInt(offset: Int = 0): UInt {
            return (this[offset].toUInt() shl 24) or (this[offset + 1].toUInt() shl 16) or (this[offset + 2].toUInt() shl 8) or (this[offset + 3].toUInt())
        }
    }
}