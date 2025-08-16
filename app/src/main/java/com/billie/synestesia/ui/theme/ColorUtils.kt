package com.billie.synestesia.ui.theme

import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color

object ColorUtils {
    /**
     * Convertit une couleur hexadécimale en Color Compose
     */
    fun hexToComposeColor(hexColor: String): Color {
        return try {
            Color(AndroidColor.parseColor(hexColor))
        } catch (e: Exception) {
            Color.Gray
        }
    }

    /**
     * Convertit une couleur hexadécimale en Color Android
     */
    fun hexToAndroidColor(hexColor: String): Int {
        return try {
            AndroidColor.parseColor(hexColor)
        } catch (e: Exception) {
            AndroidColor.GRAY
        }
    }

    /**
     * Nettoie une couleur hexadécimale en ajoutant # si nécessaire
     */
    fun cleanHexColor(hexColor: String): String {
        return if (hexColor.startsWith("#")) hexColor else "#$hexColor"
    }
}
