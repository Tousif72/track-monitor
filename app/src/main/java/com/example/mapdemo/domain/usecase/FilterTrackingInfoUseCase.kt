package com.example.mapdemo.domain.usecase

import com.example.mapdemo.domain.model.TrackingInfo

class FilterTrackingInfoUseCase {
    operator fun invoke(items: List<TrackingInfo>, query: String): List<TrackingInfo> {
        val q = query.trim()
        if (q.isEmpty()) return items

        return items.filter {
            it.plateNo.contains(q, ignoreCase = true) ||
                it.driverName.contains(q, ignoreCase = true) ||
                it.location.contains(q, ignoreCase = true)
        }
    }
}
