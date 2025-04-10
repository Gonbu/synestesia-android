package com.example.synestesia.location

import android.content.Context
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationManager(private val context: Context) {
    fun getCurrentLocation(onLocationResult: (LatLng) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    onLocationResult(LatLng(it.latitude, it.longitude))
                } ?: run {
                    Log.e("Location", "Could not get location")
                    // Default location (Sydney)
                    onLocationResult(LatLng(-34.0, 151.0))
                }
            }
        } catch (e: SecurityException) {
            Log.e("Location", "Security Exception: ${e.message}")
        }
    }
} 
