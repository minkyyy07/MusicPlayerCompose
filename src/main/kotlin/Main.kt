package com.example.musicplayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.example.musicplayer.data.SampleData
import com.example.musicplayer.ui.screens.MainScreen
import com.example.musicplayer.ui.theme.AppTheme
import java.awt.Dimension

fun main() = application {
    val appState = rememberAppState()

    Window(
        title = "Music Player",
        onCloseRequest = {
            appState.release()
            exitApplication()
        },
        state = appState.windowState
    ) {
        // Set minimum window size
        window.minimumSize = Dimension(800, 600)

        AppTheme {
            MainScreen(appState)
        }
    }
}

@Composable
private fun rememberAppState(
    windowState: WindowState = WindowState(width = 1024.dp, height = 768.dp)
): AppState {
    val coroutineScope = rememberCoroutineScope()
    return remember(windowState, coroutineScope) {
        val state = AppState(windowState, coroutineScope)
        state.setTrackList(SampleData.sampleTracks)
        if (SampleData.sampleTracks.isNotEmpty()) {
            state.selectTrack(SampleData.sampleTracks[0])
        }
        state
    }
}
