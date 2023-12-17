package com.app.replace.infrastructure

import com.app.replace.domain.Coordinate
import java.math.BigDecimal

data class KakaoPlaceInformationByRoadAddress(
    val meta: Meta,
    val documents: List<Documents>
) {
    fun getCoordinate(): Coordinate {
        return Coordinate(BigDecimal(documents.get(0).x), BigDecimal(documents.get(0).y))
    }
}

data class Documents(
    val address: Map<String, String>,
    val address_name: String,
    val address_type: String,
    val road_address: Map<String, String>,
    val x: String,
    val y: String
)

data class Meta(
    val is_end: Boolean,
    val pageableCount: Int,
    val totalCount: Int
)
