package com.example.mapdemo.presentation

import com.example.mapdemo.domain.model.TrackingInfo
import com.example.mapdemo.domain.usecase.FilterTrackingInfoUseCase
import com.example.mapdemo.domain.usecase.GetTrackingInfoUseCase
import com.example.mapdemo.domain.usecase.SortOrder
import com.example.mapdemo.domain.usecase.SortTrackingInfoUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class TrackMonitorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `load updates state with sorted items`() = runTest {
        val older = tracking("X 1", "2025-12-17T14:39:20.984Z")
        val newer = tracking("X 2", "2025-12-17T15:39:20.984Z")

        val vm = TrackMonitorViewModel(
            getTrackingInfoUseCase = GetTrackingInfoUseCase(FakeRepo(Result.success(listOf(older, newer)))),
            filterTrackingInfoUseCase = FilterTrackingInfoUseCase(),
            sortTrackingInfoUseCase = SortTrackingInfoUseCase(),
        )

        vm.load()

        val state = vm.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(listOf(newer, older), state.items)
    }

    @Test
    fun `query filters visible items`() = runTest {
        val a = tracking("X 19599", "2025-12-17T14:39:20.984Z")
        val b = tracking("O 41291", "2025-12-17T15:39:20.984Z")

        val vm = TrackMonitorViewModel(
            getTrackingInfoUseCase = GetTrackingInfoUseCase(FakeRepo(Result.success(listOf(a, b)))),
            filterTrackingInfoUseCase = FilterTrackingInfoUseCase(),
            sortTrackingInfoUseCase = SortTrackingInfoUseCase(),
        )

        vm.load()
        vm.onQueryChanged("19599")

        val visible = vm.visibleItems(vm.uiState.value)
        assertEquals(listOf(a), visible)
    }

    @Test
    fun `toggle sort changes order`() = runTest {
        val older = tracking("X 1", "2025-12-17T14:39:20.984Z")
        val newer = tracking("X 2", "2025-12-17T15:39:20.984Z")

        val vm = TrackMonitorViewModel(
            getTrackingInfoUseCase = GetTrackingInfoUseCase(FakeRepo(Result.success(listOf(older, newer)))),
            filterTrackingInfoUseCase = FilterTrackingInfoUseCase(),
            sortTrackingInfoUseCase = SortTrackingInfoUseCase(),
        )

        vm.load()
        assertEquals(SortOrder.DESC, vm.uiState.value.sortOrder)
        assertEquals(listOf(newer, older), vm.uiState.value.items)

        vm.onToggleSort()
        assertEquals(SortOrder.ASC, vm.uiState.value.sortOrder)
        assertEquals(listOf(older, newer), vm.uiState.value.items)
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

    private class FakeRepo(
        private val result: Result<List<TrackingInfo>>,
    ) : com.example.mapdemo.domain.repository.TrackingRepository {
        override suspend fun getTrackingInfo(): Result<List<TrackingInfo>> = result
    }
}
