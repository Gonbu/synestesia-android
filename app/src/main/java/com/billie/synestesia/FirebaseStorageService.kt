package com.billie.synestesia

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth

object FirebaseStorageService {
    private val storage: FirebaseStorage = Firebase.storage

    /**
     * Upload une image sur Firebase Storage et retourne l'URL de téléchargement.
     * @param imageUri Uri locale de l'image à uploader
     * @param souvenirId Identifiant du souvenir pour organiser le stockage
     * @return L'URL de téléchargement de l'image
     */
    suspend fun uploadSouvenirImage(imageUri: Uri, souvenirId: String): String? {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return null
        val ref = storage.reference.child("souvenirs/$userId/$souvenirId/${imageUri.lastPathSegment}")
        return try {
            ref.putFile(imageUri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
} 
