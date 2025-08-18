package com.example.musicplayer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.example.musicplayer.MusicTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import javax.imageio.ImageIO
import org.jetbrains.skia.Image as SkiaImage

@Composable
fun AlbumCover(track: MusicTrack, modifier: Modifier = Modifier) {
    var imageBitmap by remember(track.coverArtPath) { mutableStateOf<ImageBitmap?>(null) }
    var loadError by remember(track.coverArtPath) { mutableStateOf(false) }

    LaunchedEffect(track.coverArtPath) {
        val path = track.coverArtPath
        if (path.isNullOrBlank()) {
            loadError = true
            return@LaunchedEffect
        }
        val buffered = withContext(Dispatchers.IO) {
            runCatching {
                when {
                    path.startsWith("http://") || path.startsWith("https://") -> ImageIO.read(URL(path))
                    else -> ImageIO.read(File(path))
                }
            }.getOrNull()
        }
        if (buffered != null) {
            // Преобразуем BufferedImage в Skia Image, затем в ImageBitmap
            val bytes = java.io.ByteArrayOutputStream().use { baos ->
                javax.imageio.ImageIO.write(buffered, "png", baos)
                baos.toByteArray()
            }
            imageBitmap = SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap()
        } else {
            loadError = true
        }
    }

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colors.primary.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        when {
            imageBitmap != null -> Image(
                bitmap = imageBitmap!!,
                contentDescription = "${track.album} cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            loadError -> Text(
                text = track.title.take(2).uppercase(),
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.primary
            )
            else -> Text(
                text = "…",
                style = MaterialTheme.typography.h4,
                color = MaterialTheme.colors.primary
            )
        }
    }
}
