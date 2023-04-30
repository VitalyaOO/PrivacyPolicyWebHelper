package com.sesameworkshop.elabcs.helper.util

fun decrypt(string: String, key: Int = 4): String {
    val sb = StringBuilder()
    for (element in string) {
        if (element in 'A'..'Z') {
            var t1 = element.code - 'A'.code - key
            if (t1 < 0) t1 += 26
            sb.append((t1 + 'A'.code).toChar())
        } else if (element in 'a'..'z') {
            var t1 = element.code - 'a'.code - key
            if (t1 < 0) t1 += 26
            sb.append((t1 + 'a'.code).toChar())
        }
    }
    return sb.toString()
}