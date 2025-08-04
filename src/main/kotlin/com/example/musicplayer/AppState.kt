package com.example.musicplayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.sound.sampled.AudioSystem

@Stable
class AppState(
    val windowState: WindowState,
    private val coroutineScope: CoroutineScope
) {
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Player)
    val currentScreen: StateFlow<Screen> = _currentScreen
    
    private val _currentTrack = MutableStateFlow<MusicTrack?>(null)
    val currentTrack: StateFlow<MusicTrack?> = _currentTrack
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    
    private var clip: Any? = null
    
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }
    
    fun playPause() {
        _isPlaying.value = !_isPlaying.value
        // TODO: Implement actual play/pause logic
    }
    
    fun selectTrack(track: MusicTrack) {
        _currentTrack.value = track
        playTrack(track)
    }

    fun playNextTrack() {
        currentTrackList?.let { tracks ->
            val currentIndex = tracks.indexOf(currentTrack.value)
            if (currentIndex != -1 && currentIndex < tracks.size -1) {
                selectTrack(tracks[currentIndex + 1])
            }
        }
    }

    fun playPreviousTrack() {
        currentTrackList?.let { tracks ->
            val currentIndex = tracks.indexOf(currentTrack.value)
            if (currentIndex > 0) {
                selectTrack(tracks[currentIndex - 1])
            }
        }
    }
    
    private fun playTrack(track: MusicTrack) {
        coroutineScope.launch {
            try {
                val audioFile = File(track.filePath)
                val audioStream = AudioSystem.getAudioInputStream(audioFile)
                val clip = AudioSystem.getClip()
                clip.open(audioStream)
                clip.start()
                this@AppState.clip = clip
                _isPlaying.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error (e.g., show error message to user)
            }
        }
    }
    
    fun stopPlayback() {
        (clip as? javax.sound.sampled.Clip)?.let {
            it.stop()
            it.close()
            clip = null
            _isPlaying.value = false
        }
    }
}

@Composable
fun rememberAppState(
    windowState: WindowState = WindowState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): AppState {
    return remember(windowState, coroutineScope) {
        AppState(windowState, coroutineScope)
    }
}

data class MusicTrack(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val filePath: String,
    val coverArtPath: String? = null
)
