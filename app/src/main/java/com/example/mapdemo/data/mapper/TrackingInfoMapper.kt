package com.example.mapdemo.data.mapper

import com.example.mapdemo.data.remote.dto.TrackingInfoDto
import com.example.mapdemo.domain.model.TrackingInfo
import java.time.Instant

fun TrackingInfoDto.toDomain(): TrackingInfo {
    return TrackingInfo(
        plateNo = plateNo,
        driverName = driverName,
        lat = lat,
        lng = lng,
        location = location,
        imageUrl = imageUrl,
        lastUpdated = Instant.parse(lastUpdated),
    )
}
