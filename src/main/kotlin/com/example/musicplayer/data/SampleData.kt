package com.example.musicplayer.data

object SampleData {
    val sampleTracks = listOf(
        MusicTrack(
            id = "1",
            title = "Bohemian Rhapsody",
            artist = "Queen",
            album = "A Night at the Opera",
            duration = 354000, // 5:54 in milliseconds
            filePath = "/path/to/bohemian_rhapsody.mp3",
            coverArtPath = "https://upload.wikimedia.org/wikipedia/en/thumb/e/ef/BoRhapUSPromo92.jpg/220px-BoRhapUSPromo92.jpg"
        ),
        MusicTrack(
            id = "2",
            title = "Hotel California",
            artist = "Eagles",
            album = "Hotel California",
            duration = 391000, // 6:31 in milliseconds
            filePath = "/path/to/hotel_california.mp3",
            coverArtPath = "https://upload.wikimedia.org/wikipedia/en/thumb/4/49/Hotelcalifornia.jpg/220px-Hotelcalifornia.jpg"
        ),
        MusicTrack(
            id = "3",
            title = "Stairway to Heaven",
            artist = "Led Zeppelin",
            album = "Led Zeppelin IV",
            duration = 482000, // 8:02 in milliseconds
            filePath = "/path/to/stairway_to_heaven.mp3",
            coverArtPath = "https://upload.wikimedia.org/wikipedia/en/2/26/Led_Zeppelin_-_Led_Zeppelin_IV.jpg"
        )
    )
}
