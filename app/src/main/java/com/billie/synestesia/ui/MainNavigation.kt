package com.billie.synestesia.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.billie.synestesia.location.LocationManager
import com.billie.synestesia.permission.checkLocationPermissions
import com.billie.synestesia.permission.rememberLocationPermissionLauncher
import com.billie.synestesia.utils.PermissionConstants
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

@Composable
fun mainNavigation(paddingValues: PaddingValues) {
    var currentRoute by remember { mutableStateOf(BottomNavItem.MAP) }
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigation(
                currentRoute = currentRoute,
                onNavigate = { route -> currentRoute = route }
            )
        }
    ) { innerPadding ->
        when (currentRoute) {
            BottomNavItem.MAP -> {
                MapContent(
                    currentLatLng = currentLatLng,
                    onMapLoaded = { isMapLoaded = true },
                    coroutineScope = coroutineScope,
                    modifier = Modifier.fillMaxSize().padding(innerPadding)
                )
            }
            BottomNavItem.SETTINGS -> {
                settingsScreen(onNavigateBack = { currentRoute = BottomNavItem.MAP })
            }
            BottomNavItem.PROFILE -> {
                profileScreen(onNavigateBack = { currentRoute = BottomNavItem.SETTINGS })
            }
        }
    }
}
