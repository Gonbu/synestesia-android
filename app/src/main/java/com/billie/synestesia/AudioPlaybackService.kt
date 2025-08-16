package com.billie.synestesia

import android.content.Context
import android.media.MediaPlayer
import com.billie.synestesia.utils.LogUtils
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AudioPlaybackService {
    private var mediaPlayer: MediaPlayer? = null
    private var currentAudioUrl: String? = null
    private var progressJob: Job? = null

    // Callbacks pour la mise à jour de l'UI
    private var onProgressUpdate: ((Long) -> Unit)? = null
    private var onPlaybackComplete: (() -> Unit)? = null
    private var onError: ((String) -> Unit)? = null

    fun playAudio(
        context: Context,
        audioUrl: String,
        onProgress: (Long) -> Unit,
        onComplete: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            // Arrêter la lecture en cours
            stopAudio()

            // Mettre à jour les callbacks
            this.onProgressUpdate = onProgress
            this.onPlaybackComplete = onComplete
            this.onError = onError
            currentAudioUrl = audioUrl

            // Créer le MediaPlayer
            mediaPlayer = MediaPlayer().apply {
                setOnPreparedListener {
                    LogUtils.d("Audio prêt à être lu: $audioUrl")
                    start()

                    // Démarrer le suivi de la progression
                    startProgressTracking()
                }

                setOnCompletionListener {
                    LogUtils.d("Lecture audio terminée")
                    onPlaybackComplete?.invoke()
                    stopProgressTracking()
                }

                setOnErrorListener { _, what, extra ->
                    val errorMsg = "Erreur de lecture audio: what=$what, extra=$extra"
                    LogUtils.e(errorMsg)
                    onError?.invoke(errorMsg)
                    stopProgressTracking()
                    true
                }
            }

            // Préparer l'audio
            if (audioUrl.startsWith("http")) {
                // URL distante
                mediaPlayer?.setDataSource(audioUrl)
            } else {
                // Fichier local
                val file = File(audioUrl)
                if (file.exists()) {
                    mediaPlayer?.setDataSource(file.absolutePath)
                } else {
                    throw Exception("Fichier audio introuvable: $audioUrl")
                }
            }

            mediaPlayer?.prepareAsync()
        } catch (e: Exception) {
            val errorMsg = "Erreur lors de la préparation de l'audio: ${e.message}"
            LogUtils.e(errorMsg, e)
            onError?.invoke(errorMsg)
        }
    }

    fun pauseAudio() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                stopProgressTracking()
                LogUtils.d("Audio mis en pause")
            }
        }
    }

    fun resumeAudio() {
        mediaPlayer?.let { player ->
            if (!player.isPlaying) {
                player.start()
                startProgressTracking()
                LogUtils.d("Audio repris")
            }
        }
    }

    fun stopAudio() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.stop()
            }
            player.release()
        }
        mediaPlayer = null
        stopProgressTracking()
        currentAudioUrl = null
        LogUtils.d("Lecture audio arrêtée")
    }

    fun seekTo(positionMs: Long) {
        mediaPlayer?.let { player ->
            try {
                player.seekTo(positionMs.toInt())
                onProgressUpdate?.invoke(positionMs)
                LogUtils.d("Position audio changée: ${positionMs}ms")
            } catch (e: Exception) {
                LogUtils.e("Erreur lors du changement de position: ", e)
            }
        }
    }

    fun getCurrentPosition(): Long {
        return mediaPlayer?.currentPosition?.toLong() ?: 0L
    }

    fun getDuration(): Long {
        return mediaPlayer?.duration?.toLong() ?: 0L
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = CoroutineScope(Dispatchers.Main).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(100) // Mise à jour toutes les 100ms
                mediaPlayer?.let { player ->
                    val currentPos = player.currentPosition.toLong()
                    onProgressUpdate?.invoke(currentPos)
                }
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    fun cleanup() {
        stopAudio()
        onProgressUpdate = null
        onPlaybackComplete = null
        onError = null
    }

    // Méthodes publiques pour configurer les callbacks
    fun setProgressCallback(callback: (Long) -> Unit) {
        onProgressUpdate = callback
    }

    fun setCompletionCallback(callback: () -> Unit) {
        onPlaybackComplete = callback
    }

    fun setErrorCallback(callback: (String) -> Unit) {
        onError = callback
    }
}
