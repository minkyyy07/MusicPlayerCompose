package com.example.musicplayer.data

/**
 * Data class representing a music track
 * @param id Unique identifier for the track
 * @param title Title of the track
 * @param artist Name of the artist
 * @param album Name of the album
 * @param duration Duration in milliseconds
 * @param filePath Path to the audio file
 * @param coverArtPath URL or path to the cover art image
 */
data class MusicTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val filePath: String,
    val coverArtPath: String
)
