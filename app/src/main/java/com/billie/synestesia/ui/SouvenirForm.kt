package com.billie.synestesia.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.billie.synestesia.camera.createImageFile
import com.billie.synestesia.camera.rememberTakePictureLauncher
import com.google.android.gms.maps.model.LatLng
import java.sql.Date
import com.billie.synestesia.models.SouvenirItem
import com.billie.synestesia.permission.checkCameraPermission
import com.billie.synestesia.permission.rememberCameraPermissionLauncher
import com.billie.synestesia.FirebaseStorageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import com.billie.synestesia.FirestoreService
import android.util.Log

// Palette de 30 couleurs prédéfinies
private val colorPalette = listOf(
    "#FF0000", // Rouge
    "#FF4500", // Orange-rouge
    "#FF8C00", // Orange foncé
    "#FFA500", // Orange
    "#FFD700", // Or
    "#FFFF00", // Jaune
    "#9ACD32", // Jaune-vert
    "#32CD32", // Vert lime
    "#00FF00", // Vert
    "#00FA9A", // Vert printemps
    "#00CED1", // Bleu-vert foncé
    "#00BFFF", // Bleu ciel
    "#0000FF", // Bleu
    "#4169E1", // Bleu royal
    "#8A2BE2", // Bleu-violet
    "#9370DB", // Violet moyen
    "#9932CC", // Violet foncé
    "#FF00FF", // Magenta
    "#FF1493", // Rose profond
    "#FF69B4", // Rose chaud
    "#FFB6C1", // Rose clair
    "#FFC0CB", // Rose pâle
    "#F0E68C", // Khaki
    "#D2B48C", // Brun clair
    "#A0522D", // Brun sienna
    "#8B4513", // Brun selle
    "#696969", // Gris foncé
    "#C0C0C0", // Argent
    "#FFFFFF", // Blanc
    "#000000"  // Noir
)

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
            items(colorPalette) { color ->
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
private fun ColorCircle(
    color: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val circleColor = try {
        Color(android.graphics.Color.parseColor(color))
    } catch (e: Exception) {
        Color.Gray
    }
    
    Box(
        modifier = Modifier
            .size(40.dp)
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
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}

@Composable
fun SouvenirFormSheet(
    latLng: LatLng?,
    onSaveClick: (SouvenirItem) -> Unit
) {
    var titre by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(colorPalette[0]) } // Couleur par défaut

    var photoPath by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    // Utiliser un MutableState pour stocker l'URI temporaire
    val tempPhotoUri = remember { mutableStateOf<Uri?>(null) }
    val tempPhotoPath = remember { mutableStateOf("") }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri.value != null) {
            // Mettre à jour les variables d'état avec les valeurs temporaires
            photoUri = tempPhotoUri.value
            photoPath = tempPhotoPath.value
        }
    }

    // Launcher pour demander la permission
    val cameraPermissionLauncher = rememberCameraPermissionLauncher(
        onPermissionGranted = {
            // Créer un fichier pour la photo et lancer l'appareil photo
            val (file, uri) = createImageFile(context)
            tempPhotoUri.value = uri
            tempPhotoPath.value = file.absolutePath
            takePictureLauncher.launch(uri)
        },
        onPermissionDenied = {
            Toast.makeText(context, "Permission caméra refusée", Toast.LENGTH_SHORT).show()
        }
    )

    var isUploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Créer un nouveau souvenir",
            style = MaterialTheme.typography.headlineSmall
        )
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

        // Aperçu de l'image si disponible
        photoUri?.let { uri ->
            androidx.compose.foundation.Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Photo prise",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 8.dp),
                contentScale = ContentScale.Crop
            )
        }

        Button(
            onClick = {
                if (!checkCameraPermission(context)) {
                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                } else {
                    val (file, uri) = createImageFile(context)
                    tempPhotoUri.value = uri
                    tempPhotoPath.value = file.absolutePath
                    takePictureLauncher.launch(uri)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Prendre une photo")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (titre.isNotBlank() && latLng != null && !isUploading) {
                    scope.launch {
                        isUploading = true
                        var photoUrl = ""
                        val souvenirSansPhoto = SouvenirItem(
                            titre = titre,
                            description = description,
                            latitude = latLng.latitude,
                            longitude = latLng.longitude,
                            date = System.currentTimeMillis(),
                            couleur = selectedColor,
                            photo = ""
                        )
                        // 1. Création du document Firestore
                        val souvenirId = try {
                            FirestoreService.addSouvenirAndReturnId(souvenirSansPhoto)
                        } catch (e: Exception) {
                            Log.e("SouvenirForm", "Erreur création Firestore: ", e)
                            Toast.makeText(context, "Erreur lors de la création du souvenir", Toast.LENGTH_SHORT).show()
                            isUploading = false
                            return@launch
                        }
                        // 2. Upload de la photo si besoin
                        if (photoUri != null && souvenirId != null) {
                            try {
                                photoUrl = FirebaseStorageService.uploadSouvenirImage(photoUri!!, souvenirId) ?: ""
                                if (photoUrl.isEmpty()) {
                                    Toast.makeText(context, "Erreur lors de l'upload de la photo (utilisateur non connecté ou upload échoué)", Toast.LENGTH_LONG).show()
                                    Log.e("SouvenirForm", "Upload Storage échoué ou user non connecté")
                                    isUploading = false
                                    return@launch
                                }
                            } catch (e: Exception) {
                                Log.e("SouvenirForm", "Erreur upload Storage: ", e)
                                Toast.makeText(context, "Erreur lors de l'upload de la photo", Toast.LENGTH_LONG).show()
                                isUploading = false
                                return@launch
                            }
                            // 3. Mise à jour du document Firestore
                            try {
                                FirestoreService.updateSouvenirPhoto(souvenirId, photoUrl)
                            } catch (e: Exception) {
                                Log.e("SouvenirForm", "Erreur update Firestore: ", e)
                                Toast.makeText(context, "Erreur lors de la mise à jour du souvenir avec la photo", Toast.LENGTH_LONG).show()
                                isUploading = false
                                return@launch
                            }
                        }
                        isUploading = false
                        // Créer le souvenir final avec l'ID et la photo
                        val souvenirFinal = souvenirSansPhoto.copy(
                            id = souvenirId,
                            photo = photoUrl
                        )
                        onSaveClick(souvenirFinal)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = titre.isNotBlank() && latLng != null && !isUploading
        ) {
            Text(if (isUploading) "Enregistrement..." else "Enregistrer ce souvenir")
        }
    }
}
