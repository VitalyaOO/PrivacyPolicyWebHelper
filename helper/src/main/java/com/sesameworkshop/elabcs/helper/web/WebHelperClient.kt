package com.sesameworkshop.elabcs.helper.web

import android.app.Activity

interface WebHelperClient {
    fun openWebClient(
        activity: Activity,
        exceptionActivity: Class<out Activity>,
        isCacheAlreadySaved: Boolean,
        url: String,
        exceptionTitle: String,
    )
}