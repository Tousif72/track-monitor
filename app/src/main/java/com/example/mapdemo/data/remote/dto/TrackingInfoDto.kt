package com.example.mapdemo.data.remote.dto

import com.squareup.moshi.Json

data class TrackingInfoDto(
    @Json(name = "plateNo") val plateNo: String,
    @Json(name = "driverName") val driverName: String,
    @Json(name = "lat") val lat: Double,
    @Json(name = "lng") val lng: Double,
    @Json(name = "location") val location: String,
    @Json(name = "imageURL") val imageUrl: String,
    @Json(name = "lastUpdated") val lastUpdated: String,
)
