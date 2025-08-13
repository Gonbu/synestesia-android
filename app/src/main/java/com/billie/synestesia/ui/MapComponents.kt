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
    var selectedSouvenir by remember { mutableStateOf<SouvenirItem?>(null) }
    
    // Variables pour gérer plusieurs souvenirs par point
    var selectedSouvenirs by remember { mutableStateOf<List<SouvenirItem>>(emptyList()) }
    var currentSouvenirIndex by remember { mutableStateOf(0) }

    var souvenirs by remember { mutableStateOf<List<SouvenirItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    var addOnLatLng by remember { mutableStateOf<LatLng?>(null) }
    var showAddOnExistingPoint by remember { mutableStateOf(false) }

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
            currentLatLng?.let { latLng ->
                val userIcon = BitmapDescriptorFactory.fromBitmap(createColoredMarkerBitmap("4285F4"))
                Marker(
                    state = MarkerState(position = latLng),
                    title = "Votre position",
                    snippet = "Vous êtes ici!",
                    icon = userIcon
                )
            }

            clickedLatLng?.let { latLng ->
                val clickIcon = BitmapDescriptorFactory.fromBitmap(createColoredMarkerBitmap("BA68C8"))
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

            // Grouper les souvenirs par position
            val souvenirsByPosition = souvenirs.groupBy { it.toLatLng() }
            
            souvenirsByPosition.forEach { (pos, souvenirsAtPosition) ->
                val markerIcon = BitmapDescriptorFactory.fromBitmap(createColoredMarkerBitmap(souvenirsAtPosition.first().couleur))
                val title = if (souvenirsAtPosition.size == 1) {
                    souvenirsAtPosition.first().titre
                } else {
                    "${souvenirsAtPosition.size} souvenirs"
                }
                val snippet = if (souvenirsAtPosition.size == 1) {
                    souvenirsAtPosition.first().description
                } else {
                    "Cliquez pour voir tous les souvenirs"
                }
                
                Marker(
                    state = MarkerState(position = pos!!),
                    title = title,
                    snippet = snippet,
                    icon = markerIcon,
                    onClick = {
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLng(pos),
                                1000
                            )
                        }
                        selectedSouvenirs = souvenirsAtPosition
                        currentSouvenirIndex = 0
                        showBottomSheet = true
                        true
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

        ModalBottomSheetComponent(
            showSheet = showBottomSheet,
            onDismissRequest = {
                showBottomSheet = false
                selectedSouvenir = null
                selectedSouvenirs = emptyList()
                currentSouvenirIndex = 0
                showAddOnExistingPoint = false
                addOnLatLng = null
            },
            backgroundColor = when {
                selectedSouvenirs.isNotEmpty() -> {
                    val currentSouvenir = selectedSouvenirs[currentSouvenirIndex]
                    try { 
                        androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor("#${currentSouvenir.couleur.removePrefix("#")}")) 
                    } catch (e: Exception) { 
                        androidx.compose.ui.graphics.Color.LightGray 
                    }
                }
                showAddOnExistingPoint -> {
                    // Utiliser la couleur du souvenir existant sur ce point
                    val existingSouvenirs = souvenirs.filter { souvenir ->
                        souvenir.toLatLng()?.latitude == addOnLatLng?.latitude &&
                        souvenir.toLatLng()?.longitude == addOnLatLng?.longitude
                    }
                    if (existingSouvenirs.isNotEmpty()) {
                        try { 
                            androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor("#${existingSouvenirs.first().couleur.removePrefix("#")}")) 
                        } catch (e: Exception) { 
                            androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor("#E1BEE7"))
                        }
                    } else {
                        androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor("#E1BEE7"))
                    }
                }
                else -> {
                    // Couleur rose/violet pâle pour le formulaire d'ajout sur nouveau point
                    androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor("#F3E5F5"))
                }
            }
        ) {
            if (selectedSouvenirs.isNotEmpty()) {
                val currentSouvenir = selectedSouvenirs[currentSouvenirIndex]
                SouvenirDetailCard(
                    souvenirs = selectedSouvenirs,
                    currentIndex = currentSouvenirIndex,
                    onAddSouvenir = {
                        addOnLatLng = currentSouvenir.toLatLng()
                        showAddOnExistingPoint = true
                        selectedSouvenirs = emptyList() // Vider la liste pour afficher le formulaire
                        currentSouvenirIndex = 0
                    },
                    onDeleteSouvenir = {
                        scope.launch {
                            try {
                                FirestoreService.deleteSouvenir(currentSouvenir)
                                reloadSouvenirs()
                                android.widget.Toast.makeText(context, "Souvenir supprimé !", android.widget.Toast.LENGTH_SHORT).show()
                                
                                // Mettre à jour la liste des souvenirs sélectionnés
                                val updatedSouvenirs = selectedSouvenirs.filter { it.id != currentSouvenir.id }
                                if (updatedSouvenirs.isEmpty()) {
                                    showBottomSheet = false
                                    selectedSouvenirs = emptyList()
                                    currentSouvenirIndex = 0
                                } else {
                                    selectedSouvenirs = updatedSouvenirs
                                    if (currentSouvenirIndex >= updatedSouvenirs.size) {
                                        currentSouvenirIndex = updatedSouvenirs.size - 1
                                    }
                                }
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(context, "Erreur lors de la suppression", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onClose = {
                        showBottomSheet = false
                        selectedSouvenirs = emptyList()
                        currentSouvenirIndex = 0
                        showAddOnExistingPoint = false
                        addOnLatLng = null
                    },
                    onNavigateToSouvenir = { newIndex ->
                        currentSouvenirIndex = newIndex
                    }
                )
            } else if (showAddOnExistingPoint && addOnLatLng != null) {
                // Formulaire d'ajout sur un point existant
                SouvenirFormSheet(
                    latLng = addOnLatLng,
                    onSaveClick = { _ ->
                        scope.launch {
                            try {
                                // Le souvenir a déjà été créé dans le formulaire, on recharge juste la liste
                                reloadSouvenirs()
                                
                                // Mettre à jour la liste des souvenirs sélectionnés si on était sur un point existant
                                if (addOnLatLng != null) {
                                    val updatedSouvenirs = souvenirs.filter { souvenir ->
                                        souvenir.toLatLng()?.latitude == addOnLatLng?.latitude &&
                                        souvenir.toLatLng()?.longitude == addOnLatLng?.longitude
                                    }
                                    if (updatedSouvenirs.isNotEmpty()) {
                                        selectedSouvenirs = updatedSouvenirs
                                        currentSouvenirIndex = updatedSouvenirs.size - 1 // Aller au nouveau souvenir
                                    }
                                }
                                
                                android.widget.Toast.makeText(context, "Souvenir enregistré !", android.widget.Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(context, "Erreur lors de l'enregistrement", android.widget.Toast.LENGTH_SHORT).show()
                            }
                            showAddOnExistingPoint = false
                            addOnLatLng = null
                            if (selectedSouvenirs.isEmpty()) {
                                showBottomSheet = false
                            }
                        }
                    }
                )
            } else {
                // Affiche le formulaire d'ajout si aucun souvenir sélectionné
                SouvenirFormSheet(
                    latLng = clickedLatLng,
                    onSaveClick = { _ ->
                        scope.launch {
                            try {
                                // Le souvenir a déjà été créé dans le formulaire, on recharge juste la liste
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


