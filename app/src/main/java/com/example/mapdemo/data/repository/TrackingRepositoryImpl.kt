package com.example.mapdemo.data.repository

import com.example.mapdemo.data.mapper.toDomain
import com.example.mapdemo.data.remote.TrackingApi
import com.example.mapdemo.domain.model.TrackingInfo
import com.example.mapdemo.domain.repository.TrackingRepository

class TrackingRepositoryImpl(
    private val api: TrackingApi,
) : TrackingRepository {
    override suspend fun getTrackingInfo(): Result<List<TrackingInfo>> {
        return runCatching {
            api.getTrackingInfo().map { it.toDomain() }
        }
    }
}
