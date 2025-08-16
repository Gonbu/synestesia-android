package com.billie.synestesia

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import com.billie.synestesia.utils.LogUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object AudioRecordingService {
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var outputFile: String? = null

    /**
     * Démarre l'enregistrement audio
     * @param context Le contexte de l'application
     * @return Le chemin du fichier audio créé, ou null en cas d'erreur
     */
    fun startRecording(context: Context): String? {
        if (isRecording) {
            LogUtils.e("L'enregistrement est déjà en cours")
            return null
        }

        try {
            // Créer le fichier de sortie
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "AUDIO_$timeStamp.mp3"
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            val audioFile = File(storageDir, fileName)
            outputFile = audioFile.absolutePath

            // Initialiser le MediaRecorder
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)

                try {
                    prepare()
                    start()
                    isRecording = true
                    LogUtils.d("Enregistrement audio démarré: $outputFile")
                    return outputFile
                } catch (e: IOException) {
                    LogUtils.e("Erreur lors de la préparation de l'enregistrement: ", e)
                    release()
                    return null
                }
            }
        } catch (e: Exception) {
            LogUtils.e("Erreur lors du démarrage de l'enregistrement: ", e)
            release()
        }
        return null
    }

    /**
     * Arrête l'enregistrement audio
     * @return Le chemin du fichier audio enregistré, ou null en cas d'erreur
     */
    fun stopRecording(): String? {
        if (!isRecording) {
            LogUtils.e("Aucun enregistrement en cours")
            return null
        }

        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false

            val filePath = outputFile
            outputFile = null

            LogUtils.d("Enregistrement audio arrêté: $filePath")
            filePath
        } catch (e: Exception) {
            LogUtils.e("Erreur lors de l'arrêt de l'enregistrement: ", e)
            release()
            null
        }
    }

    /**
     * Vérifie si un enregistrement est en cours
     */
    fun isRecording(): Boolean = isRecording

    /**
     * Libère les ressources du MediaRecorder
     */
    private fun release() {
        try {
            mediaRecorder?.apply {
                if (isRecording) {
                    stop()
                }
                release()
            }
        } catch (e: Exception) {
            LogUtils.e("Erreur lors de la libération des ressources: ", e)
        } finally {
            mediaRecorder = null
            isRecording = false
        }
    }

    /**
     * Nettoie les ressources lors de la destruction
     */
    fun cleanup() {
        release()
    }
}
