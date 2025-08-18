package com.example.musicplayer.data.itunes

import com.example.musicplayer.data.MusicTrack as AppMusicTrack
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Простое обращение к iTunes Search API (без ключа)
 * Документация: https://developer.apple.com/library/archive/documentation/AudioVideo/Conceptual/iTuneSearchAPI/
 */
object ITunesApi {
    private val json = Json { ignoreUnknownKeys = true }

    private val client: HttpClient by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) { json(json) }
        }
    }

    suspend fun searchTracks(term: String, limit: Int = 25, country: String = "US"): List<ITunesTrack> {
        if (term.isBlank()) return emptyList()
        val encoded = URLEncoder.encode(term.trim(), StandardCharsets.UTF_8)
        val url = "https://itunes.apple.com/search?term=$encoded&entity=song&limit=$limit&country=$country"
        val response: ITunesSearchResponse = client.get(url).body()
        return response.results
    }
}

@Serializable
data class ITunesSearchResponse(
    @SerialName("resultCount") val resultCount: Int = 0,
    @SerialName("results") val results: List<ITunesTrack> = emptyList()
)

@Serializable
data class ITunesTrack(
    @SerialName("trackId") val trackId: Long? = null,
    @SerialName("trackName") val trackName: String? = null,
    @SerialName("artistName") val artistName: String? = null,
    @SerialName("collectionName") val collectionName: String? = null,
    @SerialName("previewUrl") val previewUrl: String? = null,
    @SerialName("artworkUrl100") val artworkUrl100: String? = null,
    @SerialName("trackTimeMillis") val trackTimeMillis: Long? = null
)

// Маппер
fun ITunesTrack.toAppMusicTrack(): AppMusicTrack = AppMusicTrack(
    id = (trackId ?: 0L).toString(),
    title = trackName ?: "Unknown",
    artist = artistName ?: "Unknown",
    album = collectionName ?: "",
    duration = trackTimeMillis ?: 0L,
    filePath = previewUrl ?: "",
    coverArtPath = artworkUrl100 ?: ""
)
