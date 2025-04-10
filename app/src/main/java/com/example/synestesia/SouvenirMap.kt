package com.example.synestesia

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.synestesia.location.LocationManager
import com.example.synestesia.permission.checkLocationPermissions
import com.example.synestesia.permission.rememberLocationPermissionLauncher
import com.example.synestesia.ui.MapContent
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

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
            // Handle permission denial
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
