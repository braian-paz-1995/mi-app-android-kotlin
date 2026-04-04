package com.atio.log

import android.os.Build
import android.util.Log
import java.lang.invoke.MethodHandles
import kotlin.reflect.KClass

class Logger {
    private var tag: String = "Logger"

    constructor(tag: Any? = null) {
        if (tag is String) this.tag = tag.toString()
        if (tag is KClass<*>) this.tag = tag.java.simpleName
        if (tag == null) this.tag = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                MethodHandles.lookup().lookupClass().name
            } else {
                "Logger"
            }
        } catch (_: Exception) {
            "Logger"
        }
    }

    fun debug(e: Throwable? = null, message: StringBuilder.() -> Any?) {
        stringBuilder.clear()
        message.invoke(stringBuilder)
        Log.d(tag, stringBuilder.toString(), e)
    }

    fun error(e: Throwable? = null, message: StringBuilder.() -> Any?) {
        stringBuilder.clear()
        message.invoke(stringBuilder)
        Log.e(tag, stringBuilder.toString(), e)
    }

    fun info(e: Throwable? = null, message: StringBuilder.() -> Any?) {
        stringBuilder.clear()
        message.invoke(stringBuilder)
        Log.i(tag, stringBuilder.toString(), e)
    }

    fun warn(e: Throwable? = null, message: StringBuilder.() -> Any?) {
        stringBuilder.clear()
        message.invoke(stringBuilder)
        Log.w(tag, stringBuilder.toString(), e)
    }

    companion object {
        private val stringBuilder = StringBuilder()
        fun debug(tag: String, e: Throwable? = null, message: StringBuilder.() -> Any?) {
            stringBuilder.clear()
            message.invoke(stringBuilder)
            Log.d(tag, stringBuilder.toString(), e)
        }

        fun debug(tag: KClass<*>, e: Throwable? = null, message: StringBuilder.() -> Any?) {
            stringBuilder.clear()
            message.invoke(stringBuilder)
            Log.d(tag::class.java.simpleName, stringBuilder.toString(), e)
        }

        fun error(tag: String, e: Throwable? = null, message: StringBuilder.() -> Any?) {
            stringBuilder.clear()
            message.invoke(stringBuilder)
            Log.e(tag, stringBuilder.toString(), e)
        }

        fun error(tag: KClass<*>, e: Throwable? = null, message: StringBuilder.() -> Any?) {
            stringBuilder.clear()
            message.invoke(stringBuilder)
            Log.e(tag::class.java.simpleName, stringBuilder.toString(), e)
        }

        fun info(tag: String, e: Throwable? = null, message: StringBuilder.() -> Any?) {
            stringBuilder.clear()
            message.invoke(stringBuilder)
            Log.i(tag, stringBuilder.toString(), e)
        }

        fun info(tag: KClass<*>, e: Throwable? = null, message: StringBuilder.() -> Any?) {
            stringBuilder.clear()
            message.invoke(stringBuilder)
            Log.i(tag::class.java.simpleName, stringBuilder.toString(), e)
        }

        fun warn(tag: String, e: Throwable? = null, message: StringBuilder.() -> Any?) {
            stringBuilder.clear()
            message.invoke(stringBuilder)
            Log.w(tag, stringBuilder.toString(), e)
        }

        fun warn(tag: KClass<*>, e: Throwable? = null, message: StringBuilder.() -> Any?) {
            stringBuilder.clear()
            message.invoke(stringBuilder)
            Log.w(tag::class.java.simpleName, stringBuilder.toString(), e)
        }
    }

}