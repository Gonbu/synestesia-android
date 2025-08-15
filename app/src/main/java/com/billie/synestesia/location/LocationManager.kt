package com.billie.synestesia.location

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.billie.synestesia.utils.LogUtils
import com.billie.synestesia.utils.MessageConstants

class LocationManager(private val context: Context) {
    fun getCurrentLocation(onLocationResult: (LatLng) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    onLocationResult(LatLng(it.latitude, it.longitude))
                } ?: run {
                    LogUtils.e(MessageConstants.LOCATION_ERROR)
                    // Default location (Sydney)
                    onLocationResult(LatLng(-34.0, 151.0))
                }
            }
        } catch (e: SecurityException) {
            LogUtils.e("${MessageConstants.LOCATION_SECURITY_ERROR}${e.message}")
        }
    }
} 
