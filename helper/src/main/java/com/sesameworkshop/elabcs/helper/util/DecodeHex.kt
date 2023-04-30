package com.sesameworkshop.elabcs.helper.util

internal fun String.decodeHex(): ByteArray {
    check(length % 2 == 0) { "Size exception" }
    return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}