package com.example.musicplayer.data.deezer

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
 * Поиск треков через Deezer API (публичный, без ключа для базовых запросов).
 * Документация: https://developers.deezer.com/api/search
 * Замечание: preview (30 сек) доступен в поле preview.
 */
object DeezerApi {
    private val json = Json { ignoreUnknownKeys = true }

    private val client: HttpClient by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) { json(json) }
        }
    }

    suspend fun searchTracks(query: String, limit: Int = 25): List<DeezerTrack> {
        if (query.isBlank()) return emptyList()
        val encoded = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8)
        val url = "https://api.deezer.com/search?q=$encoded&limit=$limit"
        val response: DeezerSearchResponse = client.get(url).body()
        return response.data
    }
}

@Serializable
data class DeezerSearchResponse(
    @SerialName("data") val data: List<DeezerTrack> = emptyList(),
    @SerialName("total") val total: Int? = null,
    @SerialName("next") val next: String? = null
)

@Serializable
data class DeezerTrack(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String? = null,
    @SerialName("duration") val durationSec: Long? = null,
    @SerialName("preview") val previewUrl: String? = null,
    @SerialName("artist") val artist: DeezerArtist? = null,
    @SerialName("album") val album: DeezerAlbum? = null
)

@Serializable
data class DeezerArtist(@SerialName("name") val name: String? = null)

@Serializable
data class DeezerAlbum(
    @SerialName("title") val title: String? = null,
    @SerialName("cover_medium") val coverMedium: String? = null,
    @SerialName("cover_big") val coverBig: String? = null
)

// Маппер в доменную модель
fun DeezerTrack.toAppMusicTrack(): AppMusicTrack = AppMusicTrack(
    id = id,
    title = title ?: "Unknown",
    artist = artist?.name ?: "Unknown",
    album = album?.title ?: "",
    duration = (durationSec ?: 0L) * 1000L,
    filePath = previewUrl ?: "",
    coverArtPath = album?.coverBig ?: album?.coverMedium
)

