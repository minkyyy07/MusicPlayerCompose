package com.example.musicplayer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musicplayer.MusicTrack
import androidx.compose.foundation.clickable
import androidx.compose.material.Card

@Composable
fun TrackItem(
    track: MusicTrack,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.h6
            )
            Text(
                text = track.artist,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
            if (track.album.isNotEmpty()) {
                Text(
                    text = track.album,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
fun SearchScreen(
    availableTracks: List<MusicTrack> = emptyList(),
    onTrackSelected: (MusicTrack) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<MusicTrack>>(emptyList()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                searchResults = if (searchQuery.isNotEmpty()) {
                    availableTracks.filter { track ->
                        track.title.contains(searchQuery, ignoreCase = true) ||
                                track.artist.contains(searchQuery, ignoreCase = true) ||
                                track.album.contains(searchQuery, ignoreCase = true)
                    }
                } else {
                    emptyList()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(searchResults) { track ->
                TrackItem(
                    track = track,
                    onClick = {
                        onTrackSelected(track)
                    }
                )
                Divider()
            }
        }
    }
}