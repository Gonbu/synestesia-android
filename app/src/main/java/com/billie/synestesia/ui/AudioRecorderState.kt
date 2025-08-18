package com.billie.synestesia.ui

// Classe pour gérer l'état de l'enregistreur audio
data class AudioRecorderState(
    val isRecording: Boolean,
    val recordingTime: Long,
    val audioFilePath: String?,
    val isPlaying: Boolean,
    val formattedTime: String,
    val updateRecordingState: (Boolean, String) -> Unit
)
