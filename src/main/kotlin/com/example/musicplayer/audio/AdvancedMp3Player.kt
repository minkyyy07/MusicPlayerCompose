package com.example.musicplayer.audio

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.net.URL
import javazoom.jl.player.Player

/**
 * Упрощенный MP3 плеер для стабильного воспроизведения
 */
class SimpleMp3Player {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _position = MutableStateFlow(0L) // позиция в миллисекундах
    val position: StateFlow<Long> = _position

    private val _duration = MutableStateFlow(0L) // длительность в миллисекундах
    val duration: StateFlow<Long> = _duration

    private var player: Player? = null
    private var playbackJob: Job? = null
    private var startTime = 0L
    private var pausedPosition = 0L
    private var currentFilePath: String? = null

    fun loadTrack(filePath: String): Boolean {
        return try {
            stop()
            currentFilePath = filePath

            // Устанавливаем примерную длительность
            _duration.value = when {
                filePath.startsWith("http://") || filePath.startsWith("https://") -> 30000L // 30 сек для preview
                else -> {
                    try {
                        val file = File(filePath)
                        if (file.exists()) {
                            // Приблизительная оценка по размеру файла (128 kbps)
                            val fileSizeBytes = file.length()
                            val estimatedDurationMs = (fileSizeBytes * 8) / (128 * 1000 / 8) * 1000
                            estimatedDurationMs
                        } else {
                            30000L
                        }
                    } catch (e: Exception) {
                        30000L
                    }
                }
            }

            _position.value = 0L
            pausedPosition = 0L

            true
        } catch (e: Exception) {
            println("Ошибка загрузки трека: ${e.message}")
            false
        }
    }

    fun play() {
        if (_isPlaying.value) return

        val filePath = currentFilePath ?: return

        playbackJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                _isPlaying.value = true
                startTime = System.currentTimeMillis() - pausedPosition

                // Создаем новый плеер
                val inputStream = when {
                    filePath.startsWith("http://") || filePath.startsWith("https://") -> {
                        BufferedInputStream(URL(filePath).openStream())
                    }
                    else -> {
                        val file = File(filePath)
                        if (!file.exists()) throw Exception("Файл не найден: $filePath")
                        BufferedInputStream(FileInputStream(file))
                    }
                }

                player = Player(inputStream)

                // Обновляем позицию во время воспроизведения
                val positionUpdateJob = launch {
                    while (_isPlaying.value && isActive) {
                        val elapsed = System.currentTimeMillis() - startTime
                        _position.value = elapsed.coerceAtMost(_duration.value)
                        delay(100) // Обновляем каждые 100мс
                    }
                }

                // Воспроизведение (блокирующий вызов)
                player?.play()

                positionUpdateJob.cancel()

            } catch (e: Exception) {
                println("Ошибка воспроизведения: ${e.message}")
            } finally {
                _isPlaying.value = false
            }
        }
    }

    fun pause() {
        pausedPosition = _position.value
        stop()
    }

    fun stop() {
        _isPlaying.value = false
        playbackJob?.cancel()

        try {
            player?.close()
            player = null
        } catch (e: Exception) {
            println("Ошибка остановки: ${e.message}")
        }
    }

    fun seekTo(positionMs: Long) {
        // Для упрощения - перезапускаем трек с начала
        // В реальном плеере нужно более сложное seeking
        pausedPosition = 0L
        _position.value = 0L

        if (_isPlaying.value) {
            stop()
            // Небольшая задержка перед перезапуском
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                play()
            }
        }
    }
}
