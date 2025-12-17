package com.example.mapdemo.domain.model

import java.time.Instant

data class TrackingInfo(
    val plateNo: String,
    val driverName: String,
    val lat: Double,
    val lng: Double,
    val location: String,
    val imageUrl: String,
    val lastUpdated: Instant,
)
