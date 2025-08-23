package com.example.musicplayer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musicplayer.AppState
import com.example.musicplayer.data.MusicTrack
import com.example.musicplayer.ui.components.BottomPlayerBar
import com.example.musicplayer.ui.components.TrackItem

@Composable
fun MainScreen(appState: AppState) {
    val currentTrack by appState.currentTrack.collectAsState()
    val isPlaying by appState.isPlaying.collectAsState()
    val tracks by appState.trackList.collectAsState()
    var showDetail by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Music Player") },
                actions = {
                    IconButton(onClick = appState::toggleTheme) {
                        Icon(Icons.Default.DarkMode, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            currentTrack?.let { track ->
                BottomPlayerBar(
                    track = track,
                    isPlaying = isPlaying,
                    onPlayPauseClick = appState::playPause,
                    onPreviousClick = appState::playPreviousTrack,
                    onNextClick = appState::playNextTrack
                )
            }
        }
    ) { innerPadding ->
        Row(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Левая панель – библиотека
            Box(
                Modifier
                    .width(340.dp)
                    .fillMaxHeight()
                    .padding(12.dp)
            ) {
                LibraryPanel(
                    tracks = tracks,
                    currentTrack = currentTrack,
                    onSelect = { appState.selectTrack(it) }
                )
            }

            Divider(Modifier.fillMaxHeight().width(1.dp))

            // Правая панель – подробный плеер или подсказка
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(24.dp)
            ) {
                when {
                    currentTrack == null -> EmptyHint()
                    showDetail && currentTrack != null -> PlayerScreen(
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        onPlayPauseClick = appState::playPause,
                        onPreviousClick = appState::playPreviousTrack,
                        onNextClick = appState::playNextTrack,
                        appState = appState
                    )
                    else -> EmptyHint()
                }
            }
        }
    }
}

@Composable
private fun EmptyHint() {
    Surface(elevation = 2.dp, modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Выберите трек в списке слева")
        }
    }
}

@Composable
private fun LibraryPanel(
    tracks: List<MusicTrack>,
    currentTrack: MusicTrack?,
    onSelect: (MusicTrack) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Text(
            text = "Библиотека",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 8.dp)
        )
        Divider()
        LazyColumn(Modifier.fillMaxSize()) {
            items(tracks) { track ->
                TrackItem(
                    track = track,
                    onClick = { onSelect(track) },
                    isPlaying = currentTrack?.id == track.id && currentTrack != null
                )
            }
        }
    }
}