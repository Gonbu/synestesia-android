package com.billie.synestesia

import android.media.MediaMetadataRetriever
import com.billie.synestesia.utils.LogUtils
import java.io.File

object AudioMetadataService {

    /**
     * Récupère la durée d'un fichier audio en millisecondes
     * @param audioPath Chemin local du fichier audio
     * @return Durée en millisecondes, ou 0L si impossible à récupérer
     */
    fun getAudioDuration(audioPath: String): Long {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(audioPath)

            val durationStr = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )
            val duration = durationStr?.toLongOrNull() ?: 0L

            retriever.release()
            duration
        } catch (e: Exception) {
            LogUtils.e("Erreur lors de la récupération de la durée audio: ", e)
            0L
        }
    }

    /**
     * Récupère la durée d'un fichier audio depuis une URL
     * @param audioUrl URL du fichier audio
     * @return Durée en millisecondes, ou 0L si impossible à récupérer
     */
    fun getAudioDurationFromUrl(audioUrl: String): Long {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(audioUrl, HashMap())

            val durationStr = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )
            val duration = durationStr?.toLongOrNull() ?: 0L

            retriever.release()
            duration
        } catch (e: Exception) {
            LogUtils.e("Erreur lors de la récupération de la durée audio depuis URL: ", e)
            0L
        }
    }

    /**
     * Récupère les métadonnées complètes d'un fichier audio
     * @param audioPath Chemin local du fichier audio
     * @return Map des métadonnées, ou map vide si impossible à récupérer
     */
    fun getAudioMetadata(audioPath: String): Map<String, String> {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(audioPath)

            val metadata = mutableMapOf<String, String>()

            // Durée
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.let {
                metadata["duration"] = it
            }

            // Format
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)?.let {
                metadata["mimetype"] = it
            }

            // Taille du fichier
            try {
                val file = File(audioPath)
                if (file.exists()) {
                    metadata["filesize"] = file.length().toString()
                }
            } catch (e: Exception) {
                LogUtils.e("Erreur lors de la récupération de la taille du fichier: ", e)
            }

            retriever.release()
            metadata
        } catch (e: Exception) {
            LogUtils.e("Erreur lors de la récupération des métadonnées audio: ", e)
            emptyMap()
        }
    }
}
