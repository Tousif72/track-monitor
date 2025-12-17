package com.example.mapdemo.domain.usecase

import com.example.mapdemo.domain.model.TrackingInfo
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class SortTrackingInfoUseCaseTest {

    private val useCase = SortTrackingInfoUseCase()

    @Test
    fun `sort desc puts newest first`() {
        val older = tracking("X 1", "2025-12-17T14:39:20.984Z")
        val newer = tracking("X 2", "2025-12-17T15:39:20.984Z")

        val result = useCase(listOf(older, newer), SortOrder.DESC)

        assertEquals(listOf(newer, older), result)
    }

    @Test
    fun `sort asc puts oldest first`() {
        val older = tracking("X 1", "2025-12-17T14:39:20.984Z")
        val newer = tracking("X 2", "2025-12-17T15:39:20.984Z")

        val result = useCase(listOf(newer, older), SortOrder.ASC)

        assertEquals(listOf(older, newer), result)
    }

    private fun tracking(plate: String, ts: String): TrackingInfo {
        return TrackingInfo(
            plateNo = plate,
            driverName = "Driver",
            lat = 25.0,
            lng = 55.0,
            location = "Dubai",
            imageUrl = "https://example.com/x.jpg",
            lastUpdated = Instant.parse(ts),
        )
    }
}
