package com.billie.synestesia

import android.net.Uri
import com.billie.synestesia.utils.LogUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File
import kotlinx.coroutines.tasks.await

object FirebaseStorageService {
    private val storage: FirebaseStorage = Firebase.storage

    /**
     * Upload une image sur Firebase Storage et retourne l'URL de téléchargement.
     * @param imageUri Uri locale de l'image à uploader
     * @param souvenirId Identifiant du souvenir pour organiser le stockage
     * @return L'URL de téléchargement de l'image
     */
    suspend fun uploadSouvenirImage(imageUri: Uri, souvenirId: String): String? {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            LogUtils.e("Utilisateur non connecté pour l'upload de photo")
            return null
        }

        val ref = storage.reference.child(
            "souvenirs/$userId/$souvenirId/${imageUri.lastPathSegment}"
        )
        return try {
            ref.putFile(imageUri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            LogUtils.e("Erreur lors de l'upload de la photo: ", e)
            null
        }
    }

    /**
     * Upload un fichier audio sur Firebase Storage et retourne l'URL de téléchargement.
     * @param audioFilePath Chemin local du fichier audio à uploader
     * @param souvenirId Identifiant du souvenir pour organiser le stockage
     * @return L'URL de téléchargement du fichier audio
     */
    suspend fun uploadSouvenirAudio(audioFilePath: String, souvenirId: String): String? {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            LogUtils.e("Utilisateur non connecté pour l'upload audio")
            return null
        }

        val audioFile = File(audioFilePath)
        if (!audioFile.exists()) {
            LogUtils.e("Fichier audio introuvable: $audioFilePath")
            return null
        }

        val fileName = audioFile.name
        val ref = storage.reference.child("souvenirs/$userId/$souvenirId/$fileName")

        return try {
            ref.putFile(Uri.fromFile(audioFile)).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            LogUtils.e("Erreur lors de l'upload du fichier audio: ", e)
            null
        }
    }
}
