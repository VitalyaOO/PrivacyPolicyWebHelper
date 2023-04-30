package com.sesameworkshop.elabcs.helper.web.ui

import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.sesameworkshop.elabcs.helper.core.CacheHandler
import com.sesameworkshop.elabcs.helper.core.ExceptionClassData
import com.sesameworkshop.elabcs.helper.core.common.Const
import com.sesameworkshop.elabcs.helper.util.setupNotificationHandler
import com.sesameworkshop.elabcs.helper.web.clients.WebChromeClient
import com.sesameworkshop.elabcs.helper.web.clients.WebViewClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
internal class WebActivity @Inject constructor(
) : AppCompatActivity(){

    private var myUri: Uri? = null

    @SuppressLint("AnnotateVersionCheck")
    private val isHighLvl = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    private val webChromeClient = WebChromeClient(this)

    internal lateinit var webView : WebView

    @SuppressLint("SetJavaScriptEnabled")
    internal var defaultSetWebSettings: (WebView) -> Unit = { webView ->
        webView.settings.apply {
            mixedContentMode = 0
            javaScriptEnabled = true
            domStorageEnabled = true
            loadsImagesAutomatically = true
            databaseEnabled = true
            allowFileAccess = true
            javaScriptCanOpenWindowsAutomatically = true
            loadWithOverviewMode = true
            allowContentAccess = true
            setSupportMultipleWindows(false)
            builtInZoomControls = true
            displayZoomControls = false
            cacheMode = WebSettings.LOAD_DEFAULT
            userAgentString = userAgentString.replace("; wv","")
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) saveFormData = true
        }

        webView.apply {
            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            isSaveEnabled = true
            isFocusable = true
            isFocusableInTouchMode = true
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            setInitialScale(200)         //change useWideViewPort = true
            setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                importantForAutofill = WebView.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
            }
        }
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onPause() {
        CookieManager.getInstance().flush()
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)

        webView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)

        setContentView(webView)

        Toast.makeText(this,"Loading",Toast.LENGTH_LONG).show()

        var url = intent.getStringExtra(Const.urlName) as String

        var isSavingIsNotNecessary = intent.getBooleanExtra(Const.isCacheAlreadySavedName, true)

        val title: String? = intent.getStringExtra(Const.exceptionTitleName)

        setupNotificationHandler(this, title = title)

        defaultSetWebSettings(webView)

        var doublePress = false

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    with(webView) {
                        if (canGoBack()) {
                            if (doublePress) {
                                loadUrl(url)
                            }

                            doublePress = true
                            goBack()
                            Handler(Looper.getMainLooper()).postDelayed({
                                doublePress = false
                            }, 2000)
                        }
                    }
                }
            }
        )

        webView.webChromeClient = webChromeClient

        webView.webViewClient = WebViewClient(
            activity = this@WebActivity,
            openGameCullBack = {
                val intent = Intent(this@WebActivity, ExceptionClassData.exceptionClass!!)
                 startActivity(intent)
                 finish()
            },
            saveLinkCoolBack = { newUrl ->
                if (isSavingIsNotNecessary) return@WebViewClient

                url = newUrl

                isSavingIsNotNecessary = true

                CoroutineScope(Dispatchers.IO).launch {
                    CacheHandler.saveCacheUrl(this@WebActivity,newUrl)
                }
            },
            title = title,
        )
        webView.loadUrl(url)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101 && resultCode == RESULT_OK) {
            if (data == null) {
                webChromeClient.mFilePathCallback?.onReceiveValue(null)
                return
            }
            if (data.data != null) {
                val selectedImageUri = data.data
                if (selectedImageUri != null) {
                    webChromeClient.mFilePathCallback?.onReceiveValue(arrayOf(selectedImageUri))
                }
            } else {
                @Suppress("DEPRECATION") val bitmap = data.extras?.get("data") as Bitmap
                CoroutineScope(Dispatchers.Default).launch {
                    launch(Dispatchers.IO) {
                        myUri = createF(this@WebActivity, bitmap, isHighLvl)
                    }.join()

                    webChromeClient.mFilePathCallback?.onReceiveValue(arrayOf(myUri))
                }
            }
        } else {
            webChromeClient.mFilePathCallback?.onReceiveValue(null)
        }
    }

    @SuppressLint("InlinedApi")
    private fun createF(activity: Activity, bmp: Bitmap, isHighLvl: Boolean): Uri?{
        val imageCollection = when (isHighLvl) {
            true -> {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            }
            false -> {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        }
        val timeStamp = SimpleDateFormat.getDateInstance().format(Date())

        val imageFileName = "JPEG_" + timeStamp + "_"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$imageFileName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH, bmp.width)
            put(MediaStore.Images.Media.HEIGHT, bmp.height)
        }
        var myUri: Uri? = null
        return try {
            activity.contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                activity.contentResolver.openOutputStream(uri).use { outputStream ->
                    if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                        throw IOException("Image exception")
                    }
                    myUri = uri
                }
            } ?: throw IOException("Path exception")
            myUri
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}