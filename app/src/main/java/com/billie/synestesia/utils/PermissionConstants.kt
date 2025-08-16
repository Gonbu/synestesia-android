package com.billie.synestesia.utils

import android.Manifest

object PermissionConstants {
    // Permissions de localisation
    const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    const val ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION

    // Permissions de caméra
    const val CAMERA = Manifest.permission.CAMERA

    // Tableaux de permissions
    val LOCATION_PERMISSIONS = arrayOf(
        ACCESS_FINE_LOCATION,
        ACCESS_COARSE_LOCATION
    )

    val CAMERA_PERMISSIONS = arrayOf(CAMERA)
}
