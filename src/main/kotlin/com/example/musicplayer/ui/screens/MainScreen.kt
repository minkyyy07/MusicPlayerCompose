package com.example.musicplayer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.musicplayer.AppState
import com.example.musicplayer.Screen
import com.example.musicplayer.ui.components.BottomPlayerBar
import com.example.musicplayer.ui.components.TopBar

@Composable
fun MainScreen(appState: AppState) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Library) }
    val currentTrack by appState.currentTrack.collectAsState()
    val isPlaying by appState.isPlaying.collectAsState()
    
    Scaffold(
        topBar = {
            TopBar(
                currentScreen = currentScreen,
                onNavigationClick = { screen ->
                    currentScreen = screen
                }
            )
        },
        bottomBar = {
            currentTrack?.let { track ->
                BottomPlayerBar(
                    track = track,
                    isPlaying = isPlaying,
                    onPlayPauseClick = { appState.playPause() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colors.background)
        ) {
            when (currentScreen) {
                is Screen.Player -> PlayerScreen(
                    currentTrack = currentTrack,
                    isPlaying = isPlaying,
                    onPlayPauseClick = { appState.playPause() },
                    onPreviousClick = { appState.playPreviousTrack() },
                    onNextClick = { appState.playNextTrack() },
                    appState = appState
                )
                is Screen.Search -> SearchScreen(
                    availableTracks = appState.trackList.collectAsState().value,
                    onTrackSelected = { track ->
                        appState.selectTrack(track)
                    }
                )
                is Screen.Library -> LibraryScreen()
                is Screen.Settings -> SettingsScreen()
            }
        }
    }
}
