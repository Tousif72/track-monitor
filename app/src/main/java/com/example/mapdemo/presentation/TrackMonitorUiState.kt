package com.example.mapdemo.presentation

import com.example.mapdemo.domain.model.TrackingInfo
import com.example.mapdemo.domain.usecase.SortOrder

enum class ViewMode {
    LIST,
    MAP,
}

data class TrackMonitorUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val items: List<TrackingInfo> = emptyList(),
    val query: String = "",
    val sortOrder: SortOrder = SortOrder.DESC,
    val viewMode: ViewMode = ViewMode.LIST,
    val selectedOnMap: TrackingInfo? = null,
    val isSearchVisible: Boolean = false,
)
