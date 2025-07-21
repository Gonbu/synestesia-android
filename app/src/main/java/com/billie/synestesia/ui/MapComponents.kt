package com.billie.synestesia.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MapContent(
    currentLatLng: LatLng?,
    onMapLoaded: () -> Unit,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState()
    val uiSettings = MapUiSettings(myLocationButtonEnabled = false)

    // État pour le marker ajouté par clic
    var clickedLatLng by remember { mutableStateOf<LatLng?>(null) }
    // État pour contrôler l'affichage du ModalBottomSheet
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            onMapLoaded = onMapLoaded,
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            onMapClick = { latLng ->
                clickedLatLng = latLng
                showBottomSheet = false
            }
        ) {
            // Marker pour la position actuelle
            currentLatLng?.let { latLng ->
                Marker(
                    state = MarkerState(position = latLng),
                    title = "Votre position",
                    snippet = "Vous êtes ici!"
                )
            }

            // Marker pour la position cliquée
            clickedLatLng?.let { latLng ->
                Marker(
                    state = MarkerState(position = latLng),
                    title = "Position sélectionnée",
                    snippet = "Cliquez pour plus d'infos",
                    onClick = {
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLng(latLng),
                                1000
                            )
                        }
                        showBottomSheet = true
                        true // Indique que le clic a été géré
                    }
                )
            }
        }

        if (currentLatLng != null) {
            LocationButton(
                currentLatLng = currentLatLng,
                coroutineScope = coroutineScope,
                cameraPositionState = cameraPositionState
            )
        }

        // Utilisation du ModalBottomSheet modifié
        ModalBottomSheetComponent(
            showSheet = showBottomSheet,
            onDismissRequest = { showBottomSheet = false }
        ) {
            SouvenirFormSheet(
                latLng = clickedLatLng,
                onSaveClick = { formData ->
                    println("Souvenir enregistré: ${formData.titre}")
                    showBottomSheet = false
                }
            )
        }
    }
}

@Composable
private fun LocationButton(
    currentLatLng: LatLng,
    coroutineScope: CoroutineScope,
    cameraPositionState: CameraPositionState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        IconButton(
            onClick = {
                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f),
                        1000
                    )
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Go to Current Location"
            )
        }
    }
}