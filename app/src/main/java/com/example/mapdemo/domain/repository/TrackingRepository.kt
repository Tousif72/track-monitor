package com.example.mapdemo.domain.repository

import com.example.mapdemo.domain.model.TrackingInfo

interface TrackingRepository {
    suspend fun getTrackingInfo(): Result<List<TrackingInfo>>
}
