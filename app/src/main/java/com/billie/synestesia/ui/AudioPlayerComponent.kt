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
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

data class AudioPlayerState(
    val currentPosition: Long,
    val duration: Long,
    val isPlaying: Boolean,
    val isPaused: Boolean
)

data class AudioPlayerCallbacks(val onPlayPause: () -> Unit, val onStop: () -> Unit)

@Composable
private fun audioPlayerHeader() {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
}

@Composable
private fun audioPlayerProgress(currentPosition: Long, duration: Long) {
    LinearProgressIndicator(
        progress = {
            if (duration > 0) {
                (currentPosition.toFloat() / duration.toFloat())
            } else {
                0f
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun audioPlayerTimeDisplay(
    currentPosition: Long,
    duration: Long,
    formatTime: (Long) -> String
) {
    Text(
        text = "${formatTime(currentPosition)} / ${formatTime(duration)}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun audioPlayerButton(iconId: Int, contentDescription: String, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(48.dp)) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun audioPlayerTimeDisplay(currentPosition: Long, duration: Long) {
    val formatTime = { timeMs: Long ->
        val seconds = (timeMs / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        String.format("%02d:%02d", minutes, remainingSeconds)
    }

    Text(
        text = "${formatTime(currentPosition)} / ${formatTime(duration)}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun audioPlayerControls(state: AudioPlayerState, callbacks: AudioPlayerCallbacks) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        audioPlayerTimeDisplay(state.currentPosition, state.duration)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            audioPlayerButton(
                iconId =
                if (state.isPlaying && !state.isPaused) {
                    R.drawable.ic_pause
                } else {
                    R.drawable.ic_play
                },
                contentDescription =
                if (state.isPlaying && !state.isPaused) "Pause" else "Lecture",
                onClick = callbacks.onPlayPause
            )
            audioPlayerButton(
                iconId = R.drawable.ic_stop,
                contentDescription = "Arrêter",
                onClick = callbacks.onStop
            )
        }
    }
}

@Composable
private fun audioPlayerState(
    audioUrl: String,
    onDurationLoaded: (Long) -> Unit,
    onProgressUpdate: (Long) -> Unit,
    onPlaybackComplete: () -> Unit,
    onPlaybackError: (String) -> Unit
) {
    // Récupérer la vraie durée de l'audio
    LaunchedEffect(audioUrl) {
        if (audioUrl.isNotEmpty()) {
            try {
                val audioDuration = AudioMetadataService.getAudioDurationFromUrl(audioUrl)
                if (audioDuration > 0) {
                    onDurationLoaded(audioDuration)
                    LogUtils.d("Durée audio récupérée: ${audioDuration}ms")
                } else {
                    onDurationLoaded(10000L) // 10 secondes par défaut
                    LogUtils.w(
                        "Impossible de récupérer la durée audio, utilisation de la valeur par défaut"
                    )
                }
            } catch (e: Exception) {
                LogUtils.e("Erreur lors de la récupération de la durée audio: ", e)
                onDurationLoaded(10000L) // 10 secondes par défaut
            }
        }
    }

    // Gestion de la lecture audio
    LaunchedEffect(Unit) {
        AudioPlaybackService.setProgressCallback { position -> onProgressUpdate(position) }
        AudioPlaybackService.setCompletionCallback { onPlaybackComplete() }
        AudioPlaybackService.setErrorCallback { error -> onPlaybackError(error) }
    }

    // Nettoyage à la destruction du composant
    DisposableEffect(Unit) { onDispose { AudioPlaybackService.cleanup() } }
}

@Composable
private fun audioPlayerPlaybackLogic(
    state: AudioPlayerState,
    audioUrl: String,
    context: android.content.Context,
    onStateChange: (Boolean, Boolean, Long) -> Unit
) {
    val handlePlayPause = {
        if (state.isPlaying && !state.isPaused) {
            AudioPlaybackService.pauseAudio()
            onStateChange(state.isPlaying, true, 0L)
            LogUtils.d("Audio mis en pause")
        } else if (state.isPlaying && state.isPaused) {
            AudioPlaybackService.resumeAudio()
            onStateChange(state.isPlaying, false, 0L)
            LogUtils.d("Audio repris")
        } else {
            onStateChange(true, false, 0L)
            AudioPlaybackService.playAudio(
                context = context,
                audioUrl = audioUrl,
                onProgress = { position ->
                    onStateChange(state.isPlaying, state.isPaused, position)
                },
                onComplete = { onStateChange(false, false, 0L) },
                onError = { error ->
                    LogUtils.e("Erreur de lecture: $error")
                    onStateChange(false, false, 0L)
                }
            )
            LogUtils.d("Lecture audio démarrée: $audioUrl")
        }
    }

    val handleStop = {
        AudioPlaybackService.stopAudio()
        onStateChange(false, false, 0L)
    }

    audioPlayerControls(
        state = state,
        callbacks = AudioPlayerCallbacks(onPlayPause = handlePlayPause, onStop = handleStop)
    )
}

@Composable
fun audioPlayerComponent(audioUrl: String, modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    val context = LocalContext.current

    // Gestion des événements audio
    audioPlayerState(
        audioUrl = audioUrl,
        onDurationLoaded = { duration = it },
        onProgressUpdate = { currentPosition = it },
        onPlaybackComplete = {
            isPlaying = false
            isPaused = false
            currentPosition = 0L
        },
        onPlaybackError = { error ->
            LogUtils.e("Erreur de lecture audio: $error")
            isPlaying = false
            isPaused = false
        }
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            audioPlayerHeader()
            Spacer(modifier = Modifier.height(16.dp))
            audioPlayerProgress(currentPosition, duration)
            Spacer(modifier = Modifier.height(12.dp))
            audioPlayerPlaybackLogic(
                state =
                AudioPlayerState(
                    currentPosition = currentPosition,
                    duration = duration,
                    isPlaying = isPlaying,
                    isPaused = isPaused
                ),
                audioUrl = audioUrl,
                context = context,
                onStateChange = { newIsPlaying, newIsPaused, newPosition ->
                    isPlaying = newIsPlaying
                    isPaused = newIsPaused
                    if (newPosition > 0) currentPosition = newPosition
                }
            )
        }
    }
}
