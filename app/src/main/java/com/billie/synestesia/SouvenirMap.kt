package com.billie.synestesia

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.billie.synestesia.location.LocationManager
import com.billie.synestesia.permission.checkLocationPermissions
import com.billie.synestesia.permission.rememberLocationPermissionLauncher
import com.billie.synestesia.ui.MapContent
import com.billie.synestesia.utils.PermissionConstants
import com.google.android.gms.maps.model.LatLng

@Composable
fun SouvenirMap(paddingValues: PaddingValues) {
    var isMapLoaded by remember { mutableStateOf(false) }
    var currentLatLng by remember { mutableStateOf<LatLng?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val locationManager = remember { LocationManager(context) }

    val requestPermissionLauncher =
        rememberLocationPermissionLauncher(
            onPermissionGranted = {
                locationManager.getCurrentLocation { latLng -> currentLatLng = latLng }
            },
            onPermissionDenied = { println("Location permissions denied") }
        )

    // Initial check and request if needed
    LaunchedEffect(Unit) {
        if (!checkLocationPermissions(context)) {
            requestPermissionLauncher.launch(PermissionConstants.LOCATION_PERMISSIONS)
        } else {
            locationManager.getCurrentLocation { latLng -> currentLatLng = latLng }
        }
    }

    MapContent(
        currentLatLng = currentLatLng,
        onMapLoaded = { isMapLoaded = true },
        coroutineScope = coroutineScope,
        modifier = Modifier.fillMaxSize().padding(paddingValues)
    )
}
