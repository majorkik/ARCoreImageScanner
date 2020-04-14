package com.majorik.arcoreimagescanner.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.majorik.arcoreimagescanner.utils.CameraPermissionHelper

fun Activity.setFullScreenOnWindowFocusChanged(
    hasFocus: Boolean
) {
    if (hasFocus) {
        // https://developer.android.com/training/system-ui/immersive.html#sticky
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

fun Activity.launchPermissionSettings() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    intent.data = Uri.fromParts("package", packageName, null)
    startActivity(intent)
}

fun Activity.hasCameraPermission(): Boolean {
    return (ContextCompat.checkSelfPermission(
        this,
        CameraPermissionHelper.CAMERA_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED)
}

fun Activity.requestCameraPermission() {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(CameraPermissionHelper.CAMERA_PERMISSION),
        CameraPermissionHelper.CAMERA_PERMISSION_CODE
    )
}

fun Activity.shouldShowRequestPermissionRationale(): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(
        this,
        CameraPermissionHelper.CAMERA_PERMISSION
    )
}