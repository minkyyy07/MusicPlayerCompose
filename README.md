# Music Player Compose

A modern music player application built with Jetpack Compose for Desktop.

## Features

- 🎵 Play local music files
- 🎨 Modern Material Design UI
- 🔍 Search functionality
- 📚 Music library management
- ⚙️ Settings and preferences

## Requirements

- JDK 11 or higher
- IntelliJ IDEA or Android Studio with Compose plugin

## Getting Started

1. Clone the repository
2. Open the project in IntelliJ IDEA or Android Studio
3. Wait for Gradle to sync and download dependencies
4. Run the `main` function in `Main.kt`

## Project Structure

```
src/main/kotlin/com/example/musicplayer/
├── Main.kt                 # Application entry point
├── AppState.kt             # Application state management
├── data/
│   └── SampleData.kt       # Sample music data
├── ui/
│   ├── screens/            # Screen components
│   │   ├── MainScreen.kt
│   │   ├── PlayerScreen.kt
│   │   ├── SearchScreen.kt
│   │   ├── LibraryScreen.kt
│   │   └── SettingsScreen.kt
│   ├── components/         # Reusable UI components
│   │   ├── TopBar.kt
│   │   └── BottomPlayerBar.kt
│   └── theme/              # Theme and styling
│       ├── Theme.kt
│       ├── Type.kt
│       └── Shapes.kt
└── util/
    └── FormatUtils.kt      # Utility functions
```

## Dependencies

- Jetpack Compose for Desktop
- Coil for image loading
- Kotlin Coroutines
- JVM Audio for audio playback

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
