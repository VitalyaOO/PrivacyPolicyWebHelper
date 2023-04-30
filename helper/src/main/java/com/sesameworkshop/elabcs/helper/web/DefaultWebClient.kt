package com.sesameworkshop.elabcs.helper.web

import android.app.Activity
import android.content.Intent
import android.webkit.WebView
import com.sesameworkshop.elabcs.helper.core.ExceptionClassData
import com.sesameworkshop.elabcs.helper.core.common.Const
import com.sesameworkshop.elabcs.helper.web.ui.WebActivity

class DefaultWebClient() : SimpleWebHelperClient {
    private val webActivity = WebActivity()

    override fun setWebSettings(callBack: (WebView) -> Unit) {
        webActivity.defaultSetWebSettings = callBack
    }

    override fun openWebClient(
        activity: Activity,
        exceptionActivity: Class<out Activity>,
        isCacheAlreadySaved: Boolean,
        url: String,
        exceptionTitle: String,
    ) {
        ExceptionClassData.exceptionClass = exceptionActivity

        Intent(activity, webActivity.javaClass).also { intent ->
            intent.putExtra(Const.urlName,url)
            intent.putExtra(Const.isCacheAlreadySavedName,isCacheAlreadySaved)
            intent.putExtra(Const.exceptionTitleName,exceptionTitle)

            activity.startActivity(intent)

        }
    }
}