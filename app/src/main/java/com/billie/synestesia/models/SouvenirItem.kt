package com.billie.synestesia.models

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.Exclude

data class SouvenirItem(
    @get:Exclude
    val id: String? = null,
    val titre: String = "",
    val description: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val date: Long = 0L,
    val couleur: String = "",
    val photo: String = "",
    val audio: String = "", // URL du fichier audio
    val userId: String = "" // ID de l'utilisateur propriétaire du souvenir
) {
    fun toLatLng(): LatLng? = if (latitude != null && longitude != null) LatLng(latitude, longitude) else null
}
