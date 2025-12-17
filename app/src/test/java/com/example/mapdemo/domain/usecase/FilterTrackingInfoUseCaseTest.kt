package com.example.mapdemo.domain.usecase

import com.example.mapdemo.domain.model.TrackingInfo
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class FilterTrackingInfoUseCaseTest {

    private val useCase = FilterTrackingInfoUseCase()

    @Test
    fun `returns all items when query blank`() {
        val items = listOf(
            tracking("X 1", "John Doe", "Dubai"),
            tracking("X 2", "Jane Doe", "Sharjah"),
        )

        val result = useCase(items, "   ")

        assertEquals(items, result)
    }

    @Test
    fun `filters by plate number`() {
        val items = listOf(
            tracking("X 19599", "Wyatt Liam", "Rolla"),
            tracking("O 41291", "William Noah", "Jebel Hafeet"),
        )

        val result = useCase(items, "19599")

        assertEquals(listOf(items[0]), result)
    }

    @Test
    fun `filters by driver name case-insensitive`() {
        val items = listOf(
            tracking("X 1", "Wyatt Liam", "Rolla"),
            tracking("X 2", "William Noah", "Jebel Hafeet"),
        )

        val result = useCase(items, "wyatt")

        assertEquals(listOf(items[0]), result)
    }

    private fun tracking(plate: String, driver: String, location: String): TrackingInfo {
        return TrackingInfo(
            plateNo = plate,
            driverName = driver,
            lat = 25.0,
            lng = 55.0,
            location = location,
            imageUrl = "https://example.com/x.jpg",
            lastUpdated = Instant.parse("2025-12-17T14:39:20.984Z"),
        )
    }
}
