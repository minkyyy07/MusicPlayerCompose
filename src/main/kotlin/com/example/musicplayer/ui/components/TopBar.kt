package com.example.musicplayer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.musicplayer.Screen

// List of all screens for the top navigation
private val screens = listOf(
    Screen.Library,
    Screen.Search,
    Screen.Player,
    Screen.Settings
)

@Composable
fun TopBar(
    currentScreen: Screen,
    onNavigationClick: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        elevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 8.dp)
                .background(
                    color = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(25.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            screens.forEach { screen ->
                NavigationButton(
                    text = screen.name,
                    isSelected = currentScreen == screen,
                    onClick = { onNavigationClick(screen) }
                )
            }
        }
    }
}

@Composable
private fun NavigationButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.padding(horizontal = 4.dp),
        shape = RoundedCornerShape(15.dp),
        elevation = null,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSelected) {
                MaterialTheme.colors.primary.copy(alpha = 0.2f)
            } else {
                Color.Transparent
            },
            contentColor = MaterialTheme.colors.onSurface
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.button
        )
    }
}
