package com.billie.synestesia.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.billie.synestesia.ui.theme.ColorUtils
import com.billie.synestesia.utils.AudioConstants
import com.billie.synestesia.utils.LogUtils
import com.billie.synestesia.utils.MessageConstants
import com.billie.synestesia.utils.PermissionConstants
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

// Palette de 30 couleurs prédéfinies
private val colorPalette = AppColors.colorPalette

@Composable
fun ColorPicker(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Choisir une couleur",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(AppColors.colorPalette) { color ->
                ColorCircle(
                    color = color,
                    isSelected = selectedColor == color,
                    onClick = { onColorSelected(color) }
                )
            }
        }
    }
}

@Composable
private fun ColorCircle(color: String, isSelected: Boolean, onClick: () -> Unit) {
    val circleColor = ColorUtils.hexToComposeColor(color)

    Box(
        modifier =
        Modifier.size(40.dp)
            .clip(CircleShape)
            .background(circleColor)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) Color.White else Color.Gray,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            // Indicateur de sélection
            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color.White))
        }
    }
}

@Composable
fun SouvenirFormSheet(latLng: LatLng?, onSaveClick: (SouvenirItem) -> Unit) {
    var titre by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedColor by remember {
        mutableStateOf(AppColors.colorPalette[0])
    } // Couleur par défaut

    var photoPath by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var audioFilePath by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    // Utiliser un MutableState pour stocker l'URI temporaire
    val tempPhotoUri = remember { mutableStateOf<Uri?>(null) }
    val tempPhotoPath = remember { mutableStateOf("") }

    val takePictureLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) {
                success ->
            if (success && tempPhotoUri.value != null) {
                // Mettre à jour les variables d'état avec les valeurs temporaires
                photoUri = tempPhotoUri.value
                photoPath = tempPhotoPath.value
            }
        }

    // Launcher pour demander la permission
    val cameraPermissionLauncher =
        rememberCameraPermissionLauncher(
            onPermissionGranted = {
                // Créer un fichier pour la photo et lancer l'appareil photo
                val (file, uri) = createImageFile(context)
                tempPhotoUri.value = uri
                tempPhotoPath.value = file.absolutePath
                takePictureLauncher.launch(uri)
            },
            onPermissionDenied = {
                LogUtils.showToast(context, MessageConstants.CAMERA_PERMISSION_DENIED)
            }
        )

    var isUploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier =
        Modifier.fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = "Créer un nouveau souvenir", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = titre,
            onValueChange = { titre = it },
            label = { Text("Titre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        ColorPicker(
            selectedColor = selectedColor,
            onColorSelected = { selectedColor = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Section photo
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

        Button(
            onClick = {
                if (!checkCameraPermission(context)) {
                    cameraPermissionLauncher.launch(PermissionConstants.CAMERA)
                } else {
                    val (file, uri) = createImageFile(context)
                    tempPhotoUri.value = uri
                    tempPhotoPath.value = file.absolutePath
                    takePictureLauncher.launch(uri)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Prendre une photo") }

        Spacer(modifier = Modifier.height(16.dp))

        // Section audio
        Text(
            text = AudioConstants.LABEL_AUDIO_PLAYBACK,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        AudioRecorderComponent(
            onAudioRecorded = { filePath -> audioFilePath = filePath },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (titre.isNotBlank() && latLng != null && !isUploading) {
                    scope.launch {
                        isUploading = true
                        var photoUrl = ""
                        var audioUrl = ""

                        val souvenirSansMedia =
                            SouvenirItem(
                                titre = titre,
                                description = description,
                                latitude = latLng.latitude,
                                longitude = latLng.longitude,
                                date = System.currentTimeMillis(),
                                couleur = selectedColor,
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
                            LogUtils.showErrorToast(
                                context,
                                "Erreur: impossible de créer le souvenir"
                            )
                            isUploading = false
                            return@launch
                        }

                        // 2. Upload de la photo si disponible
                        if (photoUri != null && souvenirId != null) {
                            try {
                                photoUrl =
                                    FirebaseStorageService.uploadSouvenirImage(
                                        photoUri!!,
                                        souvenirId
                                    )
                                        ?: ""
                                if (photoUrl.isNotEmpty()) {
                                    FirestoreService.updateSouvenirPhoto(souvenirId, photoUrl)
                                }
                            } catch (e: Exception) {
                                LogUtils.e("Erreur upload photo: ", e)
                                LogUtils.showErrorToast(
                                    context,
                                    "Erreur lors de l'upload de la photo"
                                )
                            }
                        }

                        // 3. Upload de l'audio si disponible
                        if (audioFilePath != null && souvenirId != null) {
                            try {
                                audioUrl =
                                    FirebaseStorageService.uploadSouvenirAudio(
                                        audioFilePath!!,
                                        souvenirId
                                    )
                                        ?: ""
                                if (audioUrl.isNotEmpty()) {
                                    FirestoreService.updateSouvenirAudio(souvenirId, audioUrl)
                                }
                            } catch (e: Exception) {
                                LogUtils.e("Erreur upload audio: ", e)
                                LogUtils.showErrorToast(
                                    context,
                                    AudioConstants.ERROR_AUDIO_UPLOAD
                                )
                            }
                        }

                        isUploading = false

                        // Créer le souvenir final avec tous les médias
                        val souvenirFinal =
                            souvenirSansMedia.copy(
                                id = souvenirId,
                                photo = photoUrl,
                                audio = audioUrl
                            )
                        onSaveClick(souvenirFinal)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = titre.isNotBlank() && latLng != null && !isUploading
        ) { Text(if (isUploading) "Enregistrement..." else "Enregistrer ce souvenir") }

        // Espace supplémentaire pour assurer la visibilité du bouton
        Spacer(modifier = Modifier.height(32.dp))
    }
}
