import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.compose") version "1.5.0"
    kotlin("plugin.serialization") version "1.9.0"
}

group = "com.example.musicplayer"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note: To avoid potential version conflicts, we're using only Compose Desktop dependencies
    // and avoiding AndroidX Compose dependencies in a desktop project
    
    // Compose Desktop
    implementation(compose.desktop.currentOs)
    
    // Material Icons Extended
    implementation(compose.materialIconsExtended)
    
    // Coroutines for Compose Desktop
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
    
    // Image loading for Compose Desktop
    implementation("io.github.skeptick.libres:libres-compose:1.1.3")
    
    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.8")
    
    // Ktor for API calls + kotlinx.serialization
    implementation("io.ktor:ktor-client-cio:2.3.5")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // MP3 support for audio playback - только JLayer
    implementation("javazoom:jlayer:1.0.1")

    // Test
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.5")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
    
    // Enable experimental features
    sourceSets.all {
        languageSettings.apply {
            optIn("kotlin.RequiresOptIn")
            languageVersion = "1.9"
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all"
        )
    }
}

compose.desktop {
    application {
        mainClass = "com.example.musicplayer.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "MusicPlayer"
            packageVersion = "1.0.0"
            
            windows {
                menu = true
                upgradeUuid = "123e4567-e89b-12d3-a456-426614174000"
            }
            
            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))
        }
    }
}
