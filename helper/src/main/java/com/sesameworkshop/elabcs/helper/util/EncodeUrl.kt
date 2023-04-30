package com.sesameworkshop.elabcs.helper.util

import java.net.URLEncoder

fun String.encodeUrl () : String {
    return try {
        URLEncoder.encode(this, "utf-8")
    } catch (e: Exception) {
        "null"
    }
}