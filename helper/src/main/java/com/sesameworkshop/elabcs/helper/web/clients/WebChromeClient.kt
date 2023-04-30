package com.sesameworkshop.elabcs.helper.web.clients

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class WebChromeClient(
    private val activity: Activity,
) : WebChromeClient() {
    private lateinit var takePictureIntent: Intent

    @SuppressLint("AnnotateVersionCheck")
    private val isVersionHigh = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    private var permissionList: List<String> = if (isVersionHigh){
        listOf(Manifest.permission.CAMERA)
    }else{
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }

    private var mCameraPhotoPath: String? = null

    private var myOwnFilePath = ValueCallback<Array<Uri?>?> { }

    internal var mFilePathCallback: ValueCallback<Array<Uri?>?>? = null

    internal var isAllPermissionsGranted = false

    override fun onShowFileChooser(
        view: WebView?,
        filePath: ValueCallback<Array<Uri?>?>,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        myOwnFilePath = filePath

        Dexter.withContext(activity)
            .withPermissions(permissionList)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    isAllPermissionsGranted = p0?.areAllPermissionsGranted() == true
                    openChooser()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }
            }).check()
        return true
    }

    @SuppressLint("InlinedApi", "QueryPermissionsNeeded")
    private fun openChooser() {

        mFilePathCallback = myOwnFilePath

        takePictureIntent =
            if (isAllPermissionsGranted) {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            } else {
                Intent(MediaStore.ACTION_PICK_IMAGES)
            }

        if (isVersionHigh) {
            CoroutineScope(Dispatchers.IO).launch {

                if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
                    try {
                        takePictureIntent.putExtra("tPI", mCameraPhotoPath)
                    } catch (_: IOException) { }
                }
            }
        }

        val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
        contentSelectionIntent.type = "image/*"
        val intentArray: Array<Intent?> =
            arrayOf(takePictureIntent)
        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Launcher")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
        activity.startActivityForResult(chooserIntent, 101)
    }
}
