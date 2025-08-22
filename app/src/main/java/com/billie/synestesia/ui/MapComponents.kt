package com.billie.synestesia.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.billie.synestesia.FirestoreService
import com.billie.synestesia.R
import com.billie.synestesia.models.SouvenirItem
import com.billie.synestesia.ui.theme.AppColors
import com.billie.synestesia.ui.theme.ColorUtils
import com.billie.synestesia.utils.LogUtils
import com.billie.synestesia.utils.MessageConstants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            LogUtils.showToast(context, MessageConstants.ERROR_LOADING_SOUVENIRS)
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { reloadSouvenirs() }

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
            // Marqueur de position utilisateur avec taille adaptée au zoom
            currentLatLng?.let { latLng ->
                val userIcon =
                    BitmapDescriptorFactory.fromBitmap(
                        createUserLocationMarker(cameraPositionState.position.zoom)
                    )
                Marker(
                    state = MarkerState(position = latLng),
                    title = "Votre position",
                    snippet = "Vous êtes ici!",
                    icon = userIcon
                )
            }

            clickedLatLng?.let { latLng ->
                val clickIcon =
                    BitmapDescriptorFactory.fromBitmap(
                        createIndividualMarker(
                            SouvenirItem(couleur = AppColors.CLICKED_POINT),
                            cameraPositionState.position.zoom
                        )
                    )
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

            // Affichage simple de tous les souvenirs
            souvenirs.forEach { souvenir ->
                val markerIcon =
                    BitmapDescriptorFactory.fromBitmap(
                        createIndividualMarker(souvenir, cameraPositionState.position.zoom)
                    )

                Marker(
                    state = MarkerState(position = souvenir.toLatLng()!!),
                    title = souvenir.titre,
                    snippet = souvenir.description,
                    icon = markerIcon,
                    onClick = {
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLng(souvenir.toLatLng()!!),
                                1000
                            )
                        }
                        selectedSouvenirs = listOf(souvenir)
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
                selectedSouvenirs = emptyList()
                currentSouvenirIndex = 0
                showAddOnExistingPoint = false
                addOnLatLng = null
            },
            backgroundColor =
            when {
                selectedSouvenirs.isNotEmpty() -> {
                    val currentSouvenir = selectedSouvenirs[currentSouvenirIndex]
                    ColorUtils.hexToComposeColor(currentSouvenir.couleur)
                }
                showAddOnExistingPoint -> {
                    // Utiliser la couleur du souvenir existant sur ce point
                    val existingSouvenirs =
                        souvenirs.filter { souvenir ->
                            souvenir.toLatLng()?.latitude ==
                                addOnLatLng?.latitude &&
                                souvenir.toLatLng()?.longitude ==
                                addOnLatLng?.longitude
                        }
                    if (existingSouvenirs.isNotEmpty()) {
                        ColorUtils.hexToComposeColor(existingSouvenirs.first().couleur)
                    } else {
                        ColorUtils.hexToComposeColor(AppColors.LIGHT_PURPLE)
                    }
                }
                else -> {
                    // Couleur rose/violet pâle pour le formulaire d'ajout sur nouveau
                    // point
                    ColorUtils.hexToComposeColor(AppColors.LIGHT_PINK)
                }
            }
        ) {
            if (selectedSouvenirs.isNotEmpty()) {
                val currentSouvenir = selectedSouvenirs[currentSouvenirIndex]
                souvenirDetailCard(
                    souvenirs = selectedSouvenirs,
                    currentIndex = currentSouvenirIndex,
                    onAddSouvenir = {
                        addOnLatLng = currentSouvenir.toLatLng()
                        showAddOnExistingPoint = true
                        selectedSouvenirs =
                            emptyList() // Vider la liste pour afficher le formulaire
                        currentSouvenirIndex = 0
                    },
                    onDeleteSouvenir = {
                        scope.launch {
                            try {
                                // Vérifier que l'utilisateur est bien le propriétaire
                                val currentUser =
                                    com.google.firebase.auth.FirebaseAuth.getInstance()
                                        .currentUser
                                if (currentUser?.uid != currentSouvenir.userId) {
                                    LogUtils.showErrorToast(
                                        context,
                                        "Vous ne pouvez pas supprimer ce souvenir"
                                    )
                                    return@launch
                                }

                                FirestoreService.deleteSouvenir(currentSouvenir)
                                reloadSouvenirs()
                                LogUtils.showToast(context, MessageConstants.SOUVENIR_DELETED)

                                // Mettre à jour la liste des souvenirs sélectionnés
                                val updatedSouvenirs =
                                    selectedSouvenirs.filter { it.id != currentSouvenir.id }
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
                                LogUtils.e("Erreur lors de la suppression: ", e)
                                LogUtils.showErrorToast(
                                    context,
                                    MessageConstants.ERROR_DELETING_SOUVENIR
                                )
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
                    onNavigateToSouvenir = { newIndex -> currentSouvenirIndex = newIndex }
                )
            } else if (showAddOnExistingPoint && addOnLatLng != null) {
                // Formulaire d'ajout sur un point existant
                souvenirFormSheet(
                    latLng = addOnLatLng,
                    onSaveClick = { _ ->
                        scope.launch {
                            try {
                                // Le souvenir a déjà été créé dans le formulaire, on recharge
                                // juste la liste
                                reloadSouvenirs()

                                // Mettre à jour la liste des souvenirs sélectionnés si on était
                                // sur un point existant
                                if (addOnLatLng != null) {
                                    val updatedSouvenirs =
                                        souvenirs.filter { souvenir ->
                                            souvenir.toLatLng()?.latitude ==
                                                addOnLatLng?.latitude &&
                                                souvenir.toLatLng()?.longitude ==
                                                addOnLatLng?.longitude
                                        }
                                    if (updatedSouvenirs.isNotEmpty()) {
                                        selectedSouvenirs = updatedSouvenirs
                                        currentSouvenirIndex =
                                            updatedSouvenirs.size -
                                            1 // Aller au nouveau souvenir
                                    }
                                }

                                LogUtils.showToast(context, MessageConstants.SOUVENIR_SAVED)
                            } catch (e: Exception) {
                                LogUtils.showErrorToast(
                                    context,
                                    MessageConstants.ERROR_SAVING_SOUVENIR
                                )
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
                souvenirFormSheet(
                    latLng = clickedLatLng,
                    onSaveClick = { _ ->
                        scope.launch {
                            try {
                                // Le souvenir a déjà été créé dans le formulaire, on recharge
                                // juste la liste
                                reloadSouvenirs() // Recharge la liste après ajout
                                LogUtils.showToast(context, MessageConstants.SOUVENIR_SAVED)
                                showBottomSheet = false
                            } catch (e: Exception) {
                                LogUtils.showErrorToast(
                                    context,
                                    MessageConstants.ERROR_SAVING_SOUVENIR
                                )
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
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        IconButton(
            onClick = {
                coroutineScope.launch {
                    // Zoomer sur la position utilisateur avec un niveau de zoom approprié
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f),
                        1000
                    )
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_crosshair),
                contentDescription = "Aller à ma position"
            )
        }
    }
}

// Fonctions simples de création de marqueurs (remplacement du clustering)
private fun createUserLocationMarker(zoomLevel: Float): Bitmap {
    val size = calculateMarkerSize(zoomLevel, false)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint =
        Paint().apply {
            color = Color.BLUE
            isAntiAlias = true
            style = Paint.Style.FILL
        }

    // Cercle bleu pour la position utilisateur
    canvas.drawCircle(size / 2f, size / 2f, size / 3f, paint)

    // Bordure blanche
    paint.color = Color.WHITE
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = size / 20f
    canvas.drawCircle(size / 2f, size / 2f, size / 3f, paint)

    return bitmap
}

private fun createIndividualMarker(souvenir: SouvenirItem, zoomLevel: Float): Bitmap {
    val size = calculateMarkerSize(zoomLevel, false)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint =
        Paint().apply {
            color = ColorUtils.hexToAndroidColor(souvenir.couleur)
            isAntiAlias = true
            style = Paint.Style.FILL
        }

    // Cercle coloré pour le souvenir
    canvas.drawCircle(size / 2f, size / 2f, size / 3f, paint)

    // Bordure blanche
    paint.color = Color.WHITE
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = size / 20f
    canvas.drawCircle(size / 2f, size / 2f, size / 3f, paint)

    return bitmap
}

private fun calculateMarkerSize(zoomLevel: Float, isCluster: Boolean): Int {
    return when {
        zoomLevel >= 15f -> 60 // Zoom élevé : marqueurs petits
        zoomLevel >= 12f -> 70 // Zoom moyen : marqueurs moyens
        zoomLevel >= 8f -> 80 // Zoom faible : marqueurs grands
        else -> 90 // Zoom très faible : marqueurs très grands
    }
}
