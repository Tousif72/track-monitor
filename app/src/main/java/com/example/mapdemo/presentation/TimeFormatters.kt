package com.example.mapdemo.presentation

import java.time.Duration
import java.time.Instant

fun formatRelativeTime(instant: Instant, now: Instant = Instant.now()): String {
    val d = Duration.between(instant, now)
    val seconds = d.seconds

    if (seconds < 0) return "just now"
    if (seconds < 60) return "just now"

    val minutes = seconds / 60
    if (minutes < 60) return "$minutes min ago"

    val hours = minutes / 60
    if (hours < 24) return "$hours hours ago"

    val days = hours / 24
    return "$days days ago"
}
