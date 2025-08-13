package com.billie.synestesia.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class BottomNavItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    MAP("Carte", Icons.Default.LocationOn),
    SETTINGS("Paramètres", Icons.Default.Settings),
    PROFILE("Profil", Icons.Default.Person)
}

@Composable
fun BottomNavigation(
    currentRoute: BottomNavItem,
    onNavigate: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        BottomNavItem.values().forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = currentRoute == item,
                onClick = { onNavigate(item) }
            )
        }
    }
}
