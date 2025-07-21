package com.billie.synestesia.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController


@Composable
fun SouvenirFormSheet(
    latLng: LatLng?,
    onSaveClick: (SouvenirItem) -> Unit
) {
    var titre by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("") }

    var photoPath by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val controller = rememberColorPickerController()
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

        HsvColorPicker(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(12.dp),
            controller = controller,
            onColorChanged = { colorEnvelope: ColorEnvelope ->
                selectedColor = colorEnvelope.hexCode
            }
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
                onSaveClick(
                    SouvenirItem(
                        titre = titre,
                        description = description,
                        position = latLng,
                        date = Date(System.currentTimeMillis()),
                        couleur = selectedColor,
                        photo = photoPath
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = titre.isNotBlank() && latLng != null
        ) {
            Text("Enregistrer ce souvenir")
        }
    }
}
