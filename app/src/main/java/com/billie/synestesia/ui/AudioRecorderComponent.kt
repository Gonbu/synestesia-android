package com.billie.synestesia.ui

import android.Manifest
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
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@Composable
fun audioRecorderComponent(onAudioRecorded: (String) -> Unit, modifier: Modifier = Modifier) {
    var isRecording by remember { mutableStateOf(false) }
    var recordingTime by remember { mutableStateOf(0L) }
    var audioFilePath by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Launcher pour la permission audio
    val audioPermissionLauncher =
        rememberAudioPermissionLauncher(
            onPermissionGranted = {
                // Permission accordée, on peut enregistrer
                LogUtils.d("Permission audio accordée")
            },
            onPermissionDenied = {
                LogUtils.showErrorToast(
                    context,
                    AudioConstants.ERROR_AUDIO_PERMISSION_DENIED
                )
            }
        )

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

    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = AudioConstants.LABEL_AUDIO_RECORDING,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Bouton d'enregistrement principal avec icônes SVG personnalisées
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Button(
                onClick = {
                    if (!isRecording) {
                        // Vérifier la permission avant de démarrer l'enregistrement
                        if (AudioPermission.checkAudioPermission(context)) {
                            val filePath = AudioRecordingService.startRecording(context)
                            if (filePath != null) {
                                isRecording = true
                                recordingTime = 0L
                                audioFilePath = filePath
                                LogUtils.d(AudioConstants.SUCCESS_AUDIO_RECORDING_START)
                            } else {
                                LogUtils.showErrorToast(
                                    context,
                                    AudioConstants.ERROR_AUDIO_RECORDING_START
                                )
                            }
                        } else {
                            // Demander la permission
                            audioPermissionLauncher.launch(
                                android.Manifest.permission.RECORD_AUDIO
                            )
                        }
                    } else {
                        // Arrêter l'enregistrement
                        val filePath = AudioRecordingService.stopRecording()
                        if (filePath != null) {
                            isRecording = false
                            audioFilePath = filePath
                            onAudioRecorded(filePath)
                            LogUtils.d(AudioConstants.SUCCESS_AUDIO_RECORDING_STOP)
                        } else {
                            LogUtils.showErrorToast(
                                context,
                                AudioConstants.ERROR_AUDIO_RECORDING_STOP
                            )
                        }
                    }
                },
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
                        id =
                        if (isRecording) {
                            R.drawable.ic_stop
                        } else {
                            R.drawable.ic_mic
                        }
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

        Spacer(modifier = Modifier.height(20.dp))

        // Affichage du temps d'enregistrement
        if (isRecording) {
            Text(
                text = "Enregistrement en cours: $formattedTime",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Aperçu de l'audio enregistré
        audioFilePath?.let { path ->
            Spacer(modifier = Modifier.height(20.dp))

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

                        // Bouton de lecture avec icône SVG personnalisée
                        IconButton(
                            onClick = {
                                if (isPlaying) {
                                    // Arrêter la lecture
                                    AudioPlaybackService.stopAudio()
                                    isPlaying = false
                                } else {
                                    // Démarrer la lecture
                                    isPlaying = true
                                    AudioPlaybackService.playAudio(
                                        context = context,
                                        audioUrl = path,
                                        onProgress = { /* Pas besoin de progression pour la prévisualisation */
                                        },
                                        onComplete = { isPlaying = false },
                                        onError = { error ->
                                            LogUtils.e(
                                                "Erreur de lecture de la prévisualisation: $error"
                                            )
                                            isPlaying = false
                                        }
                                    )
                                }
                                LogUtils.d("Lecture audio: $path")
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                painter =
                                painterResource(
                                    id =
                                    if (isPlaying) {
                                        R.drawable.ic_stop
                                    } else {
                                        R.drawable.ic_play
                                    }
                                ),
                                contentDescription = if (isPlaying) "Arrêter" else "Lecture",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }

        // Instructions
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = AudioConstants.INSTRUCTION_AUDIO_RECORDING,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
