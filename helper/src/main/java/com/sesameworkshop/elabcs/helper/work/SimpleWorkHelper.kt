package com.sesameworkshop.elabcs.helper.work

import android.app.Activity
import android.content.Context
import com.sesameworkshop.elabcs.helper.work.model.*

interface SimpleWorkHelper  : WorkHelper{
    fun initOneSignal(oneSignalId: String, context: Context)

    fun initReferrer(activity: Activity, responseCallback: (String?) -> Unit)

    fun getRefData(referrer: String?, fbDescriptionKey: String): ReferrerData?

    fun generateLink(
        tracker: String,
        parseToolsData: ParseToolsData,
        valuesData: ValuesData?,
        mainValues: MainValues,
    )
            : String

    fun sendOneSignal(push: String?, appsId: String?)

    fun initAppsFlyer(
        activity: Activity,
        afKey: String,
        cullBack: (AppsData?) -> Unit
    )

    suspend fun getDeepLink(
        context: Context,
        fbId: String,
        fbToken: String,
        cullBack: (String?) -> Unit
    )

    fun parseValues(
        referrerData: ReferrerData?,
        appsData: AppsData?,
        deepLink: String?,
    ): ParseToolsData

    suspend fun getSystemInfo(context: Context): ValuesData
}