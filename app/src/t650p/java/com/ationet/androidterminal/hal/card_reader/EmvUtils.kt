// EmvUtils.kt
package com.ationet.androidterminal.hal.card_reader

import android.util.Log
import com.ationet.androidterminal.hal.card_reader.config.Utils.Companion.toHexString
import com.verifone.payment_sdk.SdiManager
import com.verifone.payment_sdk.SdiTlv
import com.verifone.payment_sdk.SdiEmvTxn


object EmvUtils {
    private const val TAG = "EmvUtils"

    fun retrieveTagsUsingApi(sdiManager: SdiManager, tagsToRetrieve: List<String>): String {
        val tags = ArrayList<Long>()
        for (tag in tagsToRetrieve) {
            tags.add(tag.toLong(16))
        }

        val sdiEmvTxn = SdiEmvTxn.create()
        sdiManager.emvCt.continueOnline(true, byteArrayOf(0x30, 0x30), sdiEmvTxn)

        val tagReceived = sdiManager.data.fetchTxnTags(tags, 2, false)

        fun parseTLV(data: ByteArray): Map<String, ByteArray> {
            val result = mutableMapOf<String, ByteArray>()
            var index = 0

            while (index < data.size) {
                var tag = String.format("%02X", data[index++])
                if (index < data.size && (data[index - 1].toInt() and 0x1F) == 0x1F) {
                    tag += String.format("%02X", data[index++])
                }
                if (index >= data.size) break

                var length = data[index++].toInt()
                if (length < 0) length += 256

                if (index + length > data.size) {
                    Log.e(
                        TAG,
                        "TLV parse error for tag $tag: need $length, have ${data.size - index}"
                    )
                    break
                }

                val value = data.copyOfRange(index, index + length)
                result[tag] = value
                index += length
            }

            return result
        }

        try {
            val parsed = parseTLV(tagReceived.response)
            parsed.forEach { (tag, value) ->
                Log.d(TAG, "TLV -> Tag: $tag, Value: ${value.toHexString()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing TLV", e)
        }

        var resultTrack2 = ""
        val messageTag = 0xF0
        val tlvData = SdiTlv.create()
        tlvData.load(tagReceived.response, false)

        for (tag in tags) {
            if (tlvData.obtain(messageTag).count(tag.toInt()) > 0) {
                val value =
                    tlvData.obtain(messageTag).obtain(tag.toInt()).store(false).toHexString()
                resultTrack2 = parseTrack2Data(value)
                break
            }
        }

        return resultTrack2
    }

    fun retrieveTagsUsingApiNfc(sdiManager: SdiManager,data : String):String{
        Log.d(TAG, "Value: $data")
        var resultTrack2: String
        resultTrack2= data
        Log.d(TAG, "Value Track: $resultTrack2")
        return resultTrack2
    }
    fun parseTrack2Data(value: String): String {
        var resultTrack2 = ""

        Log.d(TAG, "Value: $value")

        resultTrack2 = value.replace('D', '=')
        resultTrack2 = resultTrack2.replace("B","")
        resultTrack2 = resultTrack2.substring(0,resultTrack2.indexOf("F"))
        Log.d(TAG, "Value: $resultTrack2")


        return resultTrack2
    }
}
