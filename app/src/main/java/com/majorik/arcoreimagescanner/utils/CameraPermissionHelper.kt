package com.majorik.arcoreimagescanner.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object CameraPermissionHelper {
    const val CAMERA_PERMISSION_CODE = 0
    const val CAMERA_PERMISSION = Manifest.permission.CAMERA
}