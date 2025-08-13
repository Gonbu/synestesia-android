package com.billie.synestesia.models

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.Exclude
import java.sql.Date

data class SouvenirItem(
    @get:Exclude
    val id: String? = null,
    val titre: String = "",
    val description: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val date: Long = 0L,
    val couleur: String = "",
    val photo: String = ""
) {
    fun toLatLng(): LatLng? = if (latitude != null && longitude != null) LatLng(latitude, longitude) else null
}
