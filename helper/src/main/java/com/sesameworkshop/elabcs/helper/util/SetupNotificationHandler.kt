package com.sesameworkshop.elabcs.helper.util

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import com.onesignal.OneSignal
import com.sesameworkshop.elabcs.helper.core.common.Const
import com.sesameworkshop.elabcs.helper.web.ui.WebActivity

fun setupNotificationHandler(
    activity: Activity,
    title: String?,
) {
    OneSignal.setNotificationOpenedHandler {
        val launchUrl: String? = it?.notification?.launchURL

        if (launchUrl == null || launchUrl == "null" || launchUrl == "") {
            return@setNotificationOpenedHandler
        }

        val isValidUrl =
            launchUrl.startsWith("${decrypt("aaa")}.") ||
                    launchUrl.startsWith("${decrypt("lxxt")}://") ||
                    launchUrl.startsWith("${decrypt("lxxtw")}://")

        if (isValidUrl) {
            OneSignal.setNotificationOpenedHandler(null)

            val intent = Intent(activity, WebActivity::class.java)

            intent.putExtra(Const.urlName, launchUrl)
            intent.putExtra(Const.isCacheAlreadySavedName, true)
            intent.putExtra(Const.exceptionTitleName, title)

            activity.apply {
                startActivity(intent)
                finish()
            }
        }
    }
}