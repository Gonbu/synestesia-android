package com.billie.synestesia.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.billie.synestesia.FirebaseStorageService
import com.billie.synestesia.FirestoreService
import com.billie.synestesia.camera.createImageFile
import com.billie.synestesia.models.SouvenirItem
import com.billie.synestesia.permission.checkCameraPermission
import com.billie.synestesia.permission.rememberCameraPermissionLauncher
import com.billie.synestesia.ui.theme.AppColors
import com.billie.synestesia.utils.AudioConstants
import com.billie.synestesia.utils.LogUtils
import com.billie.synestesia.utils.MessageConstants
import com.billie.synestesia.utils.PermissionConstants
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

data class SouvenirFormData(val titre: String, val description: String, val selectedColor: String)

data class SouvenirFormCallbacks(
    val onTitreChange: (String) -> Unit,
    val onDescriptionChange: (String) -> Unit,
    val onColorSelected: (String) -> Unit
)

data class SouvenirFormLogicData(
    val titre: String,
    val description: String,
    val selectedColor: String,
    val latLng: LatLng,
    val photoUri: Uri?,
    val audioFilePath: String?
)

data class SouvenirFormContentData(
    val titre: String,
    val description: String,
    val selectedColor: String,
    val photoUri: Uri?,
    val audioFilePath: String?,
    val latLng: LatLng?
)

data class SouvenirFormContentCallbacks(
    val onTitreChange: (String) -> Unit,
    val onDescriptionChange: (String) -> Unit,
    val onColorSelected: (String) -> Unit,
    val onPhotoTaken: () -> Unit,
    val onSelectFromGallery: () -> Unit,
    val onAudioRecorded: (String) -> Unit,
    val onSaveComplete: (SouvenirItem) -> Unit
)

@Composable
private fun souvenirFormHeader() {
    Text(
        text = "Nouveau souvenir",
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun souvenirFormFields(data: SouvenirFormData, callbacks: SouvenirFormCallbacks) {
    OutlinedTextField(
        value = data.titre,
        onValueChange = callbacks.onTitreChange,
        label = { Text("Titre") },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = data.description,
        onValueChange = callbacks.onDescriptionChange,
        label = { Text("Description") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3
    )

    Spacer(modifier = Modifier.height(16.dp))

    colorPicker(
        selectedColor = data.selectedColor,
        onColorSelected = callbacks.onColorSelected,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun souvenirPhotoSection(
    photoUri: Uri?,
    onTakePhoto: () -> Unit,
    onSelectFromGallery: () -> Unit
) {
    // Aperçu de l'image si disponible
    photoUri?.let { uri ->
        LogUtils.d("Affichage de l'aperçu photo: $uri")
        androidx.compose.foundation.Image(
            painter = rememberAsyncImagePainter(model = uri),
            contentDescription = "Photo sélectionnée",
            modifier = Modifier.fillMaxWidth().height(180.dp).padding(bottom = 12.dp),
            contentScale = ContentScale.Crop
        )
    }

    // Boutons pour prendre une photo ou sélectionner depuis la galerie
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(onClick = onTakePhoto, modifier = Modifier.weight(1f)) { Text("📷 Prendre") }
        Button(onClick = onSelectFromGallery, modifier = Modifier.weight(1f)) {
            Text("🖼️ Galerie")
        }
    }
}

@Composable
private fun souvenirAudioSection(onAudioRecorded: (String) -> Unit) {
    audioRecorderComponent(onAudioRecorded = onAudioRecorded, modifier = Modifier.fillMaxWidth())
}

@Composable
private fun souvenirFormState(
    onPhotoTaken: (Uri, String) -> Unit,
    onPermissionDenied: () -> Unit
): Triple<
    androidx.activity.result.ActivityResultLauncher<Uri>,
    androidx.activity.result.ActivityResultLauncher<String>,
    androidx.activity.result.ActivityResultLauncher<String>> {
    val context = LocalContext.current
    val tempPhotoUri = remember { mutableStateOf<Uri?>(null) }
    val tempPhotoPath = remember { mutableStateOf("") }

    val takePictureLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) {
                success ->
            LogUtils.d("Photo prise avec succès: $success, URI: ${tempPhotoUri.value}")
            if (success && tempPhotoUri.value != null) {
                onPhotoTaken(tempPhotoUri.value!!, tempPhotoPath.value)
            }
        }

    val galleryLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri
            ->
            LogUtils.d("Photo sélectionnée depuis la galerie: $uri")
            if (uri != null) {
                // Pour la galerie, on utilise l'URI directement et on génère un nom de fichier
                val fileName = "gallery_${System.currentTimeMillis()}.jpg"
                onPhotoTaken(uri, fileName)
            }
        }

    val cameraPermissionLauncher =
        rememberCameraPermissionLauncher(
            onPermissionGranted = {
                LogUtils.d("Permission caméra accordée, création du fichier")
                val (file, uri) = createImageFile(context)
                LogUtils.d("Fichier créé: ${file.absolutePath}, URI: $uri")
                tempPhotoUri.value = uri
                tempPhotoPath.value = file.absolutePath
                LogUtils.d("Lancement de la caméra avec URI: $uri")
                takePictureLauncher.launch(uri)
            },
            onPermissionDenied = {
                LogUtils.e("Permission caméra refusée")
                onPermissionDenied()
            }
        )

    return Triple(takePictureLauncher, cameraPermissionLauncher, galleryLauncher)
}

private suspend fun uploadPhoto(
    photoUri: Uri?,
    souvenirId: String,
    context: android.content.Context
): String {
    if (photoUri != null) {
        try {
            val photoUrl = FirebaseStorageService.uploadSouvenirImage(photoUri, souvenirId) ?: ""
            if (photoUrl.isNotEmpty()) {
                FirestoreService.updateSouvenirPhoto(souvenirId, photoUrl)
            }
            return photoUrl
        } catch (e: Exception) {
            LogUtils.e("Erreur upload photo: ", e)
            LogUtils.showErrorToast(context, "Erreur lors de l'upload de la photo")
        }
    }
    return ""
}

private suspend fun uploadAudio(
    audioFilePath: String?,
    souvenirId: String,
    context: android.content.Context
): String {
    if (audioFilePath != null) {
        try {
            val audioUrl =
                FirebaseStorageService.uploadSouvenirAudio(audioFilePath, souvenirId) ?: ""
            if (audioUrl.isNotEmpty()) {
                FirestoreService.updateSouvenirAudio(souvenirId, audioUrl)
            }
            return audioUrl
        } catch (e: Exception) {
            LogUtils.e("Erreur upload audio: ", e)
            LogUtils.showErrorToast(context, AudioConstants.ERROR_AUDIO_UPLOAD)
        }
    }
    return ""
}

@Composable
private fun souvenirFormLogic(data: SouvenirFormLogicData, onSaveComplete: (SouvenirItem) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isUploading by remember { mutableStateOf(false) }

    // LaunchedEffect désactivé pour éviter l'enregistrement automatique
    // L'enregistrement se fait maintenant uniquement via le bouton
    /*
    LaunchedEffect(
        data.titre,
        data.description,
        data.selectedColor,
        data.latLng,
        data.photoUri,
        data.audioFilePath
    ) {
        if (data.titre.isNotBlank() && data.latLng != null && !isUploading) {
            scope.launch {
                isUploading = true
                var photoUrl = ""
                var audioUrl = ""

                val souvenirSansMedia =
                    SouvenirItem(
                        titre = data.titre,
                        description = data.description,
                        latitude = data.latLng.latitude,
                        longitude = data.latLng.longitude,
                        date = System.currentTimeMillis(),
                        couleur = data.selectedColor,
                        photo = "",
                        audio = ""
                    )

                // 1. Création du document Firestore
                val souvenirId =
                    try {
                        FirestoreService.addSouvenirAndReturnId(souvenirSansMedia)
                    } catch (e: Exception) {
                        LogUtils.e("Erreur création Firestore: ", e)
                        LogUtils.showErrorToast(
                            context,
                            MessageConstants.ERROR_CREATING_SOUVENIR
                        )
                        isUploading = false
                        return@launch
                    }

                if (souvenirId == null) {
                    LogUtils.showErrorToast(context, "Erreur: impossible de créer le souvenir")
                    isUploading = false
                    return@launch
                }

                // 2. Upload de la photo si disponible
                photoUrl = uploadPhoto(data.photoUri, souvenirId, context)

                // 3. Upload de l'audio si disponible
                audioUrl = uploadAudio(data.audioFilePath, souvenirId, context)

                isUploading = false

                // Créer le souvenir final avec tous les médias
                val souvenirFinal =
                    souvenirSansMedia.copy(id = souvenirId, photo = photoUrl, audio = audioUrl)
                onSaveComplete(souvenirFinal)
            }
        }
    }
    */
}

@Composable
private fun souvenirFormContent(
    data: SouvenirFormContentData,
    callbacks: SouvenirFormContentCallbacks
) {
    souvenirFormHeader()
    Spacer(modifier = Modifier.height(12.dp))

    souvenirFormFields(
        data = SouvenirFormData(data.titre, data.description, data.selectedColor),
        callbacks =
        SouvenirFormCallbacks(
            onTitreChange = callbacks.onTitreChange,
            onDescriptionChange = callbacks.onDescriptionChange,
            onColorSelected = callbacks.onColorSelected
        )
    )

    Spacer(modifier = Modifier.height(20.dp))

    souvenirPhotoSection(
        photoUri = data.photoUri,
        onTakePhoto = callbacks.onPhotoTaken,
        onSelectFromGallery = callbacks.onSelectFromGallery
    )

    Spacer(modifier = Modifier.height(20.dp))

    souvenirAudioSection(onAudioRecorded = callbacks.onAudioRecorded)

    Spacer(modifier = Modifier.height(20.dp))

    // La logique d'enregistrement est maintenant dans le bouton
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isUploading by remember { mutableStateOf(false) }

    souvenirFormActions(
        titre = data.titre,
        latLng = data.latLng,
        isUploading = isUploading,
        onSave = {
            // Logique d'enregistrement manuel complète
            if (data.titre.isNotBlank() && data.latLng != null) {
                scope.launch {
                    isUploading = true
                    try {
                        var photoUrl = ""
                        var audioUrl = ""

                        val souvenirSansMedia =
                            SouvenirItem(
                                titre = data.titre,
                                description = data.description,
                                latitude = data.latLng.latitude,
                                longitude = data.latLng.longitude,
                                date = System.currentTimeMillis(),
                                couleur = data.selectedColor,
                                photo = "",
                                audio = ""
                            )

                        // 1. Création du document Firestore
                        val souvenirId =
                            try {
                                FirestoreService.addSouvenirAndReturnId(souvenirSansMedia)
                            } catch (e: Exception) {
                                LogUtils.e("Erreur création Firestore: ", e)
                                LogUtils.showErrorToast(
                                    context,
                                    MessageConstants.ERROR_CREATING_SOUVENIR
                                )
                                return@launch
                            }

                        if (souvenirId == null) {
                            LogUtils.showErrorToast(
                                context,
                                "Erreur: impossible de créer le souvenir"
                            )
                            return@launch
                        }

                        // 2. Upload de la photo si disponible
                        LogUtils.d("Tentative d'upload photo: ${data.photoUri}")
                        photoUrl = uploadPhoto(data.photoUri, souvenirId, context)
                        LogUtils.d("Photo uploadée: $photoUrl")

                        // 3. Upload de l'audio si disponible
                        LogUtils.d("Tentative d'upload audio: ${data.audioFilePath}")
                        audioUrl = uploadAudio(data.audioFilePath, souvenirId, context)
                        LogUtils.d("Audio uploadé: $audioUrl")

                        // Créer le souvenir final avec tous les médias
                        val souvenirFinal =
                            souvenirSansMedia.copy(
                                id = souvenirId,
                                photo = photoUrl,
                                audio = audioUrl
                            )
                        callbacks.onSaveComplete(souvenirFinal)
                    } finally {
                        isUploading = false
                    }
                }
            }
        }
    )
}

@Composable
private fun souvenirFormActions(
    titre: String,
    latLng: LatLng?,
    isUploading: Boolean,
    onSave: () -> Unit
) {
    Button(
        onClick = onSave,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        enabled = titre.isNotBlank() && latLng != null && !isUploading
    ) {
        if (isUploading) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Enregistrement...", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            Text("💾 Enregistrer", style = MaterialTheme.typography.titleMedium)
        }
    }

    // Espace supplémentaire pour assurer la visibilité du bouton
    Spacer(modifier = Modifier.height(24.dp))
}

// Palette de 30 couleurs prédéfinies
private val colorPalette = AppColors.colorPalette

@Composable
fun souvenirFormSheet(latLng: LatLng?, onSaveClick: (SouvenirItem) -> Unit) {
    var titre by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(AppColors.colorPalette[0]) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var audioFilePath by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    val (takePictureLauncher, cameraPermissionLauncher, galleryLauncher) =
        souvenirFormState(
            onPhotoTaken = { uri, _ ->
                LogUtils.d("Photo prise: $uri")
                photoUri = uri // Mettre à jour l'état local quand la photo est prise
            },
            onPermissionDenied = {
                LogUtils.showToast(context, MessageConstants.CAMERA_PERMISSION_DENIED)
            }
        )

    Column(
        modifier =
        Modifier.fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        souvenirFormContent(
            data =
            SouvenirFormContentData(
                titre = titre,
                description = description,
                selectedColor = selectedColor,
                photoUri = photoUri,
                audioFilePath = audioFilePath,
                latLng = latLng
            ),
            callbacks =
            SouvenirFormContentCallbacks(
                onTitreChange = { titre = it },
                onDescriptionChange = { description = it },
                onColorSelected = { selectedColor = it },
                onPhotoTaken = {
                    LogUtils.d("Démarrage de la prise de photo")
                    if (!checkCameraPermission(context)) {
                        LogUtils.d("Demande de permission caméra")
                        cameraPermissionLauncher.launch(PermissionConstants.CAMERA)
                    } else {
                        LogUtils.d("Permission caméra accordée, lancement direct")
                        cameraPermissionLauncher.launch(PermissionConstants.CAMERA)
                    }
                },
                onSelectFromGallery = {
                    LogUtils.d("Sélection depuis la galerie")
                    galleryLauncher.launch("image/*")
                },
                onAudioRecorded = { filePath ->
                    LogUtils.d("Audio enregistré: $filePath")
                    audioFilePath = filePath // Mettre à jour l'état local
                },
                onSaveComplete = onSaveClick
            )
        )
    }
}
