package com.atio.log.util

import com.atio.log.Logger
import kotlin.reflect.KClass

fun Logger.d(message: String, e: Throwable? = null) {
    debug(e) { appendLine(message) }
}

fun Logger.Companion.d(tag: String, message: String, e: Throwable? = null) {
    debug(tag, e) { appendLine(message) }
}

fun Logger.Companion.d(tag: KClass<*>, message: String, e: Throwable? = null) {
    debug(tag, e) { appendLine(message) }
}

fun Logger.debug(message: String, e: Throwable? = null) {
    debug(e) { appendLine(message) }
}

fun Logger.Companion.debug(tag: String, message: String, e: Throwable? = null) {
    debug(tag, e) { appendLine(message) }
}

fun Logger.Companion.debug(tag: KClass<*>, message: String, e: Throwable? = null) {
    debug(tag, e) { appendLine(message) }
}

fun Logger.e(message: String, e: Throwable? = null) {
    error(e) { appendLine(message) }
}

fun Logger.Companion.e(tag: String, message: String, e: Throwable? = null) {
    error(tag, e) { appendLine(message) }
}

fun Logger.Companion.e(tag: KClass<*>, message: String, e: Throwable? = null) {
    error(tag, e) { appendLine(message) }
}

fun Logger.error(message: String, e: Throwable? = null) {
    error(e) { appendLine(message) }
}

fun Logger.Companion.error(tag: String, message: String, e: Throwable? = null) {
    error(tag, e) { appendLine(message) }
}

fun Logger.Companion.error(tag: KClass<*>, message: String, e: Throwable? = null) {
    error(tag, e) { appendLine(message) }
}

fun Logger.i(message: String, e: Throwable? = null) {
    info(e) { appendLine(message) }
}

fun Logger.Companion.i(tag: String, message: String, e: Throwable? = null) {
    info(tag, e) { appendLine(message) }
}

fun Logger.Companion.i(tag: KClass<*>, message: String, e: Throwable? = null) {
    info(tag, e) { appendLine(message) }
}

fun Logger.info(message: String, e: Throwable? = null) {
    info(e) { appendLine(message) }
}

fun Logger.Companion.info(tag: String, message: String, e: Throwable? = null) {
    info(tag, e) { appendLine(message) }
}

fun Logger.Companion.info(tag: KClass<*>, message: String, e: Throwable? = null) {
    info(tag, e) { appendLine(message) }
}

fun Logger.Companion.w(tag: String, message: String, e: Throwable? = null) {
    warn(tag, e) { appendLine(message) }
}

fun Logger.w(message: String, e: Throwable? = null) {
    warn(e) { appendLine(message) }
}

fun Logger.Companion.w(tag: KClass<*>, message: String, e: Throwable? = null) {
    warn(tag, e) { appendLine(message) }
}

fun Logger.Companion.warn(tag: String, message: String, e: Throwable? = null) {
    warn(tag, e) { appendLine(message) }
}

fun Logger.warn(message: String, e: Throwable? = null) {
    warn(e) { appendLine(message) }
}

fun Logger.Companion.warn(tag: KClass<*>, message: String, e: Throwable? = null) {
    warn(tag, e) { appendLine(message) }
}