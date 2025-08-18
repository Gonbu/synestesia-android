package com.billie.synestesia.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun colorPicker(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors =
        listOf(
            "#FF0000", // Rouge
            "#00FF00", // Vert
            "#0000FF", // Bleu
            "#FFFF00", // Jaune
            "#FF00FF", // Magenta
            "#00FFFF", // Cyan
            "#FFA500", // Orange
            "#800080", // Violet
            "#FFC0CB", // Rose
            "#A52A2A", // Marron
            "#808080", // Gris
            "#000000" // Noir
        )

    Column(modifier = modifier) {
        Text(
            text = "Couleur du souvenir",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            colors.forEach { color ->
                colorCircle(
                    color = color,
                    isSelected = selectedColor == color,
                    onClick = { onColorSelected(color) }
                )
            }
        }
    }
}

@Composable
private fun colorCircle(color: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = Color(android.graphics.Color.parseColor(color))

    Box(
        modifier =
        Modifier.size(32.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color =
                if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Gray
                },
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            // Indicateur de sélection
            Box(
                modifier =
                Modifier.size(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary)
            )
        }
    }
}
