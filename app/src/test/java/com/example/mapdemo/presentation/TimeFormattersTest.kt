package com.example.mapdemo.presentation

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class TimeFormattersTest {

    @Test
    fun `formatRelativeTime returns just now for under minute`() {
        val now = Instant.parse("2025-12-17T15:00:00.000Z")
        val instant = Instant.parse("2025-12-17T14:59:30.000Z")

        val result = formatRelativeTime(instant, now)

        assertEquals("just now", result)
    }

    @Test
    fun `formatRelativeTime returns minutes`() {
        val now = Instant.parse("2025-12-17T15:00:00.000Z")
        val instant = Instant.parse("2025-12-17T14:55:00.000Z")

        val result = formatRelativeTime(instant, now)

        assertEquals("5 min ago", result)
    }

    @Test
    fun `formatRelativeTime returns days`() {
        val now = Instant.parse("2025-12-17T15:00:00.000Z")
        val instant = Instant.parse("2025-12-15T15:00:00.000Z")

        val result = formatRelativeTime(instant, now)

        assertEquals("2 days ago", result)
    }
}
