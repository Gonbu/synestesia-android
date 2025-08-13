package com.billie.synestesia.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit
) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
            }
            Text(
                text = "Profil",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Picture Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp),
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
                        onValueChange = { displayName = it },
                        label = { Text("Nom d'utilisateur") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                } else {
                    Text(
                        text = displayName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Text(
                    text = email,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { 
                        if (isEditingProfile) {
                            // Sauvegarder les modifications
                            val user = FirebaseAuth.getInstance().currentUser
                            user?.let {
                                // Ici on pourrait mettre à jour le profil Firebase
                                // Pour l'instant, on change juste l'état local
                            }
                        }
                        isEditingProfile = !isEditingProfile 
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        if (isEditingProfile) Icons.Default.Check else Icons.Default.Edit,
                        contentDescription = if (isEditingProfile) "Sauvegarder" else "Modifier"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isEditingProfile) "Sauvegarder" else "Modifier le profil")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Statistics Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
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
                    StatisticItem(
                        icon = Icons.Default.LocationOn,
                        value = "12",
                        label = "Souvenirs"
                    )
                    StatisticItem(
                        icon = Icons.Default.Star,
                        value = "8",
                        label = "Favoris"
                    )
                    StatisticItem(
                        icon = Icons.Default.Share,
                        value = "5",
                        label = "Partagés"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Account Actions Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
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
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                AccountActionItem(
                    icon = Icons.Default.Info,
                    title = "Données",
                    subtitle = "Gérer vos données personnelles",
                    onClick = { /* TODO: Navigation vers gestion des données */ }
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                AccountActionItem(
                    icon = Icons.Default.Info,
                    title = "Aide et support",
                    subtitle = "Obtenir de l'aide",
                    onClick = { /* TODO: Navigation vers aide */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Danger Zone Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
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
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Déconnexion")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Se déconnecter")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedButton(
                    onClick = { /* TODO: Supprimer le compte */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
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
}

@Composable
private fun StatisticItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
                        IconButton(onClick = onClick) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Aller à $title")
                }
    }
}
