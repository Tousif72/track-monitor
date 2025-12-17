package com.example.mapdemo.domain.usecase

import com.example.mapdemo.domain.model.TrackingInfo

enum class SortOrder {
    DESC,
    ASC,
}

class SortTrackingInfoUseCase {
    operator fun invoke(items: List<TrackingInfo>, order: SortOrder): List<TrackingInfo> {
        return when (order) {
            SortOrder.DESC -> items.sortedByDescending { it.lastUpdated }
            SortOrder.ASC -> items.sortedBy { it.lastUpdated }
        }
    }
}
