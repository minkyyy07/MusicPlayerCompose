import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.Image
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.skia.Image
import java.io.File
import javax.imageio.ImageIO
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.swing.JFileChooser
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

// Функция для загрузки PNG-изображения в качестве фона (доступна только в коде)
fun loadBackgroundImage(path: String): ImageBitmap? {
    return try {
        val bufferedImage = ImageIO.read(File(path))
        bufferedImage?.let { img ->
            val baos = java.io.ByteArrayOutputStream()
            ImageIO.write(img, "png", baos)
            val bytes = baos.toByteArray()
            Image.makeFromEncoded(bytes).toComposeImageBitmap()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun TopBar(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(25.dp)
            )
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val items = listOf("Player", "Search", "Library", "Settings")
        items.forEach { item ->
            Button(
                onClick = { onSelect(item) },
                modifier = Modifier.padding(horizontal = 4.dp),
                enabled = true,
                shape = RoundedCornerShape(15.dp),
                elevation = null,
                colors = androidx.compose.material.ButtonDefaults.buttonColors(
                    backgroundColor = if (selected == item) Color.Gray.copy(alpha = 0.3f) else Color.Transparent,
                    contentColor = Color.Black
                )
            ) {
                Text(item)
            }
        }
    }
}

@Composable
@Preview
fun App() {
    var selectedTab by remember { mutableStateOf("Player") }
    var isAuthorized by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var searchResults = remember { mutableStateListOf<String>() }
    var playingTrack by remember { mutableStateOf<String?>(null) }
    var fileName by remember { mutableStateOf<String?>(null) }
    var clip by remember { mutableStateOf<Clip?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var duration by remember { mutableStateOf(1f) }

    // Состояние для фонового изображения (не используется в UI, только в коде)
    var backgroundImage by remember { mutableStateOf<ImageBitmap?>(null) }

    // Пример использования: раскомментируйте строку ниже и укажите путь к PNG-файлу
    //LaunchedEffect(Unit) { backgroundImage = loadBackgroundImage("") }

    LaunchedEffect(clip, isPlaying) {
        while (isPlaying && clip != null) {
            progress = clip!!.microsecondPosition / 1_000_000f
            duration = clip!!.microsecondLength / 1_000_000f
            kotlinx.coroutines.delay(200)
        }
    }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            // Отображение фонового изображения, если оно загружено
            backgroundImage?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = "Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopBar(selected = selectedTab, onSelect = { selectedTab = it })
                Spacer(Modifier.height(24.dp))
                when (selectedTab) {
                    "Player" -> {
                        if (!isAuthorized) {
                            Button(onClick = {
                                // Здесь должна быть реализация OAuth авторизации Spotify
                                // Для примера просто эмулируем успешную авторизацию
                                isAuthorized = true
                            }) { Text("Войти через Spotify") }
                        } else {
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                singleLine = true
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = {
                                // Пример запроса к Spotify API (реальный токен и обработка нужны)
                                // Здесь только эмуляция поиска
                                searchResults.clear()
                                if (searchQuery.isNotBlank()) {
                                    searchResults.addAll(listOf(
                                        "Spotify Track 1: $searchQuery",
                                        "Spotify Track 2: $searchQuery",
                                        "Spotify Track 3: $searchQuery"
                                    ))
                                }
                            }) { Text("Искать в Spotify") }
                            Spacer(Modifier.height(16.dp))
                            if (searchResults.isNotEmpty()) {
                                Text("Результаты поиска:")
                                Spacer(Modifier.height(8.dp))
                                searchResults.forEach { track ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(track, modifier = Modifier.weight(1f))
                                        Button(onClick = { playingTrack = track }) {
                                            Text("Воспроизвести")
                                        }
                                    }
                                    Spacer(Modifier.height(4.dp))
                                }
                            }
                            if (playingTrack != null) {
                                Spacer(Modifier.height(16.dp))
                                Text("Сейчас играет: $playingTrack")
                                // Здесь должна быть интеграция с Spotify Playback SDK
                            }
                        }
                    }
                    else -> {
                        Text("Раздел '$selectedTab' пока не реализован")
                    }
                }
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

