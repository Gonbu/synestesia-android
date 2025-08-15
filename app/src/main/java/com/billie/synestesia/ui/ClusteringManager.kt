package com.billie.synestesia.ui

import com.google.android.gms.maps.model.LatLng
import com.billie.synestesia.models.SouvenirItem
import kotlin.math.*

data class Cluster(
    val center: LatLng,
    val items: List<SouvenirItem>,
    val bounds: Pair<LatLng, LatLng>,
    val isMultiSouvenirPoint: Boolean = false // True si tous les souvenirs ont exactement les mêmes coordonnées
)

class ClusteringManager {
    
    companion object {
        private const val CLUSTER_RADIUS_KM = 2.0 // Rayon de clustering en km (augmenté)
        private const val MIN_CLUSTER_SIZE = 2 // Taille minimale pour former un cluster
        private const val MAX_CLUSTER_SIZE = 20 // Taille maximale d'un cluster
    }
    
    /**
     * Calcule la distance entre deux points en km
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Rayon de la Terre en km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
    
    /**
     * Regroupe les souvenirs en clusters avec un algorithme plus intelligent
     * Exclut les souvenirs aux mêmes coordonnées du clustering
     */
    fun createClusters(souvenirs: List<SouvenirItem>): List<Cluster> {
        if (souvenirs.isEmpty()) return emptyList()
        
        val clusters = mutableListOf<Cluster>()
        val processed = mutableSetOf<String>()
        
        // Séparer les souvenirs aux mêmes coordonnées (points multi-souvenirs)
        val souvenirsByPosition = souvenirs.groupBy { 
            "${it.latitude}_${it.longitude}" 
        }
        
        // Traiter d'abord les points multi-souvenirs (même coordonnées)
        souvenirsByPosition.forEach { (position, itemsAtPosition) ->
            if (itemsAtPosition.size > 1) {
                // Point multi-souvenirs - créer un cluster spécial
                val center = LatLng(itemsAtPosition.first().latitude!!, itemsAtPosition.first().longitude!!)
                val bounds = Pair(center, center)
                clusters.add(Cluster(center, itemsAtPosition, bounds, isMultiSouvenirPoint = true))
                
                // Marquer tous ces souvenirs comme traités
                itemsAtPosition.forEach { item ->
                    item.id?.let { id ->
                        processed.add(id)
                    }
                }
            }
        }
        
        // Maintenant traiter les souvenirs restants pour le clustering géographique
        val remainingSouvenirs = souvenirs.filter { it.id !in processed }
        
        if (remainingSouvenirs.isNotEmpty()) {
            // Trier les souvenirs par densité (plus de voisins = priorité)
            val souvenirsWithDensity = remainingSouvenirs.map { souvenir ->
                val neighborCount = remainingSouvenirs.count { other ->
                    other.id != souvenir.id && calculateDistance(
                        souvenir.latitude!!, souvenir.longitude!!,
                        other.latitude!!, other.longitude!!
                    ) <= CLUSTER_RADIUS_KM
                }
                souvenir to neighborCount
            }.sortedByDescending { it.second }
            
            // Créer des clusters en commençant par les zones les plus denses
            souvenirsWithDensity.forEach { (souvenir, _) ->
                if (souvenir.id in processed) return@forEach
                
                val nearbyItems = mutableListOf<SouvenirItem>()
                nearbyItems.add(souvenir)
                processed.add(souvenir.id!!)
                
                // Chercher les souvenirs proches de manière itérative
                var hasChanges = true
                while (hasChanges && nearbyItems.size < MAX_CLUSTER_SIZE) {
                    hasChanges = false
                    val currentItems = nearbyItems.toList()
                    
                    currentItems.forEach { currentItem ->
                        remainingSouvenirs.forEach { other ->
                            if (other.id !in processed && other.id != currentItem.id) {
                                val distance = calculateDistance(
                                    currentItem.latitude!!, currentItem.longitude!!,
                                    other.latitude!!, other.longitude!!
                                )
                                if (distance <= CLUSTER_RADIUS_KM) {
                                    nearbyItems.add(other)
                                    processed.add(other.id!!)
                                    hasChanges = true
                                }
                            }
                        }
                    }
                }
                
                if (nearbyItems.size >= MIN_CLUSTER_SIZE) {
                    // Créer un cluster géographique
                    val center = calculateClusterCenter(nearbyItems)
                    val bounds = calculateClusterBounds(nearbyItems)
                    clusters.add(Cluster(center, nearbyItems, bounds))
                } else if (nearbyItems.size == 1) {
                    // Point isolé - pas de cluster
                    val center = LatLng(nearbyItems[0].latitude!!, nearbyItems[0].longitude!!)
                    val bounds = Pair(center, center)
                    clusters.add(Cluster(center, nearbyItems, bounds))
                }
            }
        }
        
        return clusters
    }
    
    /**
     * Crée des sous-clusters pour un cluster donné selon le niveau de zoom
     */
    fun createSubClusters(cluster: Cluster, zoomLevel: Float): List<Cluster> {
        if (cluster.items.size <= 1) return listOf(cluster)
        
        return when {
            zoomLevel >= 18f -> {
                // Zoom très élevé : chaque item est un cluster séparé
                cluster.items.map { item ->
                    Cluster(
                        center = LatLng(item.latitude!!, item.longitude!!),
                        items = listOf(item),
                        bounds = Pair(
                            LatLng(item.latitude!!, item.longitude!!),
                            LatLng(item.latitude!!, item.longitude!!)
                        )
                    )
                }
            }
            zoomLevel >= 15f -> {
                // Zoom élevé : petits sous-clusters
                createSubClustersByDistance(cluster, CLUSTER_RADIUS_KM * 0.3)
            }
            zoomLevel >= 12f -> {
                // Zoom moyen : sous-clusters moyens
                createSubClustersByDistance(cluster, CLUSTER_RADIUS_KM * 0.6)
            }
            else -> listOf(cluster) // Zoom faible : garder le cluster principal
        }
    }
    
    /**
     * Crée des sous-clusters basés sur la distance
     */
    private fun createSubClustersByDistance(cluster: Cluster, maxDistance: Double): List<Cluster> {
        val items = cluster.items.toMutableList()
        val subClusters = mutableListOf<Cluster>()
        val processed = mutableSetOf<String>()
        
        while (items.isNotEmpty()) {
            val currentItem = items.first { it.id !in processed }
            val nearbyItems = mutableListOf(currentItem)
            processed.add(currentItem.id!!)
            items.remove(currentItem)
            
            // Chercher les items proches
            val iterator = items.iterator()
            while (iterator.hasNext()) {
                val item = iterator.next()
                if (item.id !in processed) {
                    val distance = calculateDistance(
                        currentItem.latitude!!, currentItem.longitude!!,
                        item.latitude!!, item.longitude!!
                    )
                    if (distance <= maxDistance) {
                        nearbyItems.add(item)
                        processed.add(item.id!!)
                        iterator.remove()
                    }
                }
            }
            
            if (nearbyItems.isNotEmpty()) {
                val center = calculateClusterCenter(nearbyItems)
                val bounds = calculateClusterBounds(nearbyItems)
                subClusters.add(Cluster(center, nearbyItems, bounds))
            }
        }
        
        return subClusters
    }
    
    /**
     * Calcule le centre d'un cluster
     */
    private fun calculateClusterCenter(items: List<SouvenirItem>): LatLng {
        val avgLat = items.map { it.latitude!! }.average()
        val avgLon = items.map { it.longitude!! }.average()
        return LatLng(avgLat, avgLon)
    }
    
    /**
     * Calcule les limites d'un cluster
     */
    private fun calculateClusterBounds(items: List<SouvenirItem>): Pair<LatLng, LatLng> {
        val minLat = items.minOf { it.latitude!! }
        val maxLat = items.maxOf { it.latitude!! }
        val minLon = items.minOf { it.longitude!! }
        val maxLon = items.maxOf { it.longitude!! }
        
        return Pair(
            LatLng(minLat, minLon),
            LatLng(maxLat, maxLon)
        )
    }
    
    /**
     * Détermine si un cluster doit être affiché selon le niveau de zoom
     */
    fun shouldShowCluster(cluster: Cluster, zoomLevel: Float): Boolean {
        return when {
            zoomLevel >= 18f -> true // Zoom très élevé : tout afficher
            zoomLevel >= 15f -> cluster.items.size <= 2 // Zoom élevé : clusters de 1-2 éléments
            zoomLevel >= 12f -> cluster.items.size <= 5 // Zoom moyen : clusters de 1-5 éléments
            zoomLevel >= 8f -> cluster.items.size <= 10 // Zoom faible : clusters de 1-10 éléments
            else -> true // Zoom très faible : tous les clusters
        }
    }
    
    /**
     * Calcule la taille optimale d'un marqueur selon le zoom
     */
    fun calculateMarkerSize(zoomLevel: Float, isCluster: Boolean): Int {
        val baseSize = when {
            isCluster -> 100 // Taille de base pour les clusters
            else -> 80 // Taille de base pour les points individuels
        }
        
        val zoomFactor = when {
            zoomLevel >= 18f -> 1.5f // Zoom très élevé
            zoomLevel >= 15f -> 1.2f // Zoom élevé
            zoomLevel >= 12f -> 1.0f // Zoom moyen
            zoomLevel >= 8f -> 0.8f  // Zoom faible
            else -> 0.6f // Zoom très faible
        }
        
        return (baseSize * zoomFactor).toInt()
    }
}
