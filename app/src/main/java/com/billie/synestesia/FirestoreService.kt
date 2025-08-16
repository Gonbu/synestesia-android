package com.billie.synestesia

import com.billie.synestesia.models.SouvenirItem
import com.billie.synestesia.utils.LogUtils
import com.billie.synestesia.utils.MessageConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

object FirestoreService {
    private val db = FirebaseFirestore.getInstance()
    private const val COLLECTION_SOUVENIRS = "souvenirs"

    suspend fun getAllSouvenirs(): List<SouvenirItem> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            LogUtils.e("Utilisateur non connecté")
            return emptyList()
        }

        val snapshot = db.collection(COLLECTION_SOUVENIRS)
            .whereEqualTo("userId", userId) // Filtrer par utilisateur
            .get()
            .await()
        val souvenirs = mutableListOf<SouvenirItem>()
        for (doc in snapshot.documents) {
            try {
                val item = doc.toObject<SouvenirItem>()
                if (item != null) {
                    val souvenirWithId = item.copy(id = doc.id)
                    if (souvenirWithId.id != null) {
                        souvenirs.add(souvenirWithId)
                    }
                }
            } catch (e: Exception) {
                LogUtils.e("${MessageConstants.FIRESTORE_MAPPING_ERROR}${doc.id}: ", e)
            }
        }
        return souvenirs
    }

    /**
     * Ajoute un souvenir et retourne l'ID du document créé
     */
    suspend fun addSouvenirAndReturnId(souvenir: SouvenirItem): String? {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            LogUtils.e("Utilisateur non connecté")
            return null
        }

        // Ajouter l'ID utilisateur au souvenir
        val souvenirWithUserId = souvenir.copy(
            id = null,
            photo = "",
            userId = userId
        )

        val docRef = db.collection(COLLECTION_SOUVENIRS)
            .add(souvenirWithUserId)
            .await()
        return docRef.id
    }

    /**
     * Met à jour l'URL de la photo d'un souvenir
     */
    suspend fun updateSouvenirPhoto(souvenirId: String, photoUrl: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            LogUtils.e("Utilisateur non connecté")
            return
        }

        // Vérifier que l'utilisateur est bien le propriétaire du souvenir
        val souvenir = db.collection(COLLECTION_SOUVENIRS)
            .document(souvenirId)
            .get()
            .await()
            .toObject<SouvenirItem>()

        if (souvenir?.userId != userId) {
            LogUtils.e("Tentative de modification d'un souvenir non autorisée")
            return
        }

        db.collection(COLLECTION_SOUVENIRS)
            .document(souvenirId)
            .update("photo", photoUrl)
            .await()
    }

    /**
     * Met à jour l'URL du fichier audio d'un souvenir
     */
    suspend fun updateSouvenirAudio(souvenirId: String, audioUrl: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            LogUtils.e("Utilisateur non connecté")
            return
        }

        // Vérifier que l'utilisateur est bien le propriétaire du souvenir
        val souvenir = db.collection(COLLECTION_SOUVENIRS)
            .document(souvenirId)
            .get()
            .await()
            .toObject<SouvenirItem>()

        if (souvenir?.userId != userId) {
            LogUtils.e("Tentative de modification d'un souvenir non autorisée")
            return
        }

        db.collection(COLLECTION_SOUVENIRS)
            .document(souvenirId)
            .update("audio", audioUrl)
            .await()
    }

    /**
     * Supprime un souvenir à partir de ses coordonnées et de son titre
     */
    suspend fun deleteSouvenir(souvenir: SouvenirItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            LogUtils.e("Utilisateur non connecté")
            return
        }

        val id = souvenir.id
        if (id != null) {
            // Vérifier que l'utilisateur est bien le propriétaire du souvenir
            if (souvenir.userId != userId) {
                LogUtils.e("Tentative de suppression d'un souvenir non autorisée")
                return
            }

            db.collection(COLLECTION_SOUVENIRS)
                .document(id)
                .delete()
                .await()
        }
    }
}
