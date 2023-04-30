package com.sesameworkshop.elabcs.helper.web

import android.webkit.WebView

interface SimpleWebHelperClient : WebHelperClient {

    fun setWebSettings(callBack : (WebView) -> Unit )
}