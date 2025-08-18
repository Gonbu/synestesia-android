package com.billie.synestesia.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
private fun profileHeader(onNavigateBack: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
        }
        Text(
            text = "Profil",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun profilePictureSection(
    displayName: String,
    email: String,
    isEditingProfile: Boolean,
    onDisplayNameChange: (String) -> Unit,
    onToggleEditing: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(100.dp).padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Photo de profil",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isEditingProfile) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = onDisplayNameChange,
                    label = { Text("Nom d'utilisateur") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            } else {
                Text(text = displayName, fontSize = 20.sp, fontWeight = FontWeight.Medium)
            }

            Text(
                text = email,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onToggleEditing, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    if (isEditingProfile) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = if (isEditingProfile) "Sauvegarder" else "Modifier"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isEditingProfile) "Sauvegarder" else "Modifier le profil")
            }
        }
    }
}

@Composable
private fun profileStatisticsSection() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Statistiques",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(icon = Icons.Default.LocationOn, value = "12", label = "Souvenirs")
                StatisticItem(icon = Icons.Default.Star, value = "8", label = "Favoris")
                StatisticItem(icon = Icons.Default.Share, value = "5", label = "Partagés")
            }
        }
    }
}

@Composable
private fun profileAccountActionsSection() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Actions du compte",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            AccountActionItem(
                icon = Icons.Default.Lock,
                title = "Sécurité",
                subtitle = "Gérer la sécurité de votre compte",
                onClick = { /* TODO: Navigation vers sécurité */ }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            AccountActionItem(
                icon = Icons.Default.Info,
                title = "Données",
                subtitle = "Gérer vos données personnelles",
                onClick = { /* TODO: Navigation vers gestion des données */ }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            AccountActionItem(
                icon = Icons.Default.Info,
                title = "Aide et support",
                subtitle = "Obtenir de l'aide",
                onClick = { /* TODO: Navigation vers aide */ }
            )
        }
    }
}

@Composable
private fun profileDangerZoneSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Zone de danger",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedButton(
                onClick = {
                    // Déconnexion
                    FirebaseAuth.getInstance().signOut()
                    // Ici on pourrait naviguer vers l'écran de connexion
                },
                modifier = Modifier.fillMaxWidth(),
                colors =
                ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Déconnexion")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Se déconnecter")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { /* TODO: Supprimer le compte */ },
                modifier = Modifier.fillMaxWidth(),
                colors =
                ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer le compte")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Supprimer le compte")
            }
        }
    }
}

@Composable
fun profileScreen(onNavigateBack: () -> Unit) {
    var displayName by remember { mutableStateOf("Utilisateur Synestesia") }
    var email by remember { mutableStateOf("") }
    var isEditingProfile by remember { mutableStateOf(false) }

    // Récupérer les informations de l'utilisateur Firebase
    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            displayName = it.displayName ?: "Utilisateur Synestesia"
            email = it.email ?: "Utilisateur anonyme"
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        profileHeader(onNavigateBack)

        Spacer(modifier = Modifier.height(24.dp))

        profilePictureSection(
            displayName = displayName,
            email = email,
            isEditingProfile = isEditingProfile,
            onDisplayNameChange = { displayName = it },
            onToggleEditing = {
                if (isEditingProfile) {
                    // Sauvegarder les modifications
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.let {
                        // Ici on pourrait mettre à jour le profil Firebase
                        // Pour l'instant, on change juste l'état local
                    }
                }
                isEditingProfile = !isEditingProfile
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        profileStatisticsSection()

        Spacer(modifier = Modifier.height(16.dp))

        profileAccountActionsSection()

        Spacer(modifier = Modifier.height(16.dp))

        profileDangerZoneSection()
    }
}

@Composable
private fun StatisticItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = label,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AccountActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Aller à $title")
        }
    }
}
