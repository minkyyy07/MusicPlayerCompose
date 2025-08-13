package com.example.musicplayer.data.jamendo

import com.example.musicplayer.MusicTrack as AppMusicTrack
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Jamendo API для получения полных треков с Creative Commons лицензией
 * Регистрация: https://developer.jamendo.com/
 */
object JamendoApi {
    private val json = Json { ignoreUnknownKeys = true }

    private val client: HttpClient by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) { json(json) }
        }
    }

    // Получить бесплатный client_id: https://developer.jamendo.com/
    private const val CLIENT_ID = "56d30c95" // Демо ключ

    suspend fun searchTracks(query: String, limit: Int = 10): List<JamendoTrack> {
        if (query.isBlank()) return emptyList()
        val encoded = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8)
        val url = "https://api.jamendo.com/v3.0/tracks/?client_id=$CLIENT_ID&format=json&limit=$limit&search=$encoded&include=musicinfo&audioformat=mp32"

        return try {
            val response: JamendoResponse = client.get(url).body()
            response.results
        } catch (e: Exception) {
            println("Ошибка Jamendo API: ${e.message}")
            emptyList()
        }
    }

    suspend fun getPopularTracks(limit: Int = 20): List<JamendoTrack> {
        val url = "https://api.jamendo.com/v3.0/tracks/?client_id=$CLIENT_ID&format=json&limit=$limit&order=popularity_total&include=musicinfo&audioformat=mp32"

        return try {
            val response: JamendoResponse = client.get(url).body()
            response.results
        } catch (e: Exception) {
            println("Ошибка получения популярных треков: ${e.message}")
            emptyList()
        }
    }
}

@Serializable
data class JamendoResponse(
    @SerialName("results") val results: List<JamendoTrack> = emptyList()
)

@Serializable
data class JamendoTrack(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("duration") val duration: Int? = null, // в секундах
    @SerialName("artist_name") val artistName: String,
    @SerialName("album_name") val albumName: String? = null,
    @SerialName("audio") val audio: String? = null, // URL полного трека
    @SerialName("audiodownload") val audioDownload: String? = null,
    @SerialName("image") val image: String? = null
)

// Маппер в доменную модель
fun JamendoTrack.toAppMusicTrack(): AppMusicTrack = AppMusicTrack(
    id = id,
    title = name,
    artist = artistName,
    album = albumName ?: "",
    duration = (duration ?: 0) * 1000L, // секунды -> миллисекунды
    filePath = audio ?: audioDownload ?: "", // Полный MP3 файл!
    coverArtPath = image
)
