package com.example.musicplayer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicplayer.MusicTrack

@Composable
fun AlbumCover(track: MusicTrack, modifier: Modifier = Modifier) {
    val imageUrl = track.albumArtUrl ?: "https://via.placeholder.com/400?text=${track.title}"

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = "${track.album} cover",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colors.primary.copy(alpha = 0.2f)),
        error = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = track.title.take(2).uppercase(),
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.primary
                )
            }
        }
    )
}