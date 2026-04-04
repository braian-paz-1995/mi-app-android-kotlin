package com.ationet.androidterminal

import com.ationet.androidterminal.core.util.limitDigits
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class DoubleExtensionTests(val input: Double?, val decimals: Int, val expected: String) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() : Collection<Array<Any?>> = buildList {
            add(arrayOf(0.12345, 4, "0.123"))
            add(arrayOf(1.23456, 4, "1.234"))
            add(arrayOf(1024.768, 4, "1024"))
            add(arrayOf(12345.678, 4, "1234"))
            add(arrayOf(2.7, 4, "2.700"))
            add(arrayOf(2.0, 4, "2.000"))
            add(arrayOf(null as Double?, 4, ""))
        }
    }

    @Test
    fun `Limit digits on double values`() {
        val result = input.limitDigits(decimals)
        assert(result == expected) { "Expected $expected, got $result" }
    }
}