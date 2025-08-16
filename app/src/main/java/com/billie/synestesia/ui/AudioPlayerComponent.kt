package com.billie.synestesia.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.billie.synestesia.AudioMetadataService
import com.billie.synestesia.AudioPlaybackService
import com.billie.synestesia.R
import com.billie.synestesia.utils.AudioConstants
import com.billie.synestesia.utils.LogUtils

@Composable
fun AudioPlayerComponent(audioUrl: String, modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Récupérer la vraie durée de l'audio
    LaunchedEffect(audioUrl) {
        if (audioUrl.isNotEmpty()) {
            try {
                val audioDuration = AudioMetadataService.getAudioDurationFromUrl(audioUrl)
                if (audioDuration > 0) {
                    duration = audioDuration
                    LogUtils.d("Durée audio récupérée: ${duration}ms")
                } else {
                    // Fallback si impossible de récupérer la durée
                    duration = 10000L // 10 secondes par défaut
                    LogUtils.w(
                        "Impossible de récupérer la durée audio, utilisation de la valeur par défaut"
                    )
                }
            } catch (e: Exception) {
                LogUtils.e("Erreur lors de la récupération de la durée audio: ", e)
                duration = 10000L // 10 secondes par défaut
            }
        }
    }

    // Gestion de la lecture audio
    LaunchedEffect(Unit) {
        AudioPlaybackService.setProgressCallback { position -> currentPosition = position }

        AudioPlaybackService.setCompletionCallback {
            isPlaying = false
            isPaused = false
            currentPosition = 0L
        }

        AudioPlaybackService.setErrorCallback { error ->
            LogUtils.e("Erreur de lecture audio: $error")
            isPlaying = false
            isPaused = false
        }
    }

    // Nettoyage à la destruction du composant
    DisposableEffect(Unit) { onDispose { AudioPlaybackService.cleanup() } }

    // Format du temps
    val formatTime = { timeMs: Long ->
        val seconds = (timeMs / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        String.format("%02d:%02d", minutes, remainingSeconds)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sound_on),
                    contentDescription = "Audio",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = AudioConstants.LABEL_AUDIO_PLAYBACK,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Barre de progression
            LinearProgressIndicator(
                progress =
                if (duration > 0) {
                    (currentPosition.toFloat() / duration.toFloat())
                } else {
                    0f
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Temps et contrôles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${formatTime(currentPosition)} / ${formatTime(duration)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Bouton Play/Pause avec icône SVG personnalisée
                    IconButton(
                        onClick = {
                            if (isPlaying && !isPaused) {
                                // Mettre en pause
                                AudioPlaybackService.pauseAudio()
                                isPaused = true
                                LogUtils.d("Audio mis en pause")
                            } else if (isPlaying && isPaused) {
                                // Reprendre
                                AudioPlaybackService.resumeAudio()
                                isPaused = false
                                LogUtils.d("Audio repris")
                            } else {
                                // Démarrer la lecture
                                isPlaying = true
                                isPaused = false
                                currentPosition = 0L
                                AudioPlaybackService.playAudio(
                                    context = context,
                                    audioUrl = audioUrl,
                                    onProgress = { position -> currentPosition = position },
                                    onComplete = {
                                        isPlaying = false
                                        isPaused = false
                                        currentPosition = 0L
                                    },
                                    onError = { error ->
                                        LogUtils.e("Erreur de lecture: $error")
                                        isPlaying = false
                                        isPaused = false
                                    }
                                )
                                LogUtils.d("Lecture audio démarrée: $audioUrl")
                            }
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter =
                            painterResource(
                                id =
                                if (isPlaying && !isPaused) {
                                    R.drawable.ic_pause
                                } else {
                                    R.drawable.ic_play
                                }
                            ),
                            contentDescription =
                            if (isPlaying && !isPaused) "Pause" else "Lecture",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Bouton Stop avec icône SVG personnalisée
                    IconButton(
                        onClick = {
                            AudioPlaybackService.stopAudio()
                            isPlaying = false
                            isPaused = false
                            currentPosition = 0L
                            LogUtils.d("Lecture audio arrêtée")
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_stop),
                            contentDescription = "Arrêter",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}
