package com.sesameworkshop.elabcs.helper.core

import android.app.Activity
import android.content.Context
import android.util.Log

internal object CacheHandler {
    fun saveCacheUrl(activity: Activity, url : String){
        val sharedPreferences = activity
            .getSharedPreferences("CacheSharedPreferences",Context.MODE_PRIVATE)

        sharedPreferences.edit().apply(){
            putString("Cache",url)
        }.apply()

        Log.e("","Saved url: $url")
    }

    fun getCacheUrl(activity: Activity): String?{
        val sharedPreferences = activity
            .getSharedPreferences("CacheSharedPreferences",Context.MODE_PRIVATE)

        val savedUrl =  sharedPreferences.getString("Cache",null)

        Log.e("","Got url: $savedUrl")

        return savedUrl
    }
}