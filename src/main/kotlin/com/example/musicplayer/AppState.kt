package com.example.musicplayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.WindowState
import com.example.musicplayer.audio.SimpleMp3Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.net.URL
import javax.sound.sampled.AudioSystem
import java.io.FileInputStream

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

    private val _trackList = MutableStateFlow<List<MusicTrack>>(emptyList())
    val trackList: StateFlow<List<MusicTrack>> = _trackList

    private var clip: Any? = null
    private val mp3Player = SimpleMp3Player()

    // Добавляем доступ к состоянию MP3 плеера
    val playerPosition = mp3Player.position
    val playerDuration = mp3Player.duration
    val playerIsPlaying = mp3Player.isPlaying

    fun setTrackList(tracks: List<MusicTrack>) {
        _trackList.value = tracks
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun playPause() {
        try {
            val currentTrack = _currentTrack.value
            if (currentTrack != null && (currentTrack.filePath.startsWith("http") || currentTrack.filePath.lowercase().endsWith(".mp3"))) {
                // MP3 плеер
                if (mp3Player.isPlaying.value) {
                    mp3Player.pause()
                    _isPlaying.value = false
                } else {
                    mp3Player.play()
                    _isPlaying.value = true
                }
            } else {
                // Стандартный клип для других форматов
                val currentClip = clip as? javax.sound.sampled.Clip
                if (currentClip != null) {
                    if (_isPlaying.value) {
                        currentClip.stop()
                        _isPlaying.value = false
                    } else {
                        currentClip.start()
                        _isPlaying.value = true
                    }
                } else {
                    // Если нет активного плеера, попробуем воспроизвести текущий трек
                    currentTrack?.let { track ->
                        playTrack(track)
                    }
                }
            }
        } catch (e: Exception) {
            println("Ошибка управления воспроизведением: ${e.message}")
        }
    }

    fun seekTo(positionMs: Long) {
        mp3Player.seekTo(positionMs)
    }

    fun selectTrack(track: MusicTrack) {
        _currentTrack.value = track
        _currentScreen.value = Screen.Player
        playTrack(track)
    }

    fun playNextTrack() {
        val tracks = _trackList.value
        val currentTrack = _currentTrack.value
        if (tracks.isNotEmpty() && currentTrack != null) {
            val currentIndex = tracks.indexOf(currentTrack)
            if (currentIndex != -1 && currentIndex < tracks.size - 1) {
                selectTrack(tracks[currentIndex + 1])
            }
        }
    }

    fun playPreviousTrack() {
        val tracks = _trackList.value
        val currentTrack = _currentTrack.value
        if (tracks.isNotEmpty() && currentTrack != null) {
            val currentIndex = tracks.indexOf(currentTrack)
            if (currentIndex > 0) {
                selectTrack(tracks[currentIndex - 1])
            }
        }
    }

    fun playTrack(track: MusicTrack) {
        coroutineScope.launch {
            try {
                println("Попытка воспроизвести трек: ${track.title}")
                println("Путь к файлу: ${track.filePath}")

                // Остановить предыдущий трек если играет
                stopPlayback()

                when {
                    track.filePath.startsWith("http://") || track.filePath.startsWith("https://") -> {
                        println("Загрузка MP3 по URL...")
                        if (mp3Player.loadTrack(track.filePath)) {
                            mp3Player.play()
                            _isPlaying.value = true
                            println("Воспроизведение URL MP3 началось")
                        } else {
                            throw Exception("Не удалось загрузить MP3 по URL")
                        }
                    }
                    track.filePath.lowercase().endsWith(".mp3") -> {
                        println("Загрузка локального MP3 файла...")
                        if (mp3Player.loadTrack(track.filePath)) {
                            mp3Player.play()
                            _isPlaying.value = true
                            println("Воспроизведение локального MP3 началось")
                        } else {
                            throw Exception("Не удалось загрузить локальный MP3")
                        }
                    }
                    else -> {
                        println("Загрузка других форматов...")
                        val audioFile = File(track.filePath)
                        if (!audioFile.exists()) {
                            throw Exception("Файл не найден: ${track.filePath}")
                        }

                        val audioStream = AudioSystem.getAudioInputStream(audioFile)
                        println("Аудиопоток получен, создаём клип...")
                        val newClip = AudioSystem.getClip()
                        newClip.open(audioStream)

                        println("Запускаем воспроизведение...")
                        newClip.start()
                        this@AppState.clip = newClip
                        _isPlaying.value = true
                        println("Воспроизведение началось успешно!")
                    }
                }

            } catch (e: Exception) {
                println("Ошибка воспроизведения: ${e.message}")
                e.printStackTrace()
                _isPlaying.value = false
            }
        }
    }

    fun stopPlayback() {
        try {
            // Остановить MP3 плеер
            mp3Player.stop()

            // Остановить стандартный клип
            (clip as? javax.sound.sampled.Clip)?.let {
                it.stop()
                it.close()
                clip = null
            }

            _isPlaying.value = false
        } catch (e: Exception) {
            println("Ошибка остановки воспроизведения: ${e.message}")
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