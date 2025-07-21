package com.billie.synestesia.models

import com.google.android.gms.maps.model.LatLng
import java.sql.Date

data class SouvenirItem(
    val titre: String,
    val description: String,
    val position: LatLng?,
    val date: Date,
    val couleur: String,
    val photo: String
)
