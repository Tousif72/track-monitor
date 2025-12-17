package com.example.mapdemo.data.remote

import com.example.mapdemo.data.remote.dto.TrackingInfoDto
import retrofit2.http.GET

interface TrackingApi {
    @GET("file/tracking/tracking-info")
    suspend fun getTrackingInfo(): List<TrackingInfoDto>
}
