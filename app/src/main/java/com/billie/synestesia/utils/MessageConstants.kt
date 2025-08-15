package com.billie.synestesia.utils

object MessageConstants {
    // Messages de succès
    const val SOUVENIR_SAVED = "Souvenir enregistré !"
    const val SOUVENIR_DELETED = "Souvenir supprimé !"
    
    // Messages d'erreur
    const val ERROR_LOADING_SOUVENIRS = "Erreur lors du chargement des souvenirs"
    const val ERROR_SAVING_SOUVENIR = "Erreur lors de l'enregistrement"
    const val ERROR_DELETING_SOUVENIR = "Erreur lors de la suppression"
    const val ERROR_CREATING_SOUVENIR = "Erreur lors de la création du souvenir"
    const val ERROR_PHOTO_UPLOAD = "Erreur lors de l'upload de la photo"
    const val ERROR_PHOTO_UPLOAD_USER = "Erreur lors de l'upload de la photo (utilisateur non connecté ou upload échoué)"
    const val ERROR_UPDATE_SOUVENIR = "Erreur lors de la mise à jour du souvenir avec la photo"
    
    // Messages de permission
    const val CAMERA_PERMISSION_DENIED = "Permission caméra refusée"
    
    // Messages de localisation
    const val LOCATION_PERMISSION_DENIED = "Location permissions denied"
    const val LOCATION_ERROR = "Could not get location"
    const val LOCATION_SECURITY_ERROR = "Security Exception: "
    
    // Messages Firestore
    const val FIRESTORE_MAPPING_ERROR = "Erreur mapping doc "
    
    // Messages de caméra
    const val CAMERA_ERROR = "Permissions caméra refusées"
}
