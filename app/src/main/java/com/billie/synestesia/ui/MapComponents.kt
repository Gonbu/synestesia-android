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
import androidx.compose.runtime.rememberCoroutineScope
import com.billie.synestesia.FirestoreService
import com.billie.synestesia.models.SouvenirItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.LaunchedEffect
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptorFactory


@Composable
fun MapContent(
    currentLatLng: LatLng?,
    onMapLoaded: () -> Unit,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState()
    val uiSettings = MapUiSettings(myLocationButtonEnabled = false)

    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    var clickedLatLng by remember { mutableStateOf<LatLng?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    // Ajout : State pour stocker les souvenirs
    var souvenirs by remember { mutableStateOf<List<SouvenirItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    // Ajout : Fonction locale pour recharger les souvenirs
    suspend fun reloadSouvenirs() {
        isLoading = true
        try {
            val loaded = withContext(Dispatchers.IO) { FirestoreService.getAllSouvenirs() }
            souvenirs = loaded
        } catch (e: Exception) {
            android.widget.Toast.makeText(context, "Erreur lors du chargement des souvenirs", android.widget.Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    // Ajout : Chargement des souvenirs au chargement de la carte
    LaunchedEffect(Unit) {
        reloadSouvenirs()
    }

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
            // Marker pour la position actuelle (bleu)
            currentLatLng?.let { latLng ->
                val userIcon = BitmapDescriptorFactory.fromBitmap(createColoredMarkerBitmap("4285F4"))
                Marker(
                    state = MarkerState(position = latLng),
                    title = "Votre position",
                    snippet = "Vous êtes ici!",
                    icon = userIcon
                )
            }

            // Marker pour la position cliquée (orange)
            clickedLatLng?.let { latLng ->
                val clickIcon = BitmapDescriptorFactory.fromBitmap(createColoredMarkerBitmap("FFA500"))
                Marker(
                    state = MarkerState(position = latLng),
                    title = "Position sélectionnée",
                    snippet = "Cliquez pour plus d'infos",
                    icon = clickIcon,
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

            // Marqueurs pour chaque souvenir avec couleur personnalisée
            souvenirs.forEach { souvenir ->
                souvenir.toLatLng()?.let { pos ->
                    val markerIcon = BitmapDescriptorFactory.fromBitmap(createColoredMarkerBitmap(souvenir.couleur))
                    Marker(
                        state = MarkerState(position = pos),
                        title = souvenir.titre,
                        snippet = souvenir.description,
                        icon = markerIcon
                    )
                }
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
                    scope.launch {
                        try {
                            FirestoreService.addSouvenir(formData)
                            reloadSouvenirs() // Recharge la liste après ajout
                            android.widget.Toast.makeText(context, "Souvenir enregistré !", android.widget.Toast.LENGTH_SHORT).show()
                            showBottomSheet = false
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(context, "Erreur lors de l'enregistrement", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
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

// Fonction utilitaire pour générer un Bitmap coloré pour le marker
fun createColoredMarkerBitmap(colorHex: String): Bitmap {
    val size = 80 // taille du marker
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()
    // Ajoute le # si absent
    val hex = if (colorHex.startsWith("#")) colorHex else "#${colorHex}"
    paint.color = Color.parseColor(hex)
    paint.isAntiAlias = true
    canvas.drawCircle(size / 2f, size / 2f, size / 2.5f, paint)
    return bitmap
}
