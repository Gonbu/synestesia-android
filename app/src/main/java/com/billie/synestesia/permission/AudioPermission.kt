package com.billie.synestesia.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object AudioPermission {

    /**
     * Vérifie si la permission d'enregistrement audio est accordée
     */
    fun checkAudioPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Vérifie si toutes les permissions audio nécessaires sont accordées
     */
    fun checkAllAudioPermissions(context: Context): Boolean {
        return checkAudioPermission(context)
    }
}
