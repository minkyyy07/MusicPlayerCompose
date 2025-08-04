package com.example.musicplayer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlaybackProgressBar(
    currentPosition: Float,
    duration: Float,
    onPositionChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Slider(
            value = currentPosition,
            onValueChange = onPositionChange,
            valueRange = 0f..duration.coerceAtLeast(1f),
            colors = SliderDefaults.colors(thumbColor = MaterialTheme.colors.primary)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentPosition.toInt()),
                style = MaterialTheme.typography.caption
            )
            Text(
                text = formatTime(duration.toInt()),
                style = MaterialTheme.typography.caption
            )
        }
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%d:%02d".format(minutes, remainingSeconds)
}