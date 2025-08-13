package com.billie.synestesia

import com.billie.synestesia.models.SouvenirItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

object FirestoreService {
    private val db = FirebaseFirestore.getInstance()
    private const val COLLECTION_SOUVENIRS = "souvenirs"

    suspend fun getAllSouvenirs(): List<SouvenirItem> {
        val snapshot = db.collection(COLLECTION_SOUVENIRS)
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
                android.util.Log.e("FirestoreService", "Erreur mapping doc ${doc.id}: ${e}")
            }
        }
        return souvenirs
    }

    /**
     * Ajoute un souvenir et retourne l'ID du document créé
     */
    suspend fun addSouvenirAndReturnId(souvenir: SouvenirItem): String? {
        val docRef = db.collection(COLLECTION_SOUVENIRS)
            .add(souvenir.copy(id = null, photo = "")) // Ajoute sans id ni photo
            .await()
        return docRef.id
    }

    /**
     * Met à jour l'URL de la photo d'un souvenir
     */
    suspend fun updateSouvenirPhoto(souvenirId: String, photoUrl: String) {
        db.collection(COLLECTION_SOUVENIRS)
            .document(souvenirId)
            .update("photo", photoUrl)
            .await()
    }

    /**
     * Supprime un souvenir à partir de ses coordonnées et de son titre
     */
    suspend fun deleteSouvenir(souvenir: SouvenirItem) {
        val id = souvenir.id
        if (id != null) {
            db.collection(COLLECTION_SOUVENIRS)
                .document(id)
                .delete()
                .await()
        }
    }
} 
