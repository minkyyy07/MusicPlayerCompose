package com.example.musicplayer.util

import java.util.concurrent.TimeUnit

object FormatUtils {
    /**
     * Format milliseconds to a human-readable time string (mm:ss)
     */
    fun formatDuration(milliseconds: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - 
                     TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
        return String.format("%02d:%02d", minutes, seconds)
    }
    
    /**
     * Format file size in bytes to a human-readable string
     */
    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
        return String.format(
            "%.1f %s", 
            bytes / Math.pow(1024.0, digitGroups.toDouble()), 
            units[digitGroups]
        )
    }
}
