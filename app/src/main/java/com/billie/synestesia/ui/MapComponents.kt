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
import com.billie.synestesia.ui.ClusteringManager
import com.billie.synestesia.ui.Cluster
import com.billie.synestesia.ui.ClusterMarkerUtils
import com.billie.synestesia.ui.theme.AppColors
import com.billie.synestesia.ui.theme.ColorUtils
import com.billie.synestesia.utils.LogUtils
import com.billie.synestesia.utils.MessageConstants



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
    
    // Variables pour le clustering
    var clusters by remember { mutableStateOf<List<Cluster>>(emptyList()) }
    var currentZoomLevel by remember { mutableStateOf(10f) }
    var explodedClusters by remember { mutableStateOf<Set<String>>(emptySet()) }
    var subClusters by remember { mutableStateOf<Map<String, List<Cluster>>>(emptyMap()) }
    var lastReclusterTime by remember { mutableStateOf(System.currentTimeMillis()) }
    val clusteringManager = remember { ClusteringManager() }

    suspend fun reloadSouvenirs() {
        isLoading = true
        try {
            val loaded = withContext(Dispatchers.IO) { FirestoreService.getAllSouvenirs() }
            souvenirs = loaded
            // Créer les clusters avec les nouveaux souvenirs
            clusters = clusteringManager.createClusters(loaded)
            // Réinitialiser les clusters éclatés et sous-clusters
            explodedClusters = emptySet()
            subClusters = emptyMap()
        } catch (e: Exception) {
            LogUtils.showToast(context, MessageConstants.ERROR_LOADING_SOUVENIRS)
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        reloadSouvenirs()
    }
    
    // Écouter les changements de zoom pour ajuster le clustering
    LaunchedEffect(cameraPositionState.position.zoom) {
        val newZoomLevel = cameraPositionState.position.zoom
        val oldZoomLevel = currentZoomLevel
        currentZoomLevel = newZoomLevel
        
        // Logique de re-clustering plus intelligente
        if (explodedClusters.isNotEmpty()) {
            val currentTime = System.currentTimeMillis()
            val timeSinceLastRecluster = currentTime - lastReclusterTime
            
            val shouldRecluster = when {
                // Re-clustering forcé au dézoom significatif (2 niveaux)
                newZoomLevel < oldZoomLevel - 2f -> true
                // Re-clustering au zoom très faible (vue d'ensemble)
                newZoomLevel < 8f -> true
                // Re-clustering automatique après 10 secondes d'inactivité
                timeSinceLastRecluster > 10000 -> true
                else -> false
            }
            
            if (shouldRecluster) {
                explodedClusters = emptySet()
                lastReclusterTime = currentTime
                // Forcer le recalcul des clusters pour le re-clustering
                if (souvenirs.isNotEmpty()) {
                    clusters = clusteringManager.createClusters(souvenirs)
                }
            }
        }
    }
    
    // Écouter les changements de position de la caméra pour le re-clustering
    LaunchedEffect(cameraPositionState.position.target) {
        val newCameraPosition = cameraPositionState.position.target
        
        // Si on a des clusters éclatés et qu'on navigue loin, re-clusterer
        if (explodedClusters.isNotEmpty()) {
            val currentTime = System.currentTimeMillis()
            val timeSinceLastRecluster = currentTime - lastReclusterTime
            
            // Re-clustering si on navigue et qu'il y a eu une pause
            if (timeSinceLastRecluster > 8000) { // 8 secondes
                explodedClusters = emptySet()
                lastReclusterTime = currentTime
                // Forcer le recalcul des clusters
                if (souvenirs.isNotEmpty()) {
                    clusters = clusteringManager.createClusters(souvenirs)
                }
            }
        }
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
            // Marqueur de position utilisateur avec taille adaptée au zoom
            currentLatLng?.let { latLng ->
                val userIcon = BitmapDescriptorFactory.fromBitmap(
                    ClusterMarkerUtils.createUserLocationMarker(currentZoomLevel)
                )
                Marker(
                    state = MarkerState(position = latLng),
                    title = "Votre position",
                    snippet = "Vous êtes ici!",
                    icon = userIcon
                )
            }

            clickedLatLng?.let { latLng ->
                val clickIcon = BitmapDescriptorFactory.fromBitmap(
                    ClusterMarkerUtils.createIndividualMarker(
                        SouvenirItem(couleur = AppColors.CLICKED_POINT),
                        currentZoomLevel
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

            // Affichage des clusters et points individuels selon le niveau de zoom
            clusters.forEach { cluster ->
                if (cluster.items.size == 1) {
                    // Point individuel - toujours affiché
                    val souvenir = cluster.items.first()
                    val markerIcon = BitmapDescriptorFactory.fromBitmap(
                        ClusterMarkerUtils.createIndividualMarker(souvenir, currentZoomLevel)
                    )
                    
                    Marker(
                        state = MarkerState(position = cluster.center),
                        title = souvenir.titre,
                        snippet = souvenir.description,
                        icon = markerIcon,
                        onClick = {
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLng(cluster.center),
                                    1000
                                )
                            }
                            selectedSouvenirs = cluster.items
                            currentSouvenirIndex = 0
                            showBottomSheet = true
                            true
                        }
                    )
                } else {
                    // Cluster avec plusieurs éléments
                    val clusterId = cluster.items.first().id
                    if (clusterId == null) return@forEach // Ignorer les clusters sans ID
                    
                    if (cluster.isMultiSouvenirPoint) {
                        // Point multi-souvenirs - comportement spécial (pas d'éclatement)
                        val clusterIcon = BitmapDescriptorFactory.fromBitmap(
                            ClusterMarkerUtils.createClusterMarker(cluster, currentZoomLevel)
                        )
                        
                        Marker(
                            state = MarkerState(position = cluster.center),
                            title = "${cluster.items.size} souvenirs",
                            snippet = "Cliquez pour voir tous les souvenirs à cet endroit",
                            icon = clusterIcon,
                            onClick = {
                                // Pour les points multi-souvenirs, pas d'éclatement, juste afficher les détails
                                selectedSouvenirs = cluster.items
                                currentSouvenirIndex = 0
                                showBottomSheet = true
                                true
                            }
                        )
                    } else {
                        // Cluster géographique - comportement d'éclatement normal
                        val isExploded = explodedClusters.contains(clusterId)
                        
                        if (isExploded) {
                            // Afficher directement tous les points individuels du cluster
                            cluster.items.forEach { souvenir ->
                                val markerIcon = BitmapDescriptorFactory.fromBitmap(
                                    ClusterMarkerUtils.createIndividualMarker(souvenir, currentZoomLevel)
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
                        } else {
                            // Cluster principal non éclaté
                            val clusterIcon = BitmapDescriptorFactory.fromBitmap(
                                ClusterMarkerUtils.createClusterMarker(cluster, currentZoomLevel)
                            )
                            
                            Marker(
                                state = MarkerState(position = cluster.center),
                                title = "${cluster.items.size} souvenirs",
                                snippet = "Cliquez pour zoomer et voir les détails",
                                icon = clusterIcon,
                                onClick = {
                                    // Marquer ce cluster comme éclaté
                                    explodedClusters = explodedClusters + cluster.items.mapNotNull { it.id }.toSet()
                                    
                                    // Zoomer pour révéler les points individuels
                                    coroutineScope.launch {
                                        val bounds = cluster.bounds
                                        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(
                                            com.google.android.gms.maps.model.LatLngBounds(
                                                bounds.first, bounds.second
                                            ),
                                            150 // Padding en pixels pour bien voir tous les points
                                        )
                                        cameraPositionState.animate(cameraUpdate, 1000)
                                    }
                                    true
                                }
                            )
                        }
                    }
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
                    ColorUtils.hexToComposeColor(currentSouvenir.couleur)
                }
                showAddOnExistingPoint -> {
                    // Utiliser la couleur du souvenir existant sur ce point
                    val existingSouvenirs = souvenirs.filter { souvenir ->
                        souvenir.toLatLng()?.latitude == addOnLatLng?.latitude &&
                        souvenir.toLatLng()?.longitude == addOnLatLng?.longitude
                    }
                    if (existingSouvenirs.isNotEmpty()) {
                        ColorUtils.hexToComposeColor(existingSouvenirs.first().couleur)
                    } else {
                        ColorUtils.hexToComposeColor(AppColors.LIGHT_PURPLE)
                    }
                }
                else -> {
                    // Couleur rose/violet pâle pour le formulaire d'ajout sur nouveau point
                    ColorUtils.hexToComposeColor(AppColors.LIGHT_PINK)
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
                                LogUtils.showToast(context, MessageConstants.SOUVENIR_DELETED)
                                
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
                                LogUtils.showErrorToast(context, MessageConstants.ERROR_DELETING_SOUVENIR)
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
                                
                                LogUtils.showToast(context, MessageConstants.SOUVENIR_SAVED)
                            } catch (e: Exception) {
                                LogUtils.showErrorToast(context, MessageConstants.ERROR_SAVING_SOUVENIR)
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
                                LogUtils.showToast(context, MessageConstants.SOUVENIR_SAVED)
                                showBottomSheet = false
                            } catch (e: Exception) {
                                LogUtils.showErrorToast(context, MessageConstants.ERROR_SAVING_SOUVENIR)
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
                    // Zoomer sur la position utilisateur avec un niveau de zoom approprié
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f),
                        1000
                    )
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Aller à ma position"
            )
        }
    }
}




