package com.example.musicplayer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.musicplayer.AppState
import com.example.musicplayer.Screen
import com.example.musicplayer.ui.components.BottomPlayerBar
import com.example.musicplayer.ui.components.MiniPlayerPlaceholder
import com.example.musicplayer.ui.components.TopBar
import com.example.musicplayer.ui.theme.MusicPlayerColors

@Composable
fun MainScreen(appState: AppState) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Library) }
    val currentTrack by appState.currentTrack.collectAsState()
    val isPlaying by appState.isPlaying.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MusicPlayerColors.DarkBackground,
                        MusicPlayerColors.DarkSurface
                    )
                )
            )
    ) {
        Scaffold(
            backgroundColor = Color.Transparent,
            topBar = {
                TopBar(
                    currentScreen = currentScreen,
                    onNavigationClick = { screen ->
                        currentScreen = screen
                    }
                )
            },
            bottomBar = {
                Surface(
                    color = MaterialTheme.colors.surface.copy(alpha = 0.95f),
                    elevation = 8.dp
                ) {
                    if (currentTrack != null) {
                        BottomPlayerBar(
                            track = currentTrack!!,
                            isPlaying = isPlaying,
                            onPlayPauseClick = { appState.playPause() },
                            onPreviousClick = { /* TODO: Previous track logic */ },
                            onNextClick = { /* TODO: Next track logic */ },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        MiniPlayerPlaceholder(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (currentScreen) {
                    is Screen.Player -> PlayerScreen(
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        onPlayPauseClick = { appState.playPause() },
                        onPreviousClick = { /* TODO: Previous track logic */ },
                        onNextClick = { /* TODO: Next track logic */ },
                        appState = appState
                    )
                    is Screen.Search -> SearchScreen(appState = appState)
                    is Screen.Library -> LibraryScreen(appState = appState)
                    is Screen.Settings -> SettingsScreen()
                }
            }
        }
    }
}
