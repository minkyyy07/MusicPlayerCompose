package com.example.musicplayer.audio

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.net.URL
import java.net.URLConnection
import javazoom.jl.player.Player
import javazoom.jl.decoder.JavaLayerException

/**
 * Улучшенный MP3 плеер для работы с потоковыми URL и локальными файлами
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
    private var audioData: ByteArray? = null

    fun loadTrack(filePath: String): Boolean {
        return try {
            stop()
            currentFilePath = filePath
            audioData = null

            // Предзагружаем данные для URL
            if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
                println("Предзагрузка аудио данных с URL...")
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val connection = URL(filePath).openConnection()
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0")
                        connection.connectTimeout = 10000
                        connection.readTimeout = 30000

                        val inputStream = BufferedInputStream(connection.getInputStream())
                        audioData = inputStream.readBytes()
                        inputStream.close()

                        _duration.value = 30000L // 30 секунд для preview треков
                        println("Аудио данные загружены: ${audioData?.size} байт")
                    } catch (e: Exception) {
                        println("Ошибка предзагрузки: ${e.message}")
                    }
                }
            } else {
                // Локальный файл
                _duration.value = try {
                    val file = File(filePath)
                    if (file.exists()) {
                        val fileSizeBytes = file.length()
                        (fileSizeBytes * 8) / (128 * 1000 / 8) * 1000
                    } else {
                        180000L // 3 минуты по умолчанию
                    }
                } catch (e: Exception) {
                    180000L
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

                // Создаем входной поток
                val inputStream = when {
                    filePath.startsWith("http://") || filePath.startsWith("https://") -> {
                        if (audioData != null) {
                            println("Используем предзагруженные данные")
                            BufferedInputStream(ByteArrayInputStream(audioData))
                        } else {
                            println("Потоковое воспроизведение с URL")
                            val connection = URL(filePath).openConnection()
                            connection.setRequestProperty("User-Agent", "Mozilla/5.0")
                            connection.connectTimeout = 10000
                            connection.readTimeout = 30000
                            BufferedInputStream(connection.getInputStream())
                        }
                    }
                    else -> {
                        val file = File(filePath)
                        if (!file.exists()) throw Exception("Файл не найден: $filePath")
                        BufferedInputStream(FileInputStream(file))
                    }
                }

                // Создаем плеер с обработкой ошибок
                player = try {
                    Player(inputStream)
                } catch (e: JavaLayerException) {
                    println("Ошибка создания плеера JLayer: ${e.message}")
                    inputStream.close()
                    throw e
                }

                println("Плеер создан успешно, начинаем воспроизведение...")

                // Обновляем позицию во время воспроизведения
                val positionUpdateJob = launch {
                    while (_isPlaying.value && isActive) {
                        val elapsed = System.currentTimeMillis() - startTime
                        _position.value = elapsed.coerceAtMost(_duration.value)
                        delay(100)
                    }
                }

                try {
                    // Воспроизведение (блокирующий вызов)
                    player?.play()
                    println("Воспроизведение завершено")
                } catch (e: JavaLayerException) {
                    println("Ошибка во время воспроизведения: ${e.message}")
                    // Продолжаем, не прерывая весь процесс
                } catch (e: Exception) {
                    println("Общая ошибка воспроизведения: ${e.message}")
                } finally {
                    positionUpdateJob.cancel()
                }

            } catch (e: Exception) {
                println("Критическая ошибка воспроизведения: ${e.message}")
                e.printStackTrace()
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
        // Простое seeking - перезапуск с начала
        pausedPosition = 0L
        _position.value = 0L

        if (_isPlaying.value) {
            stop()
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                play()
            }
        }
    }

    fun release() {
        stop()
        audioData = null
        currentFilePath = null
    }
}
