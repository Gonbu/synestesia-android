package com.billie.synestesia.camera

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun createImageFile(context: Context): Pair<File, Uri> {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val storageDir = context.getExternalFilesDir("Photos")
    val image = File.createTempFile(
        imageFileName,
        ".jpg",
        storageDir
    )
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        image
    )
    return Pair(image, uri)
}

@Composable
fun rememberTakePictureLauncher(
    onImageSaved: (Uri, String) -> Unit,
    currentPhotoUri: Uri? = null,
    currentPath: String = ""
) = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
    if (success && currentPhotoUri != null) {
        onImageSaved(currentPhotoUri, currentPath)
    }
}

// Variable temporaire pour stocker le chemin actuel
private var currentPhotoPath: String = ""