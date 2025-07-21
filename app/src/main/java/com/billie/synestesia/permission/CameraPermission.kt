package com.billie.synestesia.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat

@Composable
fun rememberCameraPermissionLauncher(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
    onResult = { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            Log.e("Camera", "Permissions caméra refusées")
            onPermissionDenied()
        }
    }
)

fun checkCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}