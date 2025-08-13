package com.example.musicplayer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musicplayer.MusicTrack
import androidx.compose.foundation.clickable
import androidx.compose.material.Card
import com.example.musicplayer.data.deezer.DeezerApi
import com.example.musicplayer.data.deezer.toAppMusicTrack
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    var online by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { newValue ->
                    searchQuery = newValue
                    error = null
                    debounceJob?.cancel()
                    if (searchQuery.isBlank()) {
                        searchResults = emptyList()
                    } else {
                        debounceJob = scope.launch {
                            delay(400)
                            if (online) {
                                isLoading = true
                                searchResults = runCatching {
                                    DeezerApi.searchTracks(searchQuery).map { it.toAppMusicTrack() }
                                }.onFailure { error = it.message }.getOrDefault(emptyList())
                                isLoading = false
                            } else {
                                searchResults = availableTracks.filter { track ->
                                    track.title.contains(searchQuery, true) ||
                                            track.artist.contains(searchQuery, true) ||
                                            track.album.contains(searchQuery, true)
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                label = { Text("Search") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (online) "Online" else "Local", style = MaterialTheme.typography.caption)
                Switch(checked = online, onCheckedChange = { checked ->
                    online = checked
                    searchResults = emptyList()
                    if (searchQuery.isNotBlank()) {
                        // перезапустить поиск
                        scope.launch {
                            isLoading = true
                            searchResults = if (online) {
                                runCatching { DeezerApi.searchTracks(searchQuery).map { it.toAppMusicTrack() } }
                                    .onFailure { error = it.message }
                                    .getOrDefault(emptyList())
                            } else {
                                availableTracks.filter { track ->
                                    track.title.contains(searchQuery, true) ||
                                            track.artist.contains(searchQuery, true) ||
                                            track.album.contains(searchQuery, true)
                                }
                            }
                            isLoading = false
                        }
                    }
                })
            }
        }

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        }
        error?.let {
            Text(it, color = MaterialTheme.colors.error, style = MaterialTheme.typography.caption)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(searchResults) { track ->
                TrackItem(
                    track = track,
                    onClick = { onTrackSelected(track) }
                )
                Divider()
            }
        }
    }
}