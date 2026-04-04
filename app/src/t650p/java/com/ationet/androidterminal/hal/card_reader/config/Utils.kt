package com.ationet.androidterminal.hal.card_reader.config

import android.content.Context
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object {
        fun Date.dateToString(format: String, locale: Locale = Locale.getDefault()): String {
            val formatter = SimpleDateFormat(format, locale)
            return formatter.format(this)
        }

        fun getCurrentDateTime(): Date {
            return Calendar.getInstance().time
        }

        fun String.hexStringToByteArray(): ByteArray {
            val result = ByteArray(this.length / 2)
            for (i in result.indices) {
                val index = i * 2
                result[i] = this.substring(index, index + 2).toLong(radix = 16).toByte()
                //Log.i("Utils", "result[i]: ${result[i]}")
            }
            return result
        }

        fun ByteArray.toHexString(): String =
            joinToString(separator = "") { eachByte -> "%02X".format(eachByte) }

        fun getDataFromAssets(context: Context, fileName: String): String {
            var contents = ""
            try {
                val stream: InputStream = context.assets.open(fileName)
                val size: Int = stream.available()
                val buffer = ByteArray(size)
                stream.read(buffer)
                stream.close()
                contents = String(buffer)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return contents
        }
    }
}