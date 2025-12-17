package com.example.mapdemo.domain.usecase

import com.example.mapdemo.domain.model.TrackingInfo
import com.example.mapdemo.domain.repository.TrackingRepository

class GetTrackingInfoUseCase(
    private val repository: TrackingRepository,
) {
    suspend operator fun invoke(): Result<List<TrackingInfo>> = repository.getTrackingInfo()
}
