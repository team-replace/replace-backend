package com.app.replace.ui.response

import com.app.replace.domain.Coordinate

data class CoordinatesResponse(val diaryCoordinates: List<CoordinateResponse>) {
    companion object {
        fun from(coordinates: List<Coordinate>) : CoordinatesResponse {
            return CoordinatesResponse(coordinates.map { coordinate -> CoordinateResponse.from(coordinate) }.toList())
        }
    }
}

data class CoordinateResponse(
    val longitude: String,
    val latitude: String
) {
    companion object {
        fun from(coordinate: Coordinate): CoordinateResponse {
            return CoordinateResponse(
                coordinate.longitude.toPlainString(),
                coordinate.latitude.toPlainString()
            )
        }
    }
}
