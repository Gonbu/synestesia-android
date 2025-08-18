package com.billie.synestesia.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
    val onAudioRecorded: (String) -> Unit,
    val onSaveComplete: (SouvenirItem) -> Unit
)

@Composable
private fun souvenirFormHeader() {
    Text(text = "Créer un nouveau souvenir", style = MaterialTheme.typography.headlineSmall)
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
private fun souvenirPhotoSection(photoUri: Uri?, onTakePhoto: () -> Unit) {
    Text(
        text = "Photo du souvenir",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    // Aperçu de l'image si disponible
    photoUri?.let { uri ->
        androidx.compose.foundation.Image(
            painter = rememberAsyncImagePainter(uri),
            contentDescription = "Photo prise",
            modifier = Modifier.fillMaxWidth().height(200.dp).padding(vertical = 8.dp),
            contentScale = ContentScale.Crop
        )
    }

    Button(onClick = onTakePhoto, modifier = Modifier.fillMaxWidth()) { Text("Prendre une photo") }
}

@Composable
private fun souvenirAudioSection(onAudioRecorded: (String) -> Unit) {
    Text(
        text = AudioConstants.LABEL_AUDIO_PLAYBACK,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    audioRecorderComponent(onAudioRecorded = onAudioRecorded, modifier = Modifier.fillMaxWidth())
}

@Composable
private fun souvenirFormState(
    onPhotoTaken: (Uri, String) -> Unit,
    onPermissionDenied: () -> Unit
): Pair<
    androidx.activity.result.ActivityResultLauncher<Uri>,
    androidx.activity.result.ActivityResultLauncher<String>> {
    val context = LocalContext.current
    val tempPhotoUri = remember { mutableStateOf<Uri?>(null) }
    val tempPhotoPath = remember { mutableStateOf("") }

    val takePictureLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) {
                success ->
            if (success && tempPhotoUri.value != null) {
                onPhotoTaken(tempPhotoUri.value!!, tempPhotoPath.value)
            }
        }

    val cameraPermissionLauncher =
        rememberCameraPermissionLauncher(
            onPermissionGranted = {
                val (file, uri) = createImageFile(context)
                tempPhotoUri.value = uri
                tempPhotoPath.value = file.absolutePath
                takePictureLauncher.launch(uri)
            },
            onPermissionDenied = onPermissionDenied
        )

    return Pair(takePictureLauncher, cameraPermissionLauncher)
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
}

@Composable
private fun souvenirFormContent(
    data: SouvenirFormContentData,
    callbacks: SouvenirFormContentCallbacks
) {
    souvenirFormHeader()
    Spacer(modifier = Modifier.height(16.dp))

    souvenirFormFields(
        data = SouvenirFormData(data.titre, data.description, data.selectedColor),
        callbacks =
        SouvenirFormCallbacks(
            onTitreChange = callbacks.onTitreChange,
            onDescriptionChange = callbacks.onDescriptionChange,
            onColorSelected = callbacks.onColorSelected
        )
    )

    Spacer(modifier = Modifier.height(16.dp))

    souvenirPhotoSection(photoUri = data.photoUri, onTakePhoto = callbacks.onPhotoTaken)

    Spacer(modifier = Modifier.height(16.dp))

    souvenirAudioSection(onAudioRecorded = callbacks.onAudioRecorded)

    Spacer(modifier = Modifier.height(24.dp))

    if (data.latLng != null) {
        souvenirFormLogic(
            data =
            SouvenirFormLogicData(
                titre = data.titre,
                description = data.description,
                selectedColor = data.selectedColor,
                latLng = data.latLng,
                photoUri = data.photoUri,
                audioFilePath = data.audioFilePath
            ),
            onSaveComplete = callbacks.onSaveComplete
        )
    }

    souvenirFormActions(
        titre = data.titre,
        latLng = data.latLng,
        isUploading = false,
        onSave = { /* La logique est gérée par souvenirFormLogic */ }
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
        modifier = Modifier.fillMaxWidth(),
        enabled = titre.isNotBlank() && latLng != null && !isUploading
    ) { Text(if (isUploading) "Enregistrement..." else "Enregistrer ce souvenir") }

    // Espace supplémentaire pour assurer la visibilité du bouton
    Spacer(modifier = Modifier.height(32.dp))
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

    val (takePictureLauncher, cameraPermissionLauncher) =
        souvenirFormState(
            onPhotoTaken = { uri, _ -> photoUri = uri },
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
                    if (!checkCameraPermission(context)) {
                        cameraPermissionLauncher.launch(PermissionConstants.CAMERA)
                    } else {
                        val (file, uri) = createImageFile(context)
                        takePictureLauncher.launch(uri)
                    }
                },
                onAudioRecorded = { filePath -> audioFilePath = filePath },
                onSaveComplete = onSaveClick
            )
        )
    }
}
