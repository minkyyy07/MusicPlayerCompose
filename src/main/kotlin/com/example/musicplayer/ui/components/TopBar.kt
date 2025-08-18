package com.example.musicplayer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.musicplayer.Screen
import com.example.musicplayer.ui.theme.MusicPlayerColors

// Иконки для экранов
private fun getScreenIcon(screen: Screen): ImageVector {
    return when (screen) {
        is Screen.Library -> Icons.Default.LibraryMusic
        is Screen.Search -> Icons.Default.Search
        is Screen.Player -> Icons.Default.PlayArrow
        is Screen.Settings -> Icons.Default.Settings
    }
}

private fun getScreenTitle(screen: Screen): String {
    return when (screen) {
        is Screen.Library -> "Библиотека"
        is Screen.Search -> "Поиск"
        is Screen.Player -> "Плеер"
        is Screen.Settings -> "Настройки"
    }
}

@Composable
fun TopBar(
    currentScreen: Screen,
    onNavigationClick: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val screens = listOf(
        Screen.Library,
        Screen.Search,
        Screen.Player,
        Screen.Settings
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.95f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            screens.forEach { screen ->
                NavigationItem(
                    screen = screen,
                    isSelected = currentScreen == screen,
                    onClick = { onNavigationClick(screen) }
                )
            }
        }
    }
}

@Composable
private fun NavigationItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        Brush.linearGradient(
            colors = listOf(
                MusicPlayerColors.Purple,
                MusicPlayerColors.Pink
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                Color.Transparent
            )
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = getScreenIcon(screen),
                contentDescription = getScreenTitle(screen),
                tint = if (isSelected) Color.White else MusicPlayerColors.DarkGray,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = getScreenTitle(screen),
                style = MaterialTheme.typography.caption,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else MusicPlayerColors.DarkGray
            )
        }
    }
}
