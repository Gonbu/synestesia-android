package com.billie.synestesia

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.LatLng

import com.billie.synestesia.location.LocationManager
import com.billie.synestesia.permission.checkLocationPermissions
import com.billie.synestesia.permission.rememberLocationPermissionLauncher
import com.billie.synestesia.ui.MapContent

@Composable
fun SouvenirMap(
    paddingValues: PaddingValues,
) {
    var isMapLoaded by remember { mutableStateOf(false) }
    var currentLatLng by remember { mutableStateOf<LatLng?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val locationManager = remember { LocationManager(context) }

    val requestPermissionLauncher = rememberLocationPermissionLauncher(
        onPermissionGranted = {
            locationManager.getCurrentLocation { latLng ->
                currentLatLng = latLng
            }
        },
        onPermissionDenied = {
            println("Location permissions denied")
        }
    )

    // Initial check and request if needed
    LaunchedEffect(Unit) {
        if (!checkLocationPermissions(context)) {
            requestPermissionLauncher.launch(arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        } else {
            locationManager.getCurrentLocation { latLng ->
                currentLatLng = latLng
            }
        }
    }

    MapContent(
        currentLatLng = currentLatLng,
        onMapLoaded = { isMapLoaded = true },
        coroutineScope = coroutineScope,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    )
}
