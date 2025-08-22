package com.billie.synestesia.ui

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.billie.synestesia.AudioPlaybackService
import com.billie.synestesia.AudioRecordingService
import com.billie.synestesia.R
import com.billie.synestesia.permission.AudioPermission
import com.billie.synestesia.permission.rememberAudioPermissionLauncher
import com.billie.synestesia.utils.AudioConstants
import com.billie.synestesia.utils.LogUtils
import kotlinx.coroutines.delay

@Composable
fun audioRecorderComponent(onAudioRecorded: (String) -> Unit, modifier: Modifier = Modifier) {
    val audioState = rememberAudioState()
    val context = LocalContext.current
    val audioPermissionLauncher = createAudioPermissionLauncher(context)

    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        audioRecorderHeader()
        audioRecordButton(
            isRecording = audioState.isRecording,
            onRecordClick = {
                if (!audioState.isRecording) {
                    handleStartRecording(context, audioPermissionLauncher) { filePath ->
                        audioState.updateRecordingState(true, filePath)
                    }
                } else {
                    handleStopRecording(context) { filePath ->
                        audioState.updateRecordingState(false, filePath)
                        onAudioRecorded(filePath)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (audioState.isRecording) {
            recordingTimeDisplay(audioState.formattedTime)
            Spacer(modifier = Modifier.height(12.dp))
        }

        audioState.audioFilePath?.let { path ->
            Spacer(modifier = Modifier.height(20.dp))
            audioPreviewCard(
                audioPath = path,
                formattedTime = audioState.formattedTime,
                isPlaying = audioState.isPlaying,
                onPlayClick = { /* La gestion de l'état se fait dans le composant */ }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        audioInstructions()
    }
}

@Composable
private fun audioRecorderHeader() {
    Text(
        text = AudioConstants.LABEL_AUDIO_RECORDING,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun audioInstructions() {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text = AudioConstants.INSTRUCTION_AUDIO_RECORDING,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun rememberAudioState(): AudioRecorderState {
    var isRecording by remember { mutableStateOf(false) }
    var recordingTime by remember { mutableStateOf(0L) }
    var audioFilePath by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    // Timer pour l'enregistrement
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                delay(1000)
                recordingTime++
            }
        }
    }

    // Format du temps d'enregistrement
    val formattedTime =
        remember(recordingTime) {
            val minutes = recordingTime / 60
            val seconds = recordingTime % 60
            String.format("%02d:%02d", minutes, seconds)
        }

    return remember {
        AudioRecorderState(
            isRecording = isRecording,
            recordingTime = recordingTime,
            audioFilePath = audioFilePath,
            isPlaying = isPlaying,
            formattedTime = formattedTime,
            updateRecordingState = { recording, filePath ->
                isRecording = recording
                if (recording) {
                    recordingTime = 0L
                }
                audioFilePath = filePath
            }
        )
    }
}

@Composable
private fun createAudioPermissionLauncher(context: Context): ActivityResultLauncher<String> {
    return rememberAudioPermissionLauncher(
        onPermissionGranted = { LogUtils.d("Permission audio accordée") },
        onPermissionDenied = {
            LogUtils.showErrorToast(context, AudioConstants.ERROR_AUDIO_PERMISSION_DENIED)
        }
    )
}

@Composable
private fun audioRecordButton(isRecording: Boolean, onRecordClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Button(
            onClick = onRecordClick,
            modifier = Modifier.size(80.dp).clip(CircleShape),
            colors =
            ButtonDefaults.buttonColors(
                containerColor =
                if (isRecording) {
                    Color.Red
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        ) {
            Icon(
                painter =
                painterResource(
                    id = if (isRecording) R.drawable.ic_stop else R.drawable.ic_mic
                ),
                contentDescription =
                if (isRecording) {
                    "Arrêter l'enregistrement"
                } else {
                    "Démarrer l'enregistrement"
                },
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun recordingTimeDisplay(formattedTime: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text = "Enregistrement en cours: $formattedTime",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Red
        )
    }
}

@Composable
private fun audioPreviewCard(
    audioPath: String,
    formattedTime: String,
    isPlaying: Boolean,
    onPlayClick: (Boolean) -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = AudioConstants.LABEL_AUDIO_RECORDED,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${AudioConstants.LABEL_AUDIO_DURATION}: $formattedTime",
                    style = MaterialTheme.typography.bodyMedium
                )

                audioPlayButton(
                    audioPath = audioPath,
                    isPlaying = isPlaying,
                    onPlayClick = onPlayClick,
                    context = context
                )
            }
        }
    }
}

@Composable
private fun audioPlayButton(
    audioPath: String,
    isPlaying: Boolean,
    onPlayClick: (Boolean) -> Unit,
    context: Context
) {
    IconButton(
        onClick = {
            if (isPlaying) {
                AudioPlaybackService.stopAudio()
                onPlayClick(false)
            } else {
                onPlayClick(true)
                AudioPlaybackService.playAudio(
                    context = context,
                    audioUrl = audioPath,
                    onProgress = { /* Pas besoin de progression pour la prévisualisation */
                    },
                    onComplete = { onPlayClick(false) },
                    onError = { error ->
                        LogUtils.e("Erreur de lecture de la prévisualisation: $error")
                        onPlayClick(false)
                    }
                )
            }
            LogUtils.d("Lecture audio: $audioPath")
        },
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            painter =
            painterResource(
                id = if (isPlaying) R.drawable.ic_stop else R.drawable.ic_play
            ),
            contentDescription = if (isPlaying) "Arrêter" else "Lecture",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
    }
}

private fun handleStartRecording(
    context: Context,
    audioPermissionLauncher: ActivityResultLauncher<String>,
    onSuccess: (String) -> Unit
) {
    if (AudioPermission.checkAudioPermission(context)) {
        val filePath = AudioRecordingService.startRecording(context)
        if (filePath != null) {
            onSuccess(filePath)
            LogUtils.d(AudioConstants.SUCCESS_AUDIO_RECORDING_START)
        } else {
            LogUtils.showErrorToast(context, AudioConstants.ERROR_AUDIO_RECORDING_START)
        }
    } else {
        audioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
    }
}

private fun handleStopRecording(context: Context, onSuccess: (String) -> Unit) {
    val filePath = AudioRecordingService.stopRecording()
    if (filePath != null) {
        onSuccess(filePath)
        LogUtils.d(AudioConstants.SUCCESS_AUDIO_RECORDING_STOP)
    } else {
        LogUtils.showErrorToast(context, AudioConstants.ERROR_AUDIO_RECORDING_STOP)
    }
}
