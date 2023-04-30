package com.sesameworkshop.elabcs.helper.web.clients

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.sesameworkshop.elabcs.helper.util.decrypt

internal class WebViewClient(
    private val activity : Activity,
    private val openGameCullBack : () -> Unit,
    private val saveLinkCoolBack:(String)->Unit,
    private val title : String?,
) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (
            title == null ||
            view?.title?.contains(
               title
            ) == true

        ) {
            openGameCullBack()
        } else {
            if (url == null || url == "" || url == "null") {
                openGameCullBack()
            }else{
                saveLinkCoolBack(url)
            }
        }
    }

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        val url = request?.url?.toString() ?: return false
        return try {
            if (url.startsWith(
                    decrypt("lxxtw") +
                        "://${decrypt("x")}.${decrypt("qi")}" +
                        "/${decrypt("nsmrglex")}")) {

                Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    activity.startActivity(this)
                }
            }
            when {
                url.startsWith("${decrypt("lxxt")}://") || url.startsWith("${decrypt("lxxtw")}://") -> false
                else -> {
                    when {
                        url.startsWith("${decrypt("qempxs")}:") -> {
                            Intent(Intent.ACTION_SEND).apply {
                                type = "plain/text"
                                putExtra(
                                    Intent.EXTRA_EMAIL, url.replace("${decrypt("qempxs")}:", "")
                                )
                                Intent.createChooser(this, decrypt("Qemp")).run {
                                    activity.startActivity(this)
                                }
                            }
                        }
                        url.startsWith("${decrypt("xip")}:") -> {
                            Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse(url)
                                Intent.createChooser(this, decrypt("Gepp")).run {
                                    activity.startActivity(this)
                                }
                            }
                        }
                    }

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    activity.startActivity(intent)
                    true
                }
            }
        } catch (e: Exception) {
            true
        }
    }
}