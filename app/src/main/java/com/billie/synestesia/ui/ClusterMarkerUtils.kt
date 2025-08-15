package com.billie.synestesia.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.billie.synestesia.models.SouvenirItem
import com.billie.synestesia.ui.theme.AppColors
import com.billie.synestesia.ui.theme.ColorUtils

object ClusterMarkerUtils {
    
    /**
     * Crée un marqueur de cluster avec le nombre d'éléments
     */
    fun createClusterMarker(cluster: Cluster, zoomLevel: Float): Bitmap {
        val size = ClusteringManager().calculateMarkerSize(zoomLevel, true)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Couleur de base du cluster (mélange des couleurs des souvenirs)
        val clusterColor = calculateClusterColor(cluster.items)
        
        // Dessiner le cercle principal
        val paint = Paint().apply {
            color = clusterColor
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        val centerX = size / 2f
        val centerY = size / 2f
        val radius = size / 2.5f
        
        canvas.drawCircle(centerX, centerY, radius, paint)
        
        // Bordure blanche
        val borderPaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawCircle(centerX, centerY, radius, borderPaint)
        
        // Texte du nombre d'éléments
        val textPaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            textSize = size * 0.3f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
        
        val text = cluster.items.size.toString()
        val textBounds = Rect()
        textPaint.getTextBounds(text, 0, text.length, textBounds)
        
        val textY = centerY + (textBounds.height() / 2f)
        canvas.drawText(text, centerX, textY, textPaint)
        
        return bitmap
    }
    
    /**
     * Crée un marqueur pour un point individuel avec une taille adaptée au zoom
     */
    fun createIndividualMarker(souvenir: SouvenirItem, zoomLevel: Float): Bitmap {
        val size = ClusteringManager().calculateMarkerSize(zoomLevel, false)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        val paint = Paint().apply {
            color = ColorUtils.hexToAndroidColor(souvenir.couleur)
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        val centerX = size / 2f
        val centerY = size / 2f
        val radius = size / 2.5f
        
        canvas.drawCircle(centerX, centerY, radius, paint)
        
        // Bordure blanche
        val borderPaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawCircle(centerX, centerY, radius, borderPaint)
        
        return bitmap
    }
    
    /**
     * Calcule une couleur représentative pour le cluster
     */
    private fun calculateClusterColor(items: List<SouvenirItem>): Int {
        if (items.isEmpty()) return Color.GRAY
        
        // Prendre la couleur du premier souvenir ou une couleur par défaut
        return ColorUtils.hexToAndroidColor(items.first().couleur)
    }
    
    /**
     * Crée un marqueur spécial pour la position de l'utilisateur
     */
    fun createUserLocationMarker(zoomLevel: Float): Bitmap {
        val size = ClusteringManager().calculateMarkerSize(zoomLevel, false)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Cercle bleu pour l'utilisateur
        val paint = Paint().apply {
            color = ColorUtils.hexToAndroidColor(AppColors.USER_LOCATION)
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        val centerX = size / 2f
        val centerY = size / 2f
        val radius = size / 2.5f
        
        canvas.drawCircle(centerX, centerY, radius, paint)
        
        // Bordure blanche plus épaisse pour l'utilisateur
        val borderPaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawCircle(centerX, centerY, radius, borderPaint)
        
        // Point central blanc
        val centerPaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        canvas.drawCircle(centerX, centerY, radius * 0.3f, centerPaint)
        
        return bitmap
    }
}
