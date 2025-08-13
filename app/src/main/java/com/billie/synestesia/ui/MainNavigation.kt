package com.billie.synestesia.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.billie.synestesia.location.LocationManager
import com.billie.synestesia.permission.checkLocationPermissions
import com.billie.synestesia.permission.rememberLocationPermissionLauncher
import com.billie.synestesia.ui.MapContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainNavigation(
    paddingValues: PaddingValues
) {
    var currentRoute by remember { mutableStateOf(BottomNavItem.MAP) }
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigation(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    currentRoute = route
                }
            )
        }
    ) { innerPadding ->
        when (currentRoute) {
            BottomNavItem.MAP -> {
                MapContent(
                    currentLatLng = currentLatLng,
                    onMapLoaded = { isMapLoaded = true },
                    coroutineScope = coroutineScope,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            BottomNavItem.SETTINGS -> {
                SettingsScreen(
                    onNavigateBack = {
                        currentRoute = BottomNavItem.MAP
                    }
                )
            }
            BottomNavItem.PROFILE -> {
                ProfileScreen(
                    onNavigateBack = {
                        currentRoute = BottomNavItem.SETTINGS
                    }
                )
            }
        }
    }
}
