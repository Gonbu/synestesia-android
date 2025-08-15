package com.billie.synestesia.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.billie.synestesia.models.SouvenirItem
import com.billie.synestesia.utils.LogUtils
import com.billie.synestesia.utils.MessageConstants
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PhotoFullScreenDialog(
    photoUrl: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
        ) {
            // Photo en plein écran
            AsyncImage(
                model = photoUrl,
                contentDescription = "Photo en plein écran",
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
            
            // Bouton fermer en haut à droite
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fermer",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun SouvenirDetailCard(
    souvenirs: List<SouvenirItem>,
    currentIndex: Int,
    onAddSouvenir: () -> Unit,
    onDeleteSouvenir: () -> Unit,
    onClose: () -> Unit,
    onNavigateToSouvenir: (Int) -> Unit,
    modifier: Modifier = Modifier,
    showAddButton: Boolean = true,
    showDeleteButton: Boolean = true
) {
    val currentSouvenir = souvenirs.getOrNull(currentIndex)
    
    if (currentSouvenir == null) return
    
    var showFullScreenPhoto by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Indicateurs de position (points)
        if (souvenirs.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                souvenirs.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(8.dp)
                            .background(
                                color = if (index == currentIndex) Color.White else Color.White.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    )
                }
            }
        }
        
        // Navigation et titre
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (souvenirs.size > 1) {
                IconButton(
                    onClick = { 
                        val newIndex = if (currentIndex > 0) currentIndex - 1 else souvenirs.size - 1
                        onNavigateToSouvenir(newIndex)
                    },
                    enabled = true
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Précédent",
                        tint = Color.White
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
            
            Text(
                text = currentSouvenir.titre,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                color = Color.White
            )
            
            if (souvenirs.size > 1) {
                IconButton(
                    onClick = { 
                        val newIndex = if (currentIndex < souvenirs.size - 1) currentIndex + 1 else 0
                        onNavigateToSouvenir(newIndex)
                    },
                    enabled = true
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Suivant",
                        tint = Color.White
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Photo du souvenir (cliquable)
        if (currentSouvenir.photo.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 12.dp)
            ) {
                AsyncImage(
                    model = currentSouvenir.photo,
                    contentDescription = "Photo du souvenir",
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                
                // Overlay avec icône de zoom
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                ) {
                    IconButton(
                        onClick = { showFullScreenPhoto = true },
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Voir en grand",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
        
        // Description
        Text(
            text = currentSouvenir.description,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp),
            color = Color.White
        )
        
        // Date
        currentSouvenir.date.let { date ->
            val formatted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date)
            Text(
                text = "Ajouté le $formatted",
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 16.dp),
                color = Color.White.copy(alpha = 0.8f)
            )
        }
        
        // Indicateur de position (texte)
        if (souvenirs.size > 1) {
            Text(
                text = "${currentIndex + 1} / ${souvenirs.size}",
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp),
                color = Color.White.copy(alpha = 0.8f)
            )
        }
        
        // Boutons d'action
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showAddButton) {
                Button(
                    onClick = onAddSouvenir,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ajouter un souvenir")
                }
            }
            if (showDeleteButton) {
                Button(
                    onClick = onDeleteSouvenir,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Supprimer")
                }
            }
            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fermer")
            }
        }
    }
    
    // Dialog pour la photo en plein écran
    if (showFullScreenPhoto) {
        PhotoFullScreenDialog(
            photoUrl = currentSouvenir.photo,
            onDismiss = { showFullScreenPhoto = false }
        )
    }
} 
